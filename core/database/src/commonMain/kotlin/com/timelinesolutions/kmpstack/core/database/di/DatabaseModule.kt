package com.timelinesolutions.kmpstack.core.database.di

import com.timelinesolutions.kmpstack.core.database.DatabaseConfig
import com.timelinesolutions.kmpstack.core.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 数据库模块
 * 提供数据库基础设施相关的依赖注入
 *
 * 此模块只提供通用的数据库基础设施，不包含任何业务相关的数据源。
 * 其他模块需要基于这些基础设施创建自己的数据库和数据源。
 *
 * 提供的依赖：
 * - DatabaseDriverFactory: 数据库驱动工厂（平台特定）
 * - DatabaseConfig: 数据库配置
 *
 * 使用示例：
 * ```kotlin
 * // 在其他模块中创建自己的数据库
 * val myModule = module {
 *     single {
 *         val driverFactory = get<DatabaseDriverFactory>()
 *         val driver = driverFactory.createDriver(MyDatabase.Schema, "my_db.db")
 *         MyDatabase(driver)
 *     }
 *
 *     single<MyDataSource> { MyDataSourceImpl(get()) }
 * }
 * ```
 */
expect fun databaseModule(): Module

/**
 * 通用数据库配置模块
 * 可以在任何模块中使用
 */
val databaseConfigModule = module {
    single { DatabaseConfig.DEFAULT }
}
