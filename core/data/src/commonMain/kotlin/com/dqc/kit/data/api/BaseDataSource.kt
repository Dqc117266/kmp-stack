package com.dqc.kit.data.api

import com.dqc.kit.data.util.executeAndMap
import com.dqc.kit.domain.result.DomainResult
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put

/**
 * 通用远程数据源基类
 * 
 * 为 feature 模块提供极简的 HTTP 请求能力
 * 提供语义化的请求方法：get, post, put, patch, delete
 * 
 * 使用示例：
 * ```kotlin
 * class RecipeRemoteDataSource(client: HttpClient) : 
 *     BaseRemoteDataSource(client, "/api/v1/recipes") {
 *     
 *     suspend fun getRecipes() = get<List<RecipeDto>>()
 *     suspend fun getRecipeDetail(id: String) = get<RecipeDto>(id)
 *     suspend fun searchRecipes(query: String) = get<List<RecipeDto>>("search") {
 *         parameter("q", query)
 *     }
 *     suspend fun syncRecipes(recipes: List<RecipeDto>) = post<Unit>("sync") {
 *         setBody(recipes)
 *     }
 * }
 * ```
 *
 * @param client Ktor HTTP 客户端
 * @param basePath API 基础路径（如："/api/v1/recipes"）
 */
abstract class BaseRemoteDataSource(
    protected val client: HttpClient,
    protected val basePath: String
) {

    /**
     * 构建完整 URL
     * 自动处理路径分隔符
     */
    protected fun buildUrl(path: String = ""): String {
        return if (path.isEmpty()) {
            basePath
        } else {
            // 自动添加 / 分隔符
            "$basePath/${path.removePrefix("/")}"
        }
    }

    /**
     * GET 请求
     *
     * @param path 路径（可选，可以是 ID 或子路径，如："123" 或 "search"）
     * @param block 请求配置块（可选，用于添加参数、Header等）
     * @return DomainResult<T>
     *
     * 示例：
     * ```kotlin
     * get<List<RecipeDto>>()              // GET /api/v1/recipes
     * get<RecipeDto>("123")               // GET /api/v1/recipes/123
     * get<List<RecipeDto>>("search") {    // GET /api/v1/recipes/search?q=keyword
     *     parameter("q", keyword)
     * }
     * ```
     */
    protected suspend inline fun <reified T> get(
        path: String = "",
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): DomainResult<T> = client.executeAndMap {
        get(buildUrl(path)) { block() }
    }

    /**
     * POST 请求
     *
     * @param path 路径（可选）
     * @param block 请求配置块（用于设置 body、参数等）
     * @return DomainResult<T>
     *
     * 示例：
     * ```kotlin
     * post<RecipeDto> {                    // POST /api/v1/recipes
     *     setBody(recipe)
     * }
     * post<Unit>("sync") {                 // POST /api/v1/recipes/sync
     *     setBody(recipes)
     * }
     * ```
     */
    protected suspend inline fun <reified T> post(
        path: String = "",
        crossinline block: HttpRequestBuilder.() -> Unit
    ): DomainResult<T> = client.executeAndMap {
        post(buildUrl(path)) { block() }
    }

    /**
     * PUT 请求
     *
     * @param path 路径（可选，通常是 ID）
     * @param block 请求配置块
     * @return DomainResult<T>
     *
     * 示例：
     * ```kotlin
     * put<RecipeDto>("123") {              // PUT /api/v1/recipes/123
     *     setBody(recipe)
     * }
     * ```
     */
    protected suspend inline fun <reified T> put(
        path: String = "",
        crossinline block: HttpRequestBuilder.() -> Unit
    ): DomainResult<T> = client.executeAndMap {
        put(buildUrl(path)) { block() }
    }

    /**
     * PATCH 请求
     *
     * @param path 路径（可选，通常是 ID）
     * @param block 请求配置块
     * @return DomainResult<T>
     *
     * 示例：
     * ```kotlin
     * patch<RecipeDto>("123") {            // PATCH /api/v1/recipes/123
     *     setBody(partialUpdate)
     * }
     * ```
     */
    protected suspend inline fun <reified T> patch(
        path: String = "",
        crossinline block: HttpRequestBuilder.() -> Unit
    ): DomainResult<T> = client.executeAndMap {
        patch(buildUrl(path)) { block() }
    }

    /**
     * DELETE 请求
     *
     * @param path 路径（可选，通常是 ID）
     * @param block 请求配置块（可选）
     * @return DomainResult<T>
     *
     * 示例：
     * ```kotlin
     * delete<Unit>("123")                  // DELETE /api/v1/recipes/123
     * ```
     */
    protected suspend inline fun <reified T> delete(
        path: String = "",
        crossinline block: HttpRequestBuilder.() -> Unit = {}
    ): DomainResult<T> = client.executeAndMap {
        delete(buildUrl(path)) { block() }
    }
}

/**
 * 通用本地数据源基类
 * 
 * 为 feature 模块提供基础的数据库操作能力
 * 业务模块可以继承此类实现自己的本地数据源
 *
 * @param T 数据库 Transacter 类型（由 SQLDelight 生成）
 */
abstract class BaseLocalDataSource<T : app.cash.sqldelight.Transacter>(
    protected val database: T
)
