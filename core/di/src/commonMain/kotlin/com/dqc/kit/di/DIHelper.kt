package com.dqc.kit.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/**
 * 依赖注入帮助类
 *
 * 提供跨平台的 Koin 启动逻辑，是壳工程初始化 DI 的统一入口。
 *
 * 使用示例（commonMain）：
 * ```kotlin
 * // 基础用法
 * DIHelper.initKoin {
 *     modules(coreModules("https://api.example.com"))
 * }
 *
 * // 带 Feature 模块
 * DIHelper.initKoin {
 *     modules(
 *         coreModules(
 *             baseUrl = "https://api.example.com",
 *             isDebug = BuildConfig.DEBUG
 *         ) + listOf(
 *             homeFeatureModule,
 *             profileFeatureModule
 *         )
 *     )
 * }
 * ```
 */
object DIHelper {

    /**
     * 初始化 Koin
     *
     * @param appDeclaration Koin 应用配置（可选）
     * @param additionalModules 额外模块列表（通常是 Feature 模块）
     * @return KoinApplication 实例
     */
    fun initKoin(
        appDeclaration: KoinAppDeclaration = {},
        additionalModules: List<Module> = emptyList()
    ): KoinApplication {
        return startKoin {
            // 应用用户自定义配置
            appDeclaration()

            // 注入额外模块
            if (additionalModules.isNotEmpty()) {
                modules(additionalModules)
            }
        }
    }

    /**
     * 带配置参数的初始化
     *
     * @param config 核心模块配置
     * @param appDeclaration Koin 应用配置（可选）
     * @param additionalModules 额外模块列表（通常是 Feature 模块）
     * @return KoinApplication 实例
     */
    fun initKoin(
        config: CoreModuleConfig,
        appDeclaration: KoinAppDeclaration = {},
        additionalModules: List<Module> = emptyList()
    ): KoinApplication {
        return startKoin {
            // 注入核心模块
            modules(coreModules(config))

            // 应用用户自定义配置
            appDeclaration()

            // 注入额外模块
            if (additionalModules.isNotEmpty()) {
                modules(additionalModules)
            }
        }
    }

    /**
     * 简化版初始化（推荐用于大多数场景）
     *
     * @param baseUrl API 基础 URL
     * @param isDebug 是否开启调试模式
     * @param enableAuth 是否启用认证
     * @param additionalModules 额外模块列表
     * @return KoinApplication 实例
     */
    fun initKoin(
        baseUrl: String,
        isDebug: Boolean = false,
        enableAuth: Boolean = true,
        additionalModules: List<Module> = emptyList(),
        appDeclaration: KoinAppDeclaration = {}
    ): KoinApplication {
        return startKoin {
            // 注入核心模块
            modules(
                coreModules(
                    baseUrl = baseUrl,
                    isDebug = isDebug,
                    enableAuth = enableAuth
                )
            )

            // 应用用户自定义配置
            appDeclaration()

            // 注入额外模块
            if (additionalModules.isNotEmpty()) {
                modules(additionalModules)
            }
        }
    }
}

/**
 * 初始化 Koin（顶层函数版本）
 *
 * 适用于需要简洁 API 的场景。
 */
fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
    additionalModules: List<Module> = emptyList()
): KoinApplication {
    return DIHelper.initKoin(appDeclaration, additionalModules)
}
