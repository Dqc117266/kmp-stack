package com.example.recipe.domain.usecase

import com.dqc.kit.domain.result.DomainResult
import com.dqc.kit.domain.usecase.FlowUseCase
import com.dqc.kit.domain.usecase.NoParamsFlowUseCase
import com.dqc.kit.domain.usecase.NoParamsUseCase
import com.dqc.kit.domain.usecase.UseCase
import com.example.recipe.domain.model.Recipe
import com.example.recipe.domain.model.RecipeFilter
import com.example.recipe.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

/**
 * 获取食谱列表用例
 */
class GetRecipesUseCase(
    private val repository: RecipeRepository
) : FlowUseCase<RecipeFilter, List<Recipe>>() {
    override fun execute(params: RecipeFilter): Flow<List<Recipe>> {
        return repository.getRecipes(params)
    }
}

/**
 * 获取食谱详情用例
 */
class GetRecipeDetailUseCase(
    private val repository: RecipeRepository
) : FlowUseCase<String, Recipe?>() {
    override fun execute(params: String): Flow<Recipe?> {
        return repository.getRecipeById(params)
    }
}

/**
 * 获取收藏食谱用例
 */
class GetFavoriteRecipesUseCase(
    private val repository: RecipeRepository
) : NoParamsFlowUseCase<List<Recipe>>() {
    override fun execute(): Flow<List<Recipe>> {
        return repository.getFavoriteRecipes()
    }
}

/**
 * 从远程获取食谱用例
 */
class FetchRecipesUseCase(
    private val repository: RecipeRepository
) : NoParamsUseCase<List<Recipe>>() {
    override suspend fun execute(): DomainResult<List<Recipe>> {
        return repository.fetchRecipesFromRemote()
    }
}

/**
 * 切换收藏状态用例
 */
class ToggleFavoriteUseCase(
    private val repository: RecipeRepository
) : UseCase<String, Boolean>() {
    override suspend fun execute(params: String): DomainResult<Boolean> {
        return repository.toggleFavorite(params)
    }
}

/**
 * 搜索食谱用例
 */
class SearchRecipesUseCase(
    private val repository: RecipeRepository
) : UseCase<String, List<Recipe>>() {
    override suspend fun execute(params: String): DomainResult<List<Recipe>> {
        return repository.searchRecipes(params)
    }
}

/**
 * 同步食谱用例
 */
class SyncRecipesUseCase(
    private val repository: RecipeRepository
) : NoParamsUseCase<Unit>() {
    override suspend fun execute(): DomainResult<Unit> {
        return repository.syncRecipes()
    }
}
