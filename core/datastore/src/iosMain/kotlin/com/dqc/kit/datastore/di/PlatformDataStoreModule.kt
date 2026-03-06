package com.dqc.kit.datastore.di

import com.dqc.kit.datastore.data.datasource.IOSPreferencesDataSource
import com.dqc.kit.datastore.data.datasource.PreferencesDataSource
import com.dqc.kit.datastore.data.repository.PreferencesRepositoryImpl
import com.dqc.kit.datastore.domain.repository.PreferencesRepository
import org.koin.dsl.module

/**
 * iOS Koin module for NSUserDefaults
 */
actual fun platformDataStoreModule() = module {
    single<PreferencesDataSource> { 
        IOSPreferencesDataSource() 
    }
    single<PreferencesRepository> { 
        PreferencesRepositoryImpl(get()) 
    }
}