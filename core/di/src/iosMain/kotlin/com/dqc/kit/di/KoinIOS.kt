package com.dqc.kit.di

import org.koin.core.KoinApplication
import org.koin.core.module.Module

/**
 * iOS 平台 Koin 扩展
 *
 * 为 Swift 提供零参数或简单参数的启动桥接函数，
 * 规避 Swift 对 Kotlin Lambda 表达式支持不佳的问题。
 */

/**
 * iOS 基础初始化（无参数版本）
 *
 * 用于 Swift 侧最简单的初始化场景。
 * 注意：需要在 Swift 侧手动配置模块
 *
 * Swift 使用示例：
 * ```swift
 * import shared
 *
 * func application(_ application: UIApplication,
 *                  didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
 *     KoinIOSKt.doInitKoinBase()
 *     return true
 * }
 * ```
 */
fun initKoinBase(): KoinApplication {
    return DIHelper.initKoin()
}

/**
 * iOS 标准初始化（带基础 URL）
 *
 * @param baseUrl API 基础 URL
 * @param isDebug 是否开启调试模式（默认 false）
 * @return KoinApplication 实例
 *
 * Swift 使用示例：
 * ```swift
 * import shared
 *
 * func application(_ application: UIApplication,
 *                  didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
 *     KoinIOSKt.doInitKoinIos(
 *         baseUrl: "https://api.example.com",
 *         isDebug: true
 *     )
 *     return true
 * }
 * ```
 */
fun initKoinIos(
    baseUrl: String,
    isDebug: Boolean = false
): KoinApplication {
    return DIHelper.initKoin(
        baseUrl = baseUrl,
        isDebug = isDebug
    )
}

/**
 * iOS 完整初始化（带认证配置）
 *
 * @param baseUrl API 基础 URL
 * @param isDebug 是否开启调试模式
 * @param enableAuth 是否启用认证
 * @return KoinApplication 实例
 */
fun initKoinIos(
    baseUrl: String,
    isDebug: Boolean = false,
    enableAuth: Boolean = true
): KoinApplication {
    return DIHelper.initKoin(
        baseUrl = baseUrl,
        isDebug = isDebug,
        enableAuth = enableAuth
    )
}

/**
 * iOS 灵活初始化（带额外模块）
 *
 * @param baseUrl API 基础 URL
 * @param isDebug 是否开启调试模式
 * @param enableAuth 是否启用认证
 * @param additionalModules 额外模块列表
 * @return KoinApplication 实例
 *
 * Swift 使用示例：
 * ```swift
 * import shared
 *
 * func application(_ application: UIApplication,
 *                  didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
 *     KoinIOSKt.doInitKoinIosWithModules(
 *         baseUrl: "https://api.example.com",
 *         isDebug: true,
 *         enableAuth: true,
 *         additionalModules: [homeModule, profileModule]
 *     )
 *     return true
 * }
 * ```
 */
fun initKoinIosWithModules(
    baseUrl: String,
    isDebug: Boolean = false,
    enableAuth: Boolean = true,
    additionalModules: List<Module> = emptyList()
): KoinApplication {
    return DIHelper.initKoin(
        baseUrl = baseUrl,
        isDebug = isDebug,
        enableAuth = enableAuth,
        additionalModules = additionalModules
    )
}

/**
 * 获取 Koin 实例（供 Swift 使用）
 *
 * Swift 使用示例：
 * ```swift
 * let koin = KoinIOSKt.getKoin()
 * let repository: UserRepository = koin.get()
 * ```
 */
fun getKoin(): org.koin.core.Koin {
    return org.koin.core.context.GlobalContext.get()
}

/**
 * 通过类获取依赖（更 Swift 友好的 API）
 *
 * @param clazz 要获取的类
 * @return 依赖实例
 *
 * Swift 使用示例：
 * ```swift
 * let repository = KoinIOSKt.get(clazz: UserRepository.self) as! UserRepository
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <T> get(clazz: kotlin.reflect.KClass<*>): T {
    return org.koin.core.context.GlobalContext.get().get(clazz) as T
}
