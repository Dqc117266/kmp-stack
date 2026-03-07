package com.timelinesolutions.kmpstack.core.database.di

import com.timelinesolutions.kmpstack.core.database.AppDatabase
import com.timelinesolutions.kmpstack.core.database.DatabaseDriverFactory
import com.timelinesolutions.kmpstack.core.database.datasource.ProjectDataSource
import com.timelinesolutions.kmpstack.core.database.datasource.ProjectDataSourceImpl
import com.timelinesolutions.kmpstack.core.database.datasource.UserDataSource
import com.timelinesolutions.kmpstack.core.database.datasource.UserDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS 平台数据库模块
 */
actual fun databaseModule(): Module = module {
    single { DatabaseDriverFactory() }
    single { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
    
    // 数据源
    single<UserDataSource> { UserDataSourceImpl(get()) }
    single<ProjectDataSource> { ProjectDataSourceImpl(get()) }
}
