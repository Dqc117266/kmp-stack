package com.dqc.kit.presentation.util

import com.dqc.kit.presentation.base.UiState
import com.dqc.kit.presentation.model.UiList
import com.dqc.kit.presentation.model.UiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 状态构建器
 * 提供便捷的状态构建方法
 */
object StateBuilder {

    /**
     * 创建加载状态
     */
    fun <S : UiState> S.withLoading(isLoading: Boolean = true): S {
        // 子类需要覆盖此方法提供具体实现
        @Suppress("UNCHECKED_CAST")
        return this
    }

    /**
     * 创建错误状态
     */
    fun <S : UiState> S.withError(error: UiText): S {
        // 子类需要覆盖此方法提供具体实现
        @Suppress("UNCHECKED_CAST")
        return this
    }
}

/**
 * Loading 状态接口
 * 用于需要加载状态的状态类
 */
interface LoadingState {
    val isLoading: Boolean
    fun withLoading(loading: Boolean): LoadingState
}

/**
 * 错误状态接口
 * 用于需要错误状态的状态类
 */
interface ErrorState {
    val error: UiText?
    fun withError(error: UiText?): ErrorState
}

/**
 * 列表状态接口
 * 用于需要列表数据的状态类
 */
interface ListState<T> {
    val listData: UiList<T>
    fun withList(list: UiList<T>): ListState<T>
}

/**
 * 防抖/节流工具
 */
object ThrottleUtils {
    private val lastExecutionTime = mutableMapOf<String, Long>()

    /**
     * 检查是否可以通过节流检查
     * @param key 操作标识
     * @param intervalMs 间隔时间（毫秒）
     * @return true 表示可以执行
     */
    fun canExecute(key: String, intervalMs: Long = 1000): Boolean {
        val now = currentTimeMillis()
        val last = lastExecutionTime[key]
        return if (last == null || now - last >= intervalMs) {
            lastExecutionTime[key] = now
            true
        } else {
            false
        }
    }

    /**
     * 清除指定 key 的节流记录
     */
    fun clear(key: String) {
        lastExecutionTime.remove(key)
    }

    /**
     * 清除所有节流记录
     */
    fun clearAll() {
        lastExecutionTime.clear()
    }

    private fun currentTimeMillis(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }
}

/**
 * 防抖工具
 */
object DebounceUtils {
    private val debounceJobs = mutableMapOf<String, kotlinx.coroutines.Job>()

    /**
     * 执行防抖操作
     * @param key 操作标识
     * @param scope 协程作用域
     * @param delayMs 延迟时间（毫秒）
     * @param action 要执行的操作
     */
    fun debounce(
        key: String,
        scope: CoroutineScope,
        delayMs: Long = 300,
        action: () -> Unit
    ) {
        debounceJobs[key]?.cancel()
        debounceJobs[key] = scope.launch {
            delay(delayMs)
            action()
            debounceJobs.remove(key)
        }
    }

    /**
     * 清除指定 key 的防抖任务
     */
    fun clear(key: String) {
        debounceJobs[key]?.cancel()
        debounceJobs.remove(key)
    }

    /**
     * 清除所有防抖任务
     */
    fun clearAll() {
        debounceJobs.values.forEach { it.cancel() }
        debounceJobs.clear()
    }
}
