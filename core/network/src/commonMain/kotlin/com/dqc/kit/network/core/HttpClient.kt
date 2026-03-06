package com.dqc.kit.network.core

import com.dqc.kit.network.auth.TokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

/**
 * Creates and configures HttpClient with authentication support
 */
fun createHttpClient(
    engine: HttpClientEngine,
    baseUrl: String,
    isDebug: Boolean,
    tokenProvider: TokenProvider? = null
): HttpClient = HttpClient(engine) {

    // ... 保留 Timeout, ContentNegotiation, Logging ...

    // 1. 移除针对 401 的 HttpRequestRetry
    // 只有在网络抖动时才需要 retry，授权失败不需要在此处理
    install(HttpRequestRetry) {
        maxRetries = 1
        retryOnExceptionIf { _, cause -> cause is io.ktor.utils.io.errors.IOException }
    }

    defaultRequest {
        url(baseUrl)
        contentType(ContentType.Application.Json)
    }

    // 2. 将拦截逻辑内聚（Ktor 3.x 推荐直接在配置中用 plugin(HttpSend)）
}.apply {
    tokenProvider?.let { provider ->
        plugin(HttpSend).intercept { request ->
            // 获取当前 Token 并添加 Header
            provider.getAccessToken()?.let {
                request.header(HttpHeaders.Authorization, "Bearer $it")
            }

            var response = execute(request)

            // 处理 401
            if (response.response.status == HttpStatusCode.Unauthorized) {
                // 这里建议在 provider 内部实现一个“同步锁”或“单例刷新”逻辑
                // 确保并发请求时只调用一次接口
                val newToken = provider.refreshToken()

                if (newToken != null) {
                    // 替换新 Token 并重试
                    request.headers.remove(HttpHeaders.Authorization)
                    request.header(HttpHeaders.Authorization, "Bearer $newToken")
                    response = execute(request)
                } else {
                    provider.clearTokens() // 刷新失败，强制登出
                }
            }

            response
        }
    }
}