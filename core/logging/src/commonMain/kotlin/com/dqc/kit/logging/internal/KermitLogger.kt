package com.dqc.kit.logging.internal

import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import com.dqc.kit.logging.api.LogLevel
import com.dqc.kit.logging.api.Logger

/**
 * Kermit 日志实现
 * 将 Logger 接口适配到 Kermit 日志库
 */
internal class KermitLogger(
    override val tag: String,
    override var isEnabled: Boolean = true,
    override var minLogLevel: LogLevel = LogLevel.DEBUG,
    private val kermitLogger: co.touchlab.kermit.Logger = createDefaultKermitLogger(tag)
) : Logger {

    companion object {
        /**
         * 创建默认的 Kermit Logger 配置
         */
        fun createDefaultKermitLogger(tag: String): co.touchlab.kermit.Logger {
            val config = StaticConfig(
                logWriterList = listOf(platformLogWriter()),
                minSeverity = Severity.Debug
            )
            return co.touchlab.kermit.Logger(config, tag)
        }
    }

    override fun v(message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.VERBOSE)) {
            kermitLogger.v(throwable) { message }
        }
    }

    override fun d(message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.DEBUG)) {
            kermitLogger.d(throwable) { message }
        }
    }

    override fun i(message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.INFO)) {
            kermitLogger.i(throwable) { message }
        }
    }

    override fun w(message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.WARN)) {
            kermitLogger.w(throwable) { message }
        }
    }

    override fun e(message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.ERROR)) {
            kermitLogger.e(throwable) { message }
        }
    }

    override fun wtf(message: String, throwable: Throwable?) {
        if (shouldLog(LogLevel.ASSERT)) {
            kermitLogger.a(throwable) { message }
        }
    }

    override fun v(tag: String?, throwable: Throwable?, message: () -> String) {
        if (shouldLog(LogLevel.VERBOSE)) {
            val logger = tag?.let { kermitLogger.withTag(it) } ?: kermitLogger
            logger.v(message(), throwable)
        }
    }

    override fun d(tag: String?, throwable: Throwable?, message: () -> String) {
        if (shouldLog(LogLevel.DEBUG)) {
            val logger = tag?.let { kermitLogger.withTag(it) } ?: kermitLogger
            logger.d(message(), throwable)
        }
    }

    override fun i(tag: String?, throwable: Throwable?, message: () -> String) {
        if (shouldLog(LogLevel.INFO)) {
            val logger = tag?.let { kermitLogger.withTag(it) } ?: kermitLogger
            logger.i(message(), throwable)
        }
    }

    override fun w(tag: String?, throwable: Throwable?, message: () -> String) {
        if (shouldLog(LogLevel.WARN)) {
            val logger = tag?.let { kermitLogger.withTag(it) } ?: kermitLogger
            logger.w(message(), throwable)
        }
    }

    override fun e(tag: String?, throwable: Throwable?, message: () -> String) {
        if (shouldLog(LogLevel.ERROR)) {
            val logger = tag?.let { kermitLogger.withTag(it) } ?: kermitLogger
            logger.e(message(), throwable)
        }
    }

    override fun wtf(tag: String?, throwable: Throwable?, message: () -> String) {
        if (shouldLog(LogLevel.ASSERT)) {
            val logger = tag?.let { kermitLogger.withTag(it) } ?: kermitLogger
            logger.a(message(), throwable)
        }
    }

    override fun withTag(tag: String): Logger {
        return KermitLogger(
            tag = tag,
            isEnabled = isEnabled,
            minLogLevel = minLogLevel,
            kermitLogger = kermitLogger.withTag(tag)
        )
    }

    /**
     * 检查指定级别是否应该记录
     */
    private fun shouldLog(level: LogLevel): Boolean {
        if (!isEnabled) return false
        return level.ordinal >= minLogLevel.ordinal
    }
}

/**
 * 将 LogLevel 转换为 Kermit 的 Severity
 */
private fun LogLevel.toSeverity(): Severity = when (this) {
    LogLevel.VERBOSE -> Severity.Verbose
    LogLevel.DEBUG -> Severity.Debug
    LogLevel.INFO -> Severity.Info
    LogLevel.WARN -> Severity.Warn
    LogLevel.ERROR -> Severity.Error
    LogLevel.ASSERT -> Severity.Assert
}
