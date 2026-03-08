package com.dqc.kit.domain.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

/**
 * 领域层结果封装
 * 表示业务操作的结果，与数据层解耦
 *
 * 设计原则：
 * 1. 独立于网络结果类型，domain 层不关心 HTTP 状态码
 * 2. 只包含 Success 和 Error 两种状态，Loading 由 ViewModel 管理
 * 3. 提供便捷的处理函数（fold, onSuccess, onError, recover 等）
 *
 * @param T 成功时返回的数据类型
 */
sealed class DomainResult<out T> {

    /**
     * 成功结果
     * @param data 业务数据
     */
    data class Success<out T>(val data: T) : DomainResult<T>()

    /**
     * 错误结果
     * @param error 领域层错误类型
     */
    data class Error(val error: DomainError) : DomainResult<Nothing>()

    // ==================== 类型检查属性 ====================

    /**
     * 是否成功
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * 是否失败
     */
    val isError: Boolean get() = this is Error

    // ==================== 数据获取 ====================

    /**
     * 获取成功数据，失败时返回 null
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    /**
     * 获取成功数据，失败时返回默认值
     *
     * @param defaultValue 默认值
     */
    fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Error -> defaultValue
    }

    /**
     * 获取成功数据，失败时抛出异常
     *
     * @throws NoSuchElementException 如果是 Error 状态
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw NoSuchElementException("Result is error: ${error.message}")
    }

    /**
     * 获取错误，成功时返回 null
     */
    fun errorOrNull(): DomainError? = when (this) {
        is Error -> error
        is Success -> null
    }

    // ==================== 转换操作 ====================

    /**
     * 转换成功数据为另一种类型
     *
     * @param transform 转换函数
     * @return 转换后的 DomainResult
     */
    inline fun <R> map(transform: (T) -> R): DomainResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    /**
     * 转换错误为另一种结果
     *
     * @param transform 错误转换函数
     * @return 转换后的 DomainResult
     */
    inline fun mapError(transform: (DomainError) -> DomainError): DomainResult<T> = when (this) {
        is Success -> this
        is Error -> Error(transform(error))
    }

    /**
     * 展平嵌套的 DomainResult
     */
    fun <R> flatMap(transform: (T) -> DomainResult<R>): DomainResult<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
    }

    /**
     * 错误恢复：如果失败则使用备用值
     *
     * @param fallback 备用值提供者
     * @return 成功时返回原值，失败时返回备用值
     */
    inline fun recover(fallback: (DomainError) -> @UnsafeVariance T): DomainResult<T> = when (this) {
        is Success -> this
        is Error -> Success(fallback(error))
    }

    /**
     * 带条件的错误恢复
     *
     * @param predicate 恢复条件
     * @param fallback 备用值提供者
     */
    inline fun recoverIf(
        predicate: (DomainError) -> Boolean,
        fallback: (DomainError) -> @UnsafeVariance T
    ): DomainResult<T> = when (this) {
        is Success -> this
        is Error -> if (predicate(error)) Success(fallback(error)) else this
    }

    // ==================== 副作用操作 ====================

    /**
     * 成功时执行副作用
     *
     * @param action 副作用函数
     * @return 原 DomainResult（支持链式调用）
     */
    inline fun onSuccess(action: (T) -> Unit): DomainResult<T> = apply {
        if (this is Success) action(data)
    }

    /**
     * 失败时执行副作用
     *
     * @param action 副作用函数
     * @return 原 DomainResult（支持链式调用）
     */
    inline fun onError(action: (DomainError) -> Unit): DomainResult<T> = apply {
        if (this is Error) action(error)
    }

    /**
     * 无论成功失败都执行副作用
     *
     * @param action 副作用函数
     * @return 原 DomainResult（支持链式调用）
     */
    inline fun onComplete(action: () -> Unit): DomainResult<T> = apply {
        action()
    }

    // ==================== 折叠操作 ====================

    /**
     * 折叠结果，处理所有可能的情况
     *
     * @param onSuccess 成功处理函数
     * @param onError 错误处理函数
     * @return 处理结果
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (DomainError) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(error)
    }

    /**
     * 获取值或处理错误（简化版 fold）
     *
     * @param onError 错误处理函数，返回替代值
     * @return 成功数据或错误处理后的值
     */
    inline fun getOrElse(onError: (DomainError) -> @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Error -> onError(error)
    }

    // ==================== 组合操作 ====================

    /**
     * 组合两个 DomainResult，两者都成功时返回 Pair
     *
     * @param other 另一个 DomainResult
     * @return 组合后的结果
     */
    fun <U> zip(other: DomainResult<U>): DomainResult<Pair<T, U>> = when {
        this is Error -> this
        other is Error -> other
        this is Success && other is Success -> Success(data to other.data)
        else -> throw IllegalStateException("Unexpected state")
    }

    /**
     * 组合两个 DomainResult，使用自定义组合函数
     *
     * @param other 另一个 DomainResult
     * @param transform 组合函数
     * @return 组合后的结果
     */
    inline fun <U, R> zipWith(
        other: DomainResult<U>,
        transform: (T, U) -> R
    ): DomainResult<R> = zip(other).map { transform(it.first, it.second) }

    companion object {
        /**
         * 创建成功结果
         */
        fun <T> success(data: T): DomainResult<T> = Success(data)

        /**
         * 创建错误结果
         */
        fun <T> error(error: DomainError): DomainResult<T> = Error(error)

        /**
         * 创建错误结果（便捷方法）
         */
        fun <T> error(message: String, cause: Throwable? = null): DomainResult<T> =
            Error(DomainError.Unknown(cause, message))

        /**
         * 运行代码块并包装为 DomainResult
         *
         * @param block 可能抛出异常的代码块
         * @return 成功或错误结果
         */
        inline fun <T> runCatching(block: () -> T): DomainResult<T> = try {
            Success(block())
        } catch (e: Throwable) {
            Error(DomainError.Unknown(e))
        }

        /**
         * 运行挂起代码块并包装为 DomainResult
         *
         * @param block 可能抛出异常的挂起代码块
         * @return 成功或错误结果
         */
        suspend inline fun <T> runCatchingAsync(crossinline block: suspend () -> T): DomainResult<T> = try {
            Success(block())
        } catch (e: Throwable) {
            Error(DomainError.Unknown(e))
        }
    }
}

