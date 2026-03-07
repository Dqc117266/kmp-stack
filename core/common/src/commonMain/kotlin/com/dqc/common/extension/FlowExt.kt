package com.dqc.common.extension

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Flow 扩展函数集
 */

// ==================== 错误处理 ====================

/**
 * 安全地收集 Flow，自动捕获异常
 *
 * @param onError 错误处理回调
 * @param onEach 元素处理回调
 */
inline fun <T> Flow<T>.safeCollect(
    crossinline onError: (Throwable) -> Unit = {},
    crossinline onEach: (T) -> Unit
) = onEach { onEach(it) }
    .catch { if (it !is CancellationException) onError(it) }

/**
 * 重试机制，带有指数退避策略
 *
 * @param maxRetries 最大重试次数
 * @param initialDelayMillis 初始延迟（毫秒）
 * @param maxDelayMillis 最大延迟（毫秒）
 * @param factor 延迟增长因子
 * @param onRetry 重试回调，参数为 (异常, 尝试次数)
 */
fun <T> Flow<T>.retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMillis: Long = 100,
    maxDelayMillis: Long = 5000,
    factor: Double = 2.0,
    onRetry: ((Throwable, Int) -> Unit)? = null
): Flow<T> = retryWhen { cause, attempt ->
    if (attempt >= maxRetries || cause is CancellationException) {
        false
    } else {
        val delayMs = (initialDelayMillis * factor.pow(attempt.toInt())).toLong()
            .coerceAtMost(maxDelayMillis)
        onRetry?.invoke(cause, attempt.toInt() + 1)
        delay(delayMs)
        true
    }
}

/**
 * 带有超时处理的安全 Flow
 *
 * @param timeoutMillis 超时时间（毫秒）
 * @param fallback 超时后的回退值
 */
fun <T> Flow<T>.withTimeoutFallback(
    timeoutMillis: Long,
    fallback: T
): Flow<T> = flow {
    try {
        withTimeout(timeoutMillis) {
            collect { emit(it) }
        }
    } catch (e: TimeoutCancellationException) {
        emit(fallback)
    }
}

// ==================== 转换操作 ====================

/**
 * 将 Flow 转换为 StateFlow，带有默认值
 *
 * @param scope CoroutineScope
 * @param initialValue 初始值
 * @return StateFlow
 */
fun <T> Flow<T>.asStateFlow(scope: CoroutineScope, initialValue: T): StateFlow<T> {
    val stateFlow = MutableStateFlow(initialValue)
    onEach { stateFlow.value = it }.launchIn(scope)
    return stateFlow.asStateFlow()
}

/**
 * 将 Flow 转换为 MutableStateFlow，带有默认值
 *
 * @param initialValue 初始值
 * @return MutableStateFlow
 */
fun <T> Flow<T>.asMutableStateFlow(initialValue: T): MutableStateFlow<T> {
    return MutableStateFlow(initialValue)
}

/**
 * 合并多个 Flow，任何一个发出值时都会触发
 *
 * @param flows 要合并的 Flow 集合
 * @return 合并后的 Flow
 */
fun <T> mergeFlows(vararg flows: Flow<T>): Flow<T> = merge(*flows)

/**
 * 扫描操作，累积结果
 *
 * @param initial 初始值
 * @param operation 累积操作
 */
fun <T, R> Flow<T>.scanWith(initial: R, operation: suspend (acc: R, value: T) -> R): Flow<R> {
    return scan(initial, operation)
}

// ==================== 节流与防抖 ====================

/**
 * 防抖 - 等待指定时间后发送最后一个值
 *
 * @param timeoutMillis 防抖时间（毫秒）
 * @return 防抖后的 Flow
 */
@OptIn(FlowPreview::class)
fun <T> Flow<T>.debounceMillis(timeoutMillis: Long): Flow<T> =
    debounce(timeoutMillis.milliseconds)

/**
 * 采样 - 固定时间间隔发送最新值（替代 throttleLatest）
 *
 * @param intervalMillis 时间间隔（毫秒）
 * @return 采样后的 Flow
 */
