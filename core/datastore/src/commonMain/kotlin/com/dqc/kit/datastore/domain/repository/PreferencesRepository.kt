package com.dqc.kit.datastore.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Key-Value storage repository interface
 * Works across all platforms (Android DataStore, iOS NSUserDefaults, JVM Preferences)
 */
interface PreferencesRepository {
    
    // String operations
    suspend fun getString(key: String, defaultValue: String = ""): String
    fun getStringFlow(key: String, defaultValue: String = ""): Flow<String>
    suspend fun putString(key: String, value: String)
    
    // Int operations
    suspend fun getInt(key: String, defaultValue: Int = 0): Int
    fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int>
    suspend fun putInt(key: String, value: Int)
    
    // Long operations
    suspend fun getLong(key: String, defaultValue: Long = 0L): Long
    fun getLongFlow(key: String, defaultValue: Long = 0L): Flow<Long>
    suspend fun putLong(key: String, value: Long)
    
    // Boolean operations
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean>
    suspend fun putBoolean(key: String, value: Boolean)
    
    // Float operations
    suspend fun getFloat(key: String, defaultValue: Float = 0f): Float
    fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float>
    suspend fun putFloat(key: String, value: Float)
    
    // Double operations
    suspend fun getDouble(key: String, defaultValue: Double = 0.0): Double
    fun getDoubleFlow(key: String, defaultValue: Double = 0.0): Flow<Double>
    suspend fun putDouble(key: String, value: Double)
    
    // Common operations
    suspend fun remove(key: String)
    suspend fun clear()
    suspend fun contains(key: String): Boolean
    suspend fun getAll(): Map<String, Any?>
}