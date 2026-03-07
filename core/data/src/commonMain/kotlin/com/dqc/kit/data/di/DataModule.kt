package com.dqc.kit.data.di

import com.dqc.kit.data.local.database.AppDatabase
import com.dqc.kit.data.local.database.DatabaseDriverFactory
import com.dqc.kit.data.local.database.datasource.UserLocalDataSource
import com.dqc.kit.data.network.datasource.UserRemoteDataSource
import com.dqc.kit.data.repository.UserRepositoryImpl
import com.dqc.kit.domain.repository.UserRepository
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Core Data Module
 * 提供数据层的依赖注入
 *
 * 包含：
 * 1. 数据库驱动和实例
 * 2. 本地数据源
 * 3. 远程数据源
 * 4. Repository 实现
 *
 * @param basePath API 基础路径（默认 /api/v1）
 */
fun coreDataModule(basePath: String = "/api/v1"): Module = module {

    // ===== Database =====
    // 数据库驱动工厂（平台特定）
    single { DatabaseDriverFactory(get()) }

    // 数据库实例
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        AppDatabase(driver)
    }

    // ===== Local Data Sources =====
    single {
        UserLocalDataSource(
            database = get()
        )
    }

    // ===== Remote Data Sources =====
    single {
        UserRemoteDataSource(
            httpClient = get<HttpClient>(),
            basePath = basePath
        )
    }

    // ===== Repositories =====
    single<UserRepository> {
        UserRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get(),
            authManager = get()
        )
    }
}