// ==================== DomainError 定义 ====================

/**
 * 领域层错误类型
 * 与 HTTP 错误码解耦，使用业务语义化的错误类型
 */
sealed class DomainError(open val message: String) {

    /**
     * 业务错误
     * 如：用户名已存在、余额不足等
     */
    data class BusinessError(
        val code: String,
        override val message: String
    ) : DomainError(message)

    /**
     * 网络错误
     * 如：无网络连接、超时等
     */
    data class NetworkError(
        override val message: String = "Network error"
    ) : DomainError(message)

    /**
     * 未授权
     * 如：Token 过期、未登录等
     */
    data class Unauthorized(
        override val message: String = "Unauthorized"
    ) : DomainError(message)

    /**
     * 未找到
     * 如：资源不存在
     */
    data class NotFound(
        override val message: String = "Resource not found"
    ) : DomainError(message)

    /**
     * 验证失败
     * 如：参数校验失败、格式错误等
     */
    data class ValidationError(
        val field: String? = null,
        override val message: String
    ) : DomainError(message)

    /**
     * 未知错误
     */
    data class Unknown(
        val throwable: Throwable? = null,
        override val message: String = throwable?.message ?: "Unknown error"
    ) : DomainError(message)

    /**
     * 解析错误
    */
    data class ParseError(
        override val message: String = "Parse error"
    ) : DomainError(message)

    companion object {
        /**
         * 快速创建验证错误
         */
        fun validation(field: String? = null, message: String): DomainError =
            ValidationError(field, message)

        /**
         * 快速创建网络错误
         */
        fun network(message: String = "Network error"): DomainError =
            NetworkError(message)

        /**
         * 快速创建未授权错误
         */
        fun unauthorized(message: String = "Unauthorized"): DomainError =
            Unauthorized(message)

        /**
         * 快速创建未找到错误
         */
        fun notFound(message: String = "Resource not found"): DomainError =
            NotFound(message)

        /**
         * 快速创建业务错误
         */
        fun business(code: String, message: String): DomainError =
            BusinessError(code, message)
    }
}

// ==================== Flow 扩展 ====================

/**
 * Flow 扩展：将 Flow<T> 转换为 Flow<DomainResult<T>>
 * 自动捕获异常并转换为 DomainResult.Error
 */
fun <T> Flow<T>.asDomainResult(): Flow<DomainResult<T>> =
    map<T, DomainResult<T>> { DomainResult.Success(it) }
        .catch { e: Throwable ->
            emit(DomainResult.Error(DomainError.Unknown(e)))
        }

/**
 * Flow<DomainResult<T>> 扩展：在 Flow 中转换 DomainResult 中的数据
 */
fun <T, R> Flow<DomainResult<T>>.mapResult(
    transform: suspend (T) -> R
): Flow<DomainResult<R>> = map { result ->
    when (result) {
        is DomainResult.Success -> DomainResult.Success(transform(result.data))
        is DomainResult.Error -> DomainResult.Error(result.error)
    }
}

/**
 * Flow<DomainResult<T>> 扩展：过滤只保留成功结果的数据
 */
fun <T> Flow<DomainResult<T>>.filterSuccess(): Flow<T> =
    mapNotNull { it.getOrNull() }

/**
 * Flow<DomainResult<T>> 扩展：只保留错误结果
 */
fun <T> Flow<DomainResult<T>>.filterErrors(): Flow<DomainError> =
    mapNotNull { it.errorOrNull() }

/**
 * Flow<DomainResult<T>> 扩展：成功时执行副作用
 */
fun <T> Flow<DomainResult<T>>.onEachSuccess(
    action: suspend (T) -> Unit
): Flow<DomainResult<T>> = onEach { result ->
    if (result is DomainResult.Success) action(result.data)
}

/**
 * Flow<DomainResult<T>> 扩展：错误时执行副作用
 */
fun <T> Flow<DomainResult<T>>.onEachError(
    action: suspend (DomainError) -> Unit
): Flow<DomainResult<T>> = onEach { result ->
    if (result is DomainResult.Error) action(result.error)
}
