package com.dqc.kit.di

import com.dqc.kit.data.di.coreDataModule
import com.dqc.kit.datastore.di.dataStoreModule
import com.dqc.kit.network.di.coreNetworkModule
import com.timelinesolutions.kmpstack.core.database.di.databaseModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 核心模块汇总
 *
 * 此文件汇总所有底层核心模块的 Koin Module，作为统一的依赖注入入口。
 * 壳工程只需引入 core:di 模块，即可获得所有基础层的依赖声明。
 *
 * 模块清单：
 * - Domain: 纯净的实体和接口（无 DI 模块，纯 Kotlin）
 * - Data: Repository 实现、数据源
 * - Network: HTTP 客户端、Auth 管理
 * - DataStore: 键值对存储
 * - Database: SQLDelight 数据库
 * - Logging: Kermit 日志
 *
 * 使用方式：
 * ```kotlin
 * // 在 commonMain 中
 * DIHelper.initKoin {
 *     modules(coreModules + featureModules)
 * }
 * ```
 */

/**
 * 核心数据模块配置
 *
 * @param basePath API 基础路径（默认 /api/v1）
 * @param baseUrl API 基础 URL（用于 Network）
 * @param isDebug 是否开启调试模式
 * @param enableAuth 是否启用认证功能
 */
class CoreModuleConfig(
    val basePath: String = "/api/v1",
    val baseUrl: String,
    val isDebug: Boolean = false,
    val enableAuth: Boolean = true
)

/**
 * 获取核心模块列表
 *
 * @param config 核心模块配置
 * @return 核心模块列表
 */
fun coreModules(config: CoreModuleConfig): List<Module> = listOf(
    // DataStore - 键值对存储（平台特定）
    dataStoreModule(),

    // Database - SQLDelight 数据库（平台特定）
    databaseModule(),

    // Network - HTTP 客户端和认证管理
    coreNetworkModule(
        baseUrl = config.baseUrl,
        isDebug = config.isDebug,
        enableAuth = config.enableAuth
    ),

    // Data - Repository 和数据源
    coreDataModule(basePath = config.basePath),

    // Core common utilities
    coreCommonModule()
)

/**
 * 核心通用模块
 * 提供跨平台的通用工具类依赖
 */
internal fun coreCommonModule(): Module = module {
    // 可在此处添加通用的工具类依赖
    // 例如：日期格式化器、JSON 解析器等
}

/**
 * 简化版核心模块列表（使用默认配置）
 *
 * @param baseUrl API 基础 URL（必填）
 * @param isDebug 是否开启调试模式（默认 false）
 * @return 核心模块列表
 */
fun coreModules(
    baseUrl: String,
    isDebug: Boolean = false,
    enableAuth: Boolean = true
): List<Module> = coreModules(
    CoreModuleConfig(
        baseUrl = baseUrl,
        isDebug = isDebug,
        enableAuth = enableAuth
    )
)
