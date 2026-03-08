package com.timelinesolutions.kmpstack.core.database.di

import android.content.Context
import com.timelinesolutions.kmpstack.core.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android 平台数据库模块
 *
 * 提供的依赖：
 * - DatabaseDriverFactory: 需要 Context 来创建数据库驱动
 */
actual fun databaseModule(): Module = module {
    single { DatabaseDriverFactory(get<Context>()) }
}
