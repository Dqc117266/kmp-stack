package com.dqc.kit.datastore.data.datasource

import kotlinx.coroutines.flow.Flow

/**
 * Platform-specific preferences data source
 * Android: DataStore implementation
 * iOS: NSUserDefaults implementation  
 * JVM: Preferences implementation
 */
internal interface PreferencesDataSource {
    suspend fun getString(key: String, defaultValue: String): String
    suspend fun getStringFlow(key: String, defaultValue: String): Flow<String>
    suspend fun putString(key: String, value: String)
    
    suspend fun getInt(key: String, defaultValue: Int): Int
    fun getIntFlow(key: String, defaultValue: Int): Flow<Int>
    suspend fun putInt(key: String, value: Int)
    
    suspend fun getLong(key: String, defaultValue: Long): Long
    fun getLongFlow(key: String, defaultValue: Long): Flow<Long>
    suspend fun putLong(key: String, value: Long)
    
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean>
    suspend fun putBoolean(key: String, value: Boolean)
    
    suspend fun getFloat(key: String, defaultValue: Float): Float
    fun getFloatFlow(key: String, defaultValue: Float): Flow<Float>
    suspend fun putFloat(key: String, value: Float)
    
    suspend fun getDouble(key: String, defaultValue: Double): Double
    fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double>
    suspend fun putDouble(key: String, value: Double)
    
    suspend fun remove(key: String)
    suspend fun clear()
    suspend fun contains(key: String): Boolean
    suspend fun getAll(): Map<String, Any?>
}