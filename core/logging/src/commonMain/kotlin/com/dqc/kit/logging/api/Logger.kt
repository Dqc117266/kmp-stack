package com.dqc.kit.logging.api

/**
 * 日志级别枚举
 */
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    ASSERT
}

/**
 * 日志接口 - 清洁架构中的抽象层
 * 领域层通过此接口使用日志功能，不依赖具体实现
 */
interface Logger {

    /**
     * 日志标签，用于区分不同模块的日志
     */
    val tag: String

    /**
     * 是否启用日志
     */
    var isEnabled: Boolean

    /**
     * 设置最低日志级别，低于此级别的日志不会输出
     */
    var minLogLevel: LogLevel

    /**
     * 记录 Verbose 级别日志
     */
    fun v(message: String, throwable: Throwable? = null)

    /**
     * 记录 Debug 级别日志
     */
    fun d(message: String, throwable: Throwable? = null)

    /**
     * 记录 Info 级别日志
     */
    fun i(message: String, throwable: Throwable? = null)

    /**
     * 记录 Warn 级别日志
     */
    fun w(message: String, throwable: Throwable? = null)

    /**
     * 记录 Error 级别日志
     */
    fun e(message: String, throwable: Throwable? = null)

    /**
     * 记录 Assert 级别日志
     */
    fun wtf(message: String, throwable: Throwable? = null)

    /**
     * 使用 lambda 延迟计算日志内容，避免在日志被过滤时仍进行字符串拼接
     */
    fun v(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun d(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun i(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun w(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun e(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun wtf(tag: String? = null, throwable: Throwable? = null, message: () -> String)

    /**
     * 创建带特定标签的新 Logger 实例
     */
    fun withTag(tag: String): Logger
}
