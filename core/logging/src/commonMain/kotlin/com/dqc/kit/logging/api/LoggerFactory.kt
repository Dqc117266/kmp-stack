package com.dqc.kit.logging.api

import kotlin.reflect.KClass

/**
 * Logger 工厂接口
 * 用于创建 Logger 实例，遵循工厂模式
 */
interface LoggerFactory {

    /**
     * 创建指定标签的 Logger
     *
     * @param tag 日志标签，通常使用类名或模块名
     * @return Logger 实例
     */
    fun create(tag: String): Logger

    /**
     * 使用类名作为标签创建 Logger
     *
     * @param clazz 用于获取标签的类
     * @return Logger 实例
     */
    fun create(clazz: KClass<*>): Logger

    /**
     * 获取全局默认 Logger
     *
     * @return 默认 Logger 实例
     */
    fun getDefault(): Logger

    /**
     * 设置全局默认 Logger 配置
     *
     * @param config Logger 配置
     */
    fun setDefaultConfig(config: LoggerConfig)
}

/**
 * Logger 配置数据类
 */
data class LoggerConfig(
    val isEnabled: Boolean = true,
    val minLogLevel: LogLevel = LogLevel.DEBUG,
    val includeThreadInfo: Boolean = false,
    val includeTimestamp: Boolean = false
)
