package com.dqc.kit.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * 防抖工具类
 * 用于防止高频操作被重复执行
 */
object DebounceUtils {

    private val debounceMap = mutableMapOf<String, Long>()

    /**
     * 检查是否应该执行操作（防抖）
     *
     * @param key 操作的唯一标识
     * @param interval 防抖间隔
     * @return true 表示应该执行，false 表示被防抖忽略
     *
     * 使用示例：
     * ```kotlin
     * fun onSearch(query: String) {
     *     if (DebounceUtils.shouldExecute("search", 300.milliseconds)) {
     *         performSearch(query)
     *     }
     * }
     * ```
     */
    fun shouldExecute(key: String, interval: Duration = 300.milliseconds): Boolean {
        val now = currentTimeMillis()
        val last = debounceMap[key]
        return if (last == null || now - last > interval.inWholeMilliseconds) {
            debounceMap[key] = now
            true
        } else {
            false
        }
    }

    /**
     * 清除指定 key 的防抖记录
     */
    fun clear(key: String) {
        debounceMap.remove(key)
    }

    /**
     * 清除所有防抖记录
     */
    fun clearAll() {
        debounceMap.clear()
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    private fun currentTimeMillis(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }
}

/**
 * Flow 扩展：对 Flow 进行防抖
 *
 * @param duration 防抖时长
 * @return 防抖后的 Flow
 */
@OptIn(kotlinx.coroutines.FlowPreview::class)
fun <T> Flow<T>.debounce(duration: Duration): Flow<T> {
    return debounce(duration.inWholeMilliseconds)
}

/**
 * 节流工具类
 * 用于限制操作执行频率
 */
object ThrottleUtils {

    private val throttleMap = mutableMapOf<String, Long>()

    /**
     * 节流执行操作
     * 确保在指定间隔内只执行一次
     *
     * @param key 操作的唯一标识
     * @param interval 节流间隔
     * @param action 要执行的操作
     *
     * 使用示例：
     * ```kotlin
     * fun onClick() {
     *     ThrottleUtils.throttle("submit", 1000.milliseconds) {
     *         submitForm()
     *     }
     * }
     * ```
     */
    fun throttle(
        key: String,
        interval: Duration = 1000.milliseconds,
        action: () -> Unit
    ) {
        val now = currentTimeMillis()
        val last = throttleMap[key]
        if (last == null || now - last > interval.inWholeMilliseconds) {
            throttleMap[key] = now
            action()
        }
    }

    /**
     * 检查是否可以通过节流检查
     *
     * @param key 操作的唯一标识
     * @param interval 节流间隔
     * @return true 表示可以执行
     */
    fun canExecute(key: String, interval: Duration = 1000.milliseconds): Boolean {
        val now = currentTimeMillis()
        val last = throttleMap[key]
        return if (last == null || now - last > interval.inWholeMilliseconds) {
            throttleMap[key] = now
            true
        } else {
            false
        }
    }

    /**
     * 清除指定 key 的节流记录
     */
    fun clear(key: String) {
        throttleMap.remove(key)
    }

    /**
     * 清除所有节流记录
     */
    fun clearAll() {
        throttleMap.clear()
    }

    /**
     * 获取当前时间戳（毫秒）
     */
    private fun currentTimeMillis(): Long {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
    }
}
