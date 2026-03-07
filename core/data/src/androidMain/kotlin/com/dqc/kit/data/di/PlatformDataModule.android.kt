package com.dqc.kit.data.di

import android.content.Context
import com.dqc.kit.data.local.database.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android 平台特定的 Data 模块
 * 提供需要 Context 的依赖
 */
fun androidDataModule(): Module = module {
    // 提供 Context 给 DatabaseDriverFactory
    single {
        DatabaseDriverFactory(androidContext())
    }
}
