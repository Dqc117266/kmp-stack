package com.example.recipe.data.repository

import com.dqc.kit.domain.result.DomainResult
import com.example.recipe.data.datasource.local.RecipeLocalDataSource
import com.example.recipe.data.datasource.remote.RecipeRemoteDataSource
import com.example.recipe.data.mapper.RecipeMapper
import com.example.recipe.domain.model.Recipe
import com.example.recipe.domain.model.RecipeFilter
import com.example.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * 食谱仓库实现
 * 
 * 负责：
 * 1. 处理 DataSource 返回的 DomainResult（成功/失败判断）
 * 2. DTO 到领域模型的转换
 * 3. 本地缓存策略
 * 4. 业务逻辑协调
 */
class RecipeRepositoryImpl(
    private val localDataSource: RecipeLocalDataSource,
    private val remoteDataSource: RecipeRemoteDataSource
) : RecipeRepository {

    override fun getRecipes(filter: RecipeFilter): Flow<List<Recipe>> {
        return when (filter) {
            is RecipeFilter.All -> localDataSource.getAllRecipes()
            is RecipeFilter.Favorites -> localDataSource.getFavoriteRecipes()
            is RecipeFilter.Search -> localDataSource.searchRecipes(filter.query)
        }
    }

    override fun getRecipeById(id: String): Flow<Recipe?> {
        return localDataSource.getRecipeById(id)
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return localDataSource.getFavoriteRecipes()
    }

    override suspend fun fetchRecipesFromRemote(): DomainResult<List<Recipe>> {
        return when (val result = remoteDataSource.getRecipes()) {
            is DomainResult.Success -> {
                val recipes = result.data.map { RecipeMapper.toDomain(it) }
                recipes.forEach { recipe ->
                    localDataSource.insertRecipe(recipe)
                }
                DomainResult.Success(recipes)
            }
            is DomainResult.Error -> result
        }
    }

    override suspend fun fetchRecipeDetail(id: String): DomainResult<Recipe> {
        return when (val result = remoteDataSource.getRecipeDetail(id)) {
            is DomainResult.Success -> {
                val recipe = RecipeMapper.toDomain(result.data)
                localDataSource.insertRecipe(recipe)
                DomainResult.Success(recipe)
            }
            is DomainResult.Error -> result
        }
    }

    override suspend fun toggleFavorite(id: String): DomainResult<Boolean> {
        val recipe = localDataSource.getRecipeById(id).first()
            ?: return DomainResult.Error(
                com.dqc.kit.domain.result.DomainError.NotFound("Recipe not found")
            )
        
        val newFavoriteState = !recipe.isFavorite
        return when (val result = localDataSource.toggleFavorite(id, newFavoriteState)) {
            is DomainResult.Success -> DomainResult.Success(newFavoriteState)
            is DomainResult.Error -> result
        }
    }

    override suspend fun syncRecipes(): DomainResult<Unit> {
        val unsyncedRecipes = localDataSource.getUnsyncedRecipes()
        if (unsyncedRecipes.isEmpty()) {
            return DomainResult.Success(Unit)
        }

        val dtoList = unsyncedRecipes.map { RecipeMapper.toDto(it) }
        return when (val result = remoteDataSource.syncRecipes(dtoList)) {
            is DomainResult.Success -> {
                unsyncedRecipes.forEach { recipe ->
                    localDataSource.markAsSynced(recipe.id)
                }
                DomainResult.Success(Unit)
            }
            is DomainResult.Error -> result
        }
    }

    override suspend fun searchRecipes(query: String): DomainResult<List<Recipe>> {
        if (query.isBlank()) {
            return DomainResult.Success(emptyList())
        }

        return when (val result = remoteDataSource.searchRecipes(query)) {
            is DomainResult.Success -> {
                val recipes = result.data.map { RecipeMapper.toDomain(it) }
                DomainResult.Success(recipes)
            }
            is DomainResult.Error -> result
        }
    }
}
