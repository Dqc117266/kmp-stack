package com.dqc.common.extension

import com.dqc.common.exception.toAppException

/**
 * Result 类型扩展函数集
 */

/**
 * 如果 Result 是失败状态，转换异常为 AppException
 *
 * @return 转换后的 Result
 */
fun <T> Result<T>.mapException(): Result<T> {
    return exceptionOrNull()?.let {
        Result.failure(it.toAppException())
    } ?: this
}

/**
 * 获取成功值，失败时返回默认值
 *
 * @param default 默认值提供者
 * @return 成功值或默认值
 */
inline fun <T> Result<T>.getOrDefault(default: () -> T): T {
    return getOrElse { default() }
}

/**
 * 如果 Result 是失败状态，执行操作
 *
 * @param action 失败时的操作
 * @return 原 Result
 */
inline fun <T> Result<T>.onFailureAction(action: (Throwable) -> Unit): Result<T> {
    exceptionOrNull()?.let(action)
    return this
}

/**
 * 如果 Result 是成功状态且满足条件，执行操作
 *
 * @param predicate 条件
 * @param action 操作
 * @return 原 Result
 */
inline fun <T> Result<T>.onSuccessIf(
    predicate: (T) -> Boolean,
    action: (T) -> Unit
): Result<T> {
    onSuccess {
        if (predicate(it)) action(it)
    }
    return this
}

/**
 * 将 Result 转换，失败时返回 null
 *
 * @return 成功值或 null
 */
fun <T> Result<T>.orNull(): T? {
    return getOrNull()
}

/**
 * 扁平化嵌套的 Result
 *
 * @return 扁平化后的 Result
 */
fun <T> Result<Result<T>>.flatten(): Result<T> {
    return fold(
        onSuccess = { it },
        onFailure = { Result.failure(it) }
    )
}

/**
 * 组合两个 Result，任一失败则返回失败
 *
 * @param other 另一个 Result
 * @param transform 组合函数
 * @return 组合后的 Result
 */
inline fun <T1, T2, R> Result<T1>.zip(
    other: Result<T2>,
    transform: (T1, T2) -> R
): Result<R> {
    return fold(
        onSuccess = { t1 ->
            other.fold(
                onSuccess = { t2 -> Result.success(transform(t1, t2)) },
                onFailure = { Result.failure(it) }
            )
        },
        onFailure = { Result.failure(it) }
    )
}

/**
 * 安全地执行操作，捕获所有异常
 *
 * @param block 代码块
 * @return Result 包装结果
 */
inline fun <T> runCatchingSafe(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e.toAppException())
    }
}

/**
 * 恢复失败结果为成功结果
 *
 * @param recovery 恢复函数
 * @return 新的 Result
 */
inline fun <T> Result<T>.recoverCatching(recovery: (Throwable) -> T): Result<T> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatchingSafe { recovery(exception) }
    }
}

/**
 * 映射成功值，失败时保持不变
 *
 * @param transform 转换函数
 * @return 新的 Result
 */
inline fun <T, R> Result<T>.mapCatching(transform: (T) -> R): Result<R> {
    return fold(
        onSuccess = { runCatchingSafe { transform(it) } },
        onFailure = { Result.failure(it) }
    )
}

/**
 * 检查 Result 是否为特定异常类型
 */
inline fun <reified E : Throwable> Result<*>.isFailureOfType(): Boolean {
    return exceptionOrNull() is E
}

/**
 * 获取特定类型的异常
 */
inline fun <reified E : Throwable> Result<*>.exceptionAsOrNull(): E? {
    return exceptionOrNull() as? E
}

/**
 * 如果失败，返回替代的 Result
 *
 * @param alternative 替代 Result 提供者
 * @return 原 Result 或替代 Result
 */
inline fun <T> Result<T>.orElse(alternative: () -> Result<T>): Result<T> {
    return if (isSuccess) this else alternative()
}

/**
 * 如果失败且满足条件，返回替代的 Result
 *
 * @param predicate 条件
 * @param alternative 替代 Result 提供者
 * @return 原 Result 或替代 Result
 */
inline fun <T> Result<T>.orElseIf(
    predicate: (Throwable) -> Boolean,
    alternative: () -> Result<T>
): Result<T> {
    return if (isFailure && predicate(exceptionOrNull()!!)) {
        alternative()
    } else this
}
