package com.example.recipe.data.datasource.remote

import com.dqc.kit.data.api.BaseRemoteDataSource
import com.example.recipe.data.model.RecipeDto
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody

/**
 * 食谱远程数据源
 * 
 * 极简的接口请求定义：路径 + 类型，动作由函数名决定
 */
class RecipeRemoteDataSource(
    client: HttpClient
) : BaseRemoteDataSource(client, "/api/v1/recipes") {

    /**
     * 获取食谱列表
     * GET /api/v1/recipes
     */
    suspend fun getRecipes() = get<List<RecipeDto>>()

    /**
     * 获取食谱详情
     * GET /api/v1/recipes/{id}
     */
    suspend fun getRecipeDetail(id: String) = get<RecipeDto>(id)

    /**
     * 搜索食谱
     * GET /api/v1/recipes/search?q={query}
     */
    suspend fun searchRecipes(query: String) = get<List<RecipeDto>>("search") {
        parameter("q", query)
    }

    /**
     * 同步食谱
     * POST /api/v1/recipes/sync
     */
    suspend fun syncRecipes(recipes: List<RecipeDto>) = post<Unit>("sync") {
        setBody(recipes)
    }
}
