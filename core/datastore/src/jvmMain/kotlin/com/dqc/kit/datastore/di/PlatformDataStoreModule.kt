package com.dqc.kit.datastore.di

import com.dqc.kit.datastore.data.datasource.JvmPreferencesDataSource
import com.dqc.kit.datastore.data.datasource.PreferencesDataSource
import com.dqc.kit.datastore.data.repository.PreferencesRepositoryImpl
import com.dqc.kit.datastore.domain.repository.PreferencesRepository
import org.koin.dsl.module

/**
 * JVM Koin module for Preferences
 */
actual fun platformDataStoreModule() = module {
    single<PreferencesDataSource> { 
        JvmPreferencesDataSource() 
    }
    single<PreferencesRepository> { 
        PreferencesRepositoryImpl(get()) 
    }
}