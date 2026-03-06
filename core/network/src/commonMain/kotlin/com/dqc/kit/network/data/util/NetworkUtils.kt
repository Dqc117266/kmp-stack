package com.dqc.kit.network.data.util

import com.dqc.kit.network.data.NetworkResult
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.io.IOException

/**
 * 顶级函数：安全发起 API 调用并捕获异常
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> =
    try {
        NetworkResult.Success(apiCall())
    } catch (e: Exception) {
        NetworkResult.Error(
            code = extractErrorCode(e),
            message = extractErrorMessage(e)
        )
    }

/**
 * 内部辅助函数（设为 private，不对外暴露，保持 Utils 简洁）
 */
private fun extractErrorCode(e: Throwable): Int? = when (e) {
    is ClientRequestException -> e.response.status.value
    is ServerResponseException -> e.response.status.value
    is ResponseException -> e.response.status.value
    is SocketTimeoutException -> HttpStatusCode.RequestTimeout.value
    is IOException -> null // 建议使用 ktor 的 IOException
    is NoTransformationFoundException -> HttpStatusCode.UnsupportedMediaType.value
    else -> null
}

private fun extractErrorMessage(e: Throwable): String = when (e) {
    is ClientRequestException -> "Client error: ${e.response.status}"
    is ServerResponseException -> "Server error: ${e.response.status}"
    is SocketTimeoutException -> "Request timeout"
    is IOException -> "Network error: ${e.message ?: "No connection"}"
    else -> e.message ?: "Unknown error"
}