package com.dqc.kit.data.util

import com.dqc.kit.domain.result.DomainError
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode

/**
 * 网络异常映射器
 * 将底层网络异常映射为领域层错误类型
 */
object ErrorMapper {

    /**
     * 将 Throwable 映射为 DomainError
     */
    fun map(throwable: Throwable): DomainError {
        return when (throwable) {
            // 客户端请求错误 (4xx)
            is ClientRequestException -> mapClientError(throwable)

            // 服务器错误 (5xx)
            is ServerResponseException -> DomainError.NetworkError(
                message = "Server error: ${throwable.response.status}"
            )

            // 连接超时
            is SocketTimeoutException -> DomainError.NetworkError(
                message = "Connection timeout"
            )

            // IO 错误（无网络等）
            is kotlinx.io.IOException -> DomainError.NetworkError(
                message = "No internet connection"
            )

            // 其他未知错误
            else -> DomainError.Unknown(
                throwable = throwable,
                message = throwable.message ?: "Unknown error"
            )
        }
    }

    /**
     * 映射客户端错误 (4xx)
     */
    private fun mapClientError(exception: ClientRequestException): DomainError {
        return when (exception.response.status) {
            HttpStatusCode.Unauthorized -> DomainError.Unauthorized(
                message = "Session expired, please login again"
            )

            HttpStatusCode.Forbidden -> DomainError.BusinessError(
                code = "FORBIDDEN",
                message = "Access denied"
            )

            HttpStatusCode.NotFound -> DomainError.NotFound(
                message = "Resource not found"
            )

            HttpStatusCode.BadRequest -> DomainError.ValidationError(
                message = "Invalid request"
            )

            HttpStatusCode.UnprocessableEntity -> {
                // 尝试解析验证错误详情
                DomainError.ValidationError(
                    message = "Validation failed"
                )
            }

            else -> DomainError.BusinessError(
                code = exception.response.status.value.toString(),
                message = "Request failed: ${exception.response.status}"
            )
        }
    }

    /**
     * 根据业务状态码映射错误
     */
    fun mapBusinessCode(code: Int, message: String?): DomainError {
        return when (code) {
            401 -> DomainError.Unauthorized(message ?: "Unauthorized")
            403 -> DomainError.BusinessError("FORBIDDEN", message ?: "Forbidden")
            404 -> DomainError.NotFound(message ?: "Not found")
            422 -> DomainError.ValidationError(message = message ?: "Validation failed")
            else -> DomainError.BusinessError(
                code = code.toString(),
                message = message ?: "Business error"
            )
        }
    }
}

/**
 * 安全执行挂起块，自动捕获并映射异常
 */
suspend inline fun <T> runCatchingNetwork(
    crossinline block: suspend () -> T
): Result<T> = try {
    Result.success(block())
} catch (e: Throwable) {
    Result.failure(NetworkException(ErrorMapper.map(e)))
}

/**
 * 网络异常包装器
 * 用于在 Result 中传递 DomainError
 */
class NetworkException(val error: DomainError) : Exception(error.message)
