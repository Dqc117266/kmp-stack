package com.example.recipe.data.datasource.local

import com.dqc.kit.domain.result.DomainError
import com.dqc.kit.domain.result.DomainResult
import com.example.recipe.data.IngredientEntity
import com.example.recipe.data.RecipeDatabase
import com.example.recipe.data.RecipeEntity
import com.example.recipe.data.mapper.RecipeMapper
import com.example.recipe.domain.model.Recipe
import com.timelinesolutions.kmpstack.core.database.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * 食谱本地数据源
 */
interface RecipeLocalDataSource {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipeById(id: String): Flow<Recipe?>
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    fun searchRecipes(query: String): Flow<List<Recipe>>
    suspend fun insertRecipe(recipe: Recipe): DomainResult<Unit>
    suspend fun updateRecipe(recipe: Recipe): DomainResult<Unit>
    suspend fun deleteRecipe(id: String): DomainResult<Unit>
    suspend fun toggleFavorite(id: String, isFavorite: Boolean): DomainResult<Unit>
    suspend fun getUnsyncedRecipes(): List<Recipe>
    suspend fun markAsSynced(id: String): DomainResult<Unit>
}

/**
 * 本地数据源实现
 */
class RecipeLocalDataSourceImpl(
    database: RecipeDatabase
) : BaseDataSource<RecipeDatabase>(database), RecipeLocalDataSource {

    private val queries = database.recipeEntityQueries

    private fun mapRecipeEntity(entity: RecipeEntity): Recipe {
        val ingredients = queries.selectIngredientsByRecipe(entity.id).executeAsList()
        return RecipeMapper.toDomain(entity, ingredients)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> = flowList(
        query = queries.selectAll(),
        mapper = ::mapRecipeEntity
    )

    override fun getRecipeById(id: String): Flow<Recipe?> = flowOneOrNull(
        query = queries.selectById(id),
        mapper = ::mapRecipeEntity
    )

    override fun getFavoriteRecipes(): Flow<List<Recipe>> = flowList(
        query = queries.selectFavorites(),
        mapper = ::mapRecipeEntity
    )

    override fun searchRecipes(query: String): Flow<List<Recipe>> = flowList(
        query = queries.searchByTitle(query),
        mapper = ::mapRecipeEntity
    )

    override suspend fun insertRecipe(recipe: Recipe): DomainResult<Unit> = withTransaction {
        try {
            val params = RecipeMapper.toInsertParams(recipe)
            queries.insertRecipe(
                params[0] as String,
                params[1] as String,
                params[2] as String?,
                params[3] as String?,
                params[4] as Long,
                params[5] as Long,
                params[6] as String,
                params[7] as String,
                params[8] as String?,
                params[9] as Long,
                params[10] as Long,
                params[11] as Long,
                params[12] as Long
            )
            // 插入食材
            recipe.ingredients.forEach { ingredient ->
                val iParams = RecipeMapper.toInsertParams(ingredient, recipe.id)
                queries.insertIngredient(
                    iParams[0] as String,
                    iParams[1] as String,
                    iParams[2] as String,
                    iParams[3] as Double,
                    iParams[4] as String
                )
            }
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(DomainError.Unknown(e, "Failed to insert recipe"))
        }
    }

    override suspend fun updateRecipe(recipe: Recipe): DomainResult<Unit> = withTransaction {
        try {
            val params = RecipeMapper.toUpdateParams(recipe)
            queries.updateRecipe(
                params[0] as String,
                params[1] as String?,
                params[2] as String?,
                params[3] as Long,
                params[4] as Long,
                params[5] as String,
                params[6] as String,
                params[7] as String?,
                params[8] as Long,
                params[9] as Long,
                params[10] as Long,
                params[11] as String
            )
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(DomainError.Unknown(e, "Failed to update recipe"))
        }
    }

    override suspend fun deleteRecipe(id: String): DomainResult<Unit> = withTransaction {
        try {
            queries.deleteById(id)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(DomainError.Unknown(e, "Failed to delete recipe"))
        }
    }

    override suspend fun toggleFavorite(id: String, isFavorite: Boolean): DomainResult<Unit> = withTransaction {
        try {
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            queries.toggleFavorite(if (isFavorite) 1L else 0L, now, id)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(DomainError.Unknown(e, "Failed to toggle favorite"))
        }
    }

    override suspend fun getUnsyncedRecipes(): List<Recipe> {
        return withContext(Dispatchers.IO) {
            queries.selectUnsynced().executeAsList().map { entity ->
                val ingredients = queries.selectIngredientsByRecipe(entity.id).executeAsList()
                RecipeMapper.toDomain(entity, ingredients)
            }
        }
    }

    override suspend fun markAsSynced(id: String): DomainResult<Unit> = withTransaction {
        try {
            queries.markAsSynced(id)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(DomainError.Unknown(e, "Failed to mark as synced"))
        }
    }
}
