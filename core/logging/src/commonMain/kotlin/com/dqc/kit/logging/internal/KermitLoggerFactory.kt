package com.dqc.kit.logging.internal

import com.dqc.kit.logging.api.LogLevel
import com.dqc.kit.logging.api.Logger
import com.dqc.kit.logging.api.LoggerConfig
import com.dqc.kit.logging.api.LoggerFactory
import kotlin.reflect.KClass

/**
 * LoggerFactory 的 Kermit 实现
 */
class KermitLoggerFactory : LoggerFactory {

    private var defaultConfig: LoggerConfig = LoggerConfig()
    private val loggerCache = mutableMapOf<String, Logger>()

    override fun create(tag: String): Logger {
        return loggerCache.getOrPut(tag) {
            KermitLogger(
                tag = tag,
                isEnabled = defaultConfig.isEnabled,
                minLogLevel = defaultConfig.minLogLevel
            )
        }
    }

    override fun create(clazz: KClass<*>): Logger {
        val tag = clazz.simpleName ?: "UnknownClass"
        return create(tag)
    }

    override fun getDefault(): Logger {
        return create("App")
    }

    override fun setDefaultConfig(config: LoggerConfig) {
        defaultConfig = config
        // 更新缓存中的 logger 配置
        loggerCache.values.forEach { logger ->
            if (logger is KermitLogger) {
                logger.isEnabled = config.isEnabled
                logger.minLogLevel = config.minLogLevel
            }
        }
    }
}

/**
 * 简单的 LoggerFactory 实现，不使用缓存
 * 适用于需要每次都创建新实例的场景
 */
internal class SimpleKermitLoggerFactory : LoggerFactory {

    private var defaultConfig: LoggerConfig = LoggerConfig()

    override fun create(tag: String): Logger {
        return KermitLogger(
            tag = tag,
            isEnabled = defaultConfig.isEnabled,
            minLogLevel = defaultConfig.minLogLevel
        )
    }

    override fun create(clazz: KClass<*>): Logger {
        val tag = clazz.simpleName ?: "UnknownClass"
        return create(tag)
    }

    override fun getDefault(): Logger {
        return create("App")
    }

    override fun setDefaultConfig(config: LoggerConfig) {
        defaultConfig = config
    }
}
