package com.timelinesolutions.kmpstack.core.database.di

import com.timelinesolutions.kmpstack.core.database.AppDatabase
import com.timelinesolutions.kmpstack.core.database.DatabaseDriverFactory
import com.timelinesolutions.kmpstack.core.database.datasource.UserDataSource
import com.timelinesolutions.kmpstack.core.database.datasource.UserDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * 数据库模块
 * 提供数据库相关的依赖注入
 */
expect fun databaseModule(): Module