@OptIn(FlowPreview::class)
fun <T> Flow<T>.sampleMillis(intervalMillis: Long): Flow<T> =
    sample(intervalMillis.milliseconds)

// ==================== 调度器切换 ====================

/**
 * 在指定调度器上执行 Flow 操作
 *
 * @param dispatcher 调度器
 * @return Flow
 */
fun <T> Flow<T>.onDispatcher(dispatcher: CoroutineDispatcher): Flow<T> =
    flowOn(dispatcher)

/**
 * 在 IO 调度器上执行
 */
fun <T> Flow<T>.flowOnIo(dispatcherProvider: com.dqc.common.coroutines.DispatcherProvider): Flow<T> =
    flowOn(dispatcherProvider.io)

/**
 * 在 Default 调度器上执行
 */
fun <T> Flow<T>.flowOnDefault(dispatcherProvider: com.dqc.common.coroutines.DispatcherProvider): Flow<T> =
    flowOn(dispatcherProvider.default)

// ==================== 组合操作 ====================

/**
 * 组合两个 Flow，使用最新值
 *
 * @param other 另一个 Flow
 * @param transform 转换函数
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T1, T2, R> Flow<T1>.combineWithLatest(
    other: Flow<T2>,
    transform: suspend (T1, T2) -> R
): Flow<R> = flatMapLatest { t1 ->
    other.map { t2 -> transform(t1, t2) }
}

// ==================== 收集辅助 ====================

/**
 * 在指定 Scope 中启动收集
 *
 * @param scope CoroutineScope
 * @param action 收集动作
 */
fun <T> Flow<T>.launchIn(scope: CoroutineScope, action: suspend (T) -> Unit) =
    onEach { action(it) }.launchIn(scope)

/**
 * 收集到 StateFlow
 *
 * @param scope CoroutineScope
 * @param initial 初始值
 * @return StateFlow
 */
fun <T> Flow<T>.stateIn(
    scope: CoroutineScope,
    initial: T
): StateFlow<T> {
    val state = MutableStateFlow(initial)
    scope.launch {
        collect { state.value = it }
    }
    return state.asStateFlow()
}

// ==================== 缓存与共享 ====================

/**
 * 创建可重放的 SharedFlow
 *
 * @param replay 重放数量
 * @return MutableSharedFlow
 */
fun <T> sharedFlow(replay: Int = 0): MutableSharedFlow<T> =
    MutableSharedFlow(replay = replay)

/**
 * 创建带默认值的 StateFlow
 *
 * @param initialValue 初始值
 * @return MutableStateFlow
 */
fun <T> stateFlow(initialValue: T): MutableStateFlow<T> =
    MutableStateFlow(initialValue)

/**
 * 安全地更新 StateFlow 值
 */
inline fun <T> MutableStateFlow<T>.update(function: (T) -> T) {
    value = function(value)
}

// ==================== 流构建器 ====================

/**
 * 将挂起函数转换为 Flow
 *
 * @param block 挂起函数
 * @return Flow
 */
fun <T> flowFrom(block: suspend () -> T): Flow<T> = flow {
    emit(block())
}

/**
 * 在指定调度器上创建 Flow
 *
 * @param dispatcher 调度器
 * @param block 代码块
 * @return Flow
 */
fun <T> flowOn(
    dispatcher: CoroutineDispatcher,
    block: suspend () -> T
): Flow<T> = flow {
    emit(kotlinx.coroutines.withContext(dispatcher) { block() })
}

// ==================== 条件操作 ====================

/**
 * 忽略空值
 */
fun <T : Any> Flow<T?>.ignoreNulls(): Flow<T> =
    filterNotNull()

/**
 * 当值改变时只发送一次（去重）
 */
fun <T> Flow<T>.distinct(): Flow<T> =
    distinctUntilChanged()

/**
 * 限制发送次数
 *
 * @param count 最大次数
 */
fun <T> Flow<T>.takeMax(count: Int): Flow<T> =
    take(count)
