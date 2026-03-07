package com.dqc.kit.data.di

import com.dqc.kit.data.local.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * JVM 平台特定的 Data 模块
 * 适用于桌面应用
 */
fun jvmDataModule(): Module = module {
    // JVM 的 DatabaseDriverFactory 不需要参数
    single { DatabaseDriverFactory() }
}
