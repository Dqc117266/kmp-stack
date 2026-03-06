package com.dqc.kit.datastore.di

import com.dqc.kit.datastore.data.datasource.AndroidPreferencesDataSource
import com.dqc.kit.datastore.data.datasource.PreferencesDataSource
import com.dqc.kit.datastore.data.repository.PreferencesRepositoryImpl
import com.dqc.kit.datastore.domain.repository.PreferencesRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android Koin module for DataStore
 */
actual fun platformDataStoreModule() = module {
    single<PreferencesDataSource> { 
        AndroidPreferencesDataSource(androidContext()) 
    }
    single<PreferencesRepository> { 
        PreferencesRepositoryImpl(get()) 
    }
}