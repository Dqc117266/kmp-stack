package com.example.recipe.domain.repository

import com.dqc.kit.domain.result.DomainResult
import com.example.recipe.domain.model.Recipe
import com.example.recipe.domain.model.RecipeFilter
import kotlinx.coroutines.flow.Flow

/**
 * 食谱仓库接口
 */
interface RecipeRepository {

    /**
     * 获取所有食谱（Flow 实时观察）
     */
    fun getRecipes(filter: RecipeFilter = RecipeFilter.All): Flow<List<Recipe>>

    /**
     * 根据ID获取食谱详情
     */
    fun getRecipeById(id: String): Flow<Recipe?>

    /**
     * 获取收藏的食谱
     */
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    /**
     * 从远程获取食谱列表
     */
    suspend fun fetchRecipesFromRemote(): DomainResult<List<Recipe>>

    /**
     * 根据ID从远程获取食谱详情
     */
    suspend fun fetchRecipeDetail(id: String): DomainResult<Recipe>

    /**
     * 收藏/取消收藏食谱
     */
    suspend fun toggleFavorite(id: String): DomainResult<Boolean>

    /**
     * 同步本地数据到远程
     */
    suspend fun syncRecipes(): DomainResult<Unit>

    /**
     * 搜索食谱
     */
    suspend fun searchRecipes(query: String): DomainResult<List<Recipe>>
}
