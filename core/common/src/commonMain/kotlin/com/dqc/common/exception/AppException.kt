package com.dqc.common.exception

/**
 * 应用程序基础异常类
 *
 * 所有应用异常的基类，提供统一的错误处理接口
 *
 * @param message 错误消息
 * @param cause 原始异常
 */
sealed class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

// ==================== 网络异常 ====================

/**
 * 网络相关异常基类
 */
sealed class NetworkException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause)

/**
 * 无网络连接
 */
class NoConnectivityException(
    message: String = "No internet connection",
    cause: Throwable? = null
) : NetworkException(message, cause)

/**
 * 连接超时
 */
class ConnectionTimeoutException(
    message: String = "Connection timeout",
    cause: Throwable? = null
) : NetworkException(message, cause)

/**
 * HTTP 错误响应
 *
 * @param code HTTP 状态码
 * @param message 错误消息
 */
class HttpException(
    val code: Int,
    message: String? = null,
    cause: Throwable? = null
) : NetworkException("HTTP $code: $message", cause) {
    val isClientError: Boolean get() = code in 400..499
    val isServerError: Boolean get() = code in 500..599
}

/**
 * 请求被取消
 */
class RequestCancelledException(
    message: String = "Request was cancelled",
    cause: Throwable? = null
) : NetworkException(message, cause)

// ==================== 存储异常 ====================

/**
 * 存储相关异常基类
 */
sealed class StorageException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause)

/**
 * 数据未找到
 */
class DataNotFoundException(
    message: String = "Data not found",
    cause: Throwable? = null
) : StorageException(message, cause)

/**
 * 写入失败
 */
class WriteException(
    message: String = "Failed to write data",
    cause: Throwable? = null
) : StorageException(message, cause)

/**
 * 读取失败
 */
class ReadException(
    message: String = "Failed to read data",
    cause: Throwable? = null
) : StorageException(message, cause)

/**
 * 存储空间不足
 */
class InsufficientStorageException(
    message: String = "Insufficient storage space",
    cause: Throwable? = null
) : StorageException(message, cause)

// ==================== 业务逻辑异常 ====================

/**
 * 业务逻辑异常基类
 */
sealed class BusinessException(
    message: String? = null,
    cause: Throwable? = null
) : AppException(message, cause)

/**
 * 无效参数
 */
class InvalidParameterException(
    message: String = "Invalid parameter",
    cause: Throwable? = null
) : BusinessException(message, cause)

/**
 * 未授权/未登录
 */
class UnauthorizedException(
    message: String = "Unauthorized",
    cause: Throwable? = null
) : BusinessException(message, cause)

/**
 * 禁止访问
 */
class ForbiddenException(
    message: String = "Access forbidden",
    cause: Throwable? = null
) : BusinessException(message, cause)

// ==================== 通用异常 ====================

/**
 * 验证失败
 */
class ValidationException(
    message: String = "Validation failed",
    cause: Throwable? = null
) : AppException(message, cause)

/**
 * 操作被取消
 */
class CancellationException(
    message: String = "Operation was cancelled",
    cause: Throwable? = null
) : AppException(message, cause)

/**
 * 未知异常 - 用于包装未预期的异常
 */
class UnknownException(
    message: String = "Unknown error occurred",
    cause: Throwable? = null
) : AppException(message, cause)

// ==================== 异常处理工具 ====================

/**
 * 将任意 Throwable 转换为 AppException
 */
fun Throwable.toAppException(): AppException = when (this) {
    is AppException -> this
    is kotlinx.coroutines.CancellationException -> CancellationException(cause = this)
    else -> UnknownException(cause = this)
}

/**
 * 安全执行块，自动转换异常
 */
inline fun <T> runCatchingApp(block: () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e.toAppException())
    }

/**
 * 安全执行挂起块，自动转换异常
 */
suspend inline fun <T> runCatchingAppSuspend(crossinline block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e.toAppException())
    }
