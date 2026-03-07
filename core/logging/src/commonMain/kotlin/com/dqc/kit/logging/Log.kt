package com.dqc.kit.logging

import com.dqc.kit.logging.api.LogLevel
import com.dqc.kit.logging.api.Logger
import com.dqc.kit.logging.api.LoggerConfig
import com.dqc.kit.logging.internal.KermitLoggerFactory
import kotlin.reflect.KClass

/**
 * 静态日志入口
 *
 * 使用示例:
 * ```kotlin
 * // 简单日志
 * Log.d("调试信息")
 * Log.i("普通信息")
 * Log.e("错误信息", exception)
 *
 * // 带标签的日志
 * Log.tag("Network").i("请求开始")
 *
 * // 延迟计算（性能优化）
 * Log.d { "复杂对象: ${expensiveObject.toString()}" }
 *
 * // 为类创建 Logger
 * class MyRepository {
 *     private val logger = Log.of(this::class)
 *     // 或
 *     private val logger = Log.of("MyRepository")
 * }
 * ```
 */
object Log {

    private val factory = KermitLoggerFactory()

    /**
     * 默认 Logger 实例
     */
    private val defaultLogger: Logger = factory.getDefault()

    /**
     * 设置全局日志配置
     */
    fun config(config: LoggerConfig) {
        factory.setDefaultConfig(config)
    }

    /**
     * 是否启用日志
     */
    var isEnabled: Boolean
        get() = defaultLogger.isEnabled
        set(value) {
            defaultLogger.isEnabled = value
        }

    /**
     * 最低日志级别
     */
    var minLogLevel: LogLevel
        get() = defaultLogger.minLogLevel
        set(value) {
            defaultLogger.minLogLevel = value
        }

    /**
     * 获取带特定标签的 Logger
     */
    fun tag(tag: String): Logger = factory.create(tag)

    /**
     * 根据类获取 Logger
     */
    fun of(clazz: KClass<*>): Logger = factory.create(clazz)

    /**
     * 根据类名获取 Logger
     */
    fun of(name: String): Logger = factory.create(name)

    // ===== 便捷日志方法 =====

    fun v(message: String, throwable: Throwable? = null) {
        defaultLogger.v(message, throwable)
    }

    fun d(message: String, throwable: Throwable? = null) {
        defaultLogger.d(message, throwable)
    }

    fun i(message: String, throwable: Throwable? = null) {
        defaultLogger.i(message, throwable)
    }

    fun w(message: String, throwable: Throwable? = null) {
        defaultLogger.w(message, throwable)
    }

    fun e(message: String, throwable: Throwable? = null) {
        defaultLogger.e(message, throwable)
    }

    fun wtf(message: String, throwable: Throwable? = null) {
        defaultLogger.wtf(message, throwable)
    }

    // ===== 延迟计算的日志方法 =====

    fun v(throwable: Throwable? = null, message: () -> String) {
        defaultLogger.v(null, throwable, message)
    }

    fun d(throwable: Throwable? = null, message: () -> String) {
        defaultLogger.d(null, throwable, message)
    }

    fun i(throwable: Throwable? = null, message: () -> String) {
        defaultLogger.i(null, throwable, message)
    }

    fun w(throwable: Throwable? = null, message: () -> String) {
        defaultLogger.w(null, throwable, message)
    }

    fun e(throwable: Throwable? = null, message: () -> String) {
        defaultLogger.e(null, throwable, message)
    }

    fun wtf(throwable: Throwable? = null, message: () -> String) {
        defaultLogger.wtf(null, throwable, message)
    }
}
