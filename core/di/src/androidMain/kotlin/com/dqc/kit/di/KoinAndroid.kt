package com.dqc.kit.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * Android 平台 Koin 扩展
 *
 * 提供 Android 特定的 Koin 初始化配置，包括：
 * - Android Context 注入
 * - Android 日志支持
 * - 平台特定模块注入
 */

/**
 * 在 Android 上初始化 Koin
 *
 * @param context Android Context
 * @param isDebug 是否开启调试模式（控制日志级别）
 * @param baseUrl API 基础 URL
 * @param enableAuth 是否启用认证
 * @param additionalModules 额外的 Feature 模块
 * @return KoinApplication 实例
 *
 * 使用示例：
 * ```kotlin
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *
 *         initKoinAndroid(
 *             context = this,
 *             isDebug = BuildConfig.DEBUG,
 *             baseUrl = "https://api.example.com",
 *             additionalModules = listOf(
 *                 homeModule,
 *                 profileModule
 *             )
 *         )
 *     }
 * }
 * ```
 */
fun initKoinAndroid(
    context: Context,
    isDebug: Boolean = false,
    baseUrl: String,
    enableAuth: Boolean = true,
    additionalModules: List<Module> = emptyList()
): KoinApplication {
    return DIHelper.initKoin(
        baseUrl = baseUrl,
        isDebug = isDebug,
        enableAuth = enableAuth,
        additionalModules = additionalModules,
        appDeclaration = {
            // 注入 Android Context
            androidContext(context)

            // 配置日志（仅在 Debug 模式启用）
            if (isDebug) {
                androidLogger(Level.DEBUG)
            } else {
                androidLogger(Level.ERROR)
            }
        }
    )
}

/**
 * 带完整配置的 Android 初始化
 *
 * @param context Android Context
 * @param config 核心模块配置
 * @param additionalModules 额外的 Feature 模块
 * @return KoinApplication 实例
 */
fun initKoinAndroid(
    context: Context,
    config: CoreModuleConfig,
    additionalModules: List<Module> = emptyList()
): KoinApplication {
    return DIHelper.initKoin(
        config = config,
        additionalModules = additionalModules,
        appDeclaration = {
            // 注入 Android Context
            androidContext(context)

            // 配置日志
            if (config.isDebug) {
                androidLogger(Level.DEBUG)
            } else {
                androidLogger(Level.ERROR)
            }
        }
    )
}

/**
 * 灵活的 Android 初始化（使用 KoinAppDeclaration）
 *
 * @param context Android Context
 * @param isDebug 是否开启调试模式
 * @param baseUrl API 基础 URL
 * @param enableAuth 是否启用认证
 * @param additionalModules 额外的 Feature 模块
 * @param appDeclaration 额外的 Koin 配置
 * @return KoinApplication 实例
 */
fun initKoinAndroid(
    context: Context,
    isDebug: Boolean = false,
    baseUrl: String,
    enableAuth: Boolean = true,
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication {
    return DIHelper.initKoin(
        baseUrl = baseUrl,
        isDebug = isDebug,
        enableAuth = enableAuth,
        additionalModules = additionalModules,
        appDeclaration = {
            // 注入 Android Context
            androidContext(context)

            // 配置日志
            if (isDebug) {
                androidLogger(Level.DEBUG)
            } else {
                androidLogger(Level.ERROR)
            }

            // 应用用户自定义配置
            appDeclaration()
        }
    )
}
