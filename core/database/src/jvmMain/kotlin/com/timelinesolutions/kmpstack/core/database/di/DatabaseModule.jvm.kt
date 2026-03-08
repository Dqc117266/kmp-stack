package com.timelinesolutions.kmpstack.core.database.di

import com.timelinesolutions.kmpstack.core.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * JVM 平台数据库模块
 *
 * 提供的依赖：
 * - DatabaseDriverFactory: 创建数据库驱动
 */
actual fun databaseModule(): Module = module {
    single { DatabaseDriverFactory() }
}
