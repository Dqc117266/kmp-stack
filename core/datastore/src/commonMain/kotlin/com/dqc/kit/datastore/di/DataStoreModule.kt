package com.dqc.kit.datastore.di

import org.koin.core.module.Module

/**
 * Platform-specific DataStore module
 * Android: DataStore
 * iOS: NSUserDefaults
 * JVM: Preferences
 */
expect fun platformDataStoreModule(): Module

/**
 * Common DataStore module
 * Include this in your Koin initialization
 */
fun dataStoreModule(): Module = platformDataStoreModule()