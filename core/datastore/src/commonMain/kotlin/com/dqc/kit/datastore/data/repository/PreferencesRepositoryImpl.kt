package com.dqc.kit.datastore.data.repository

import com.dqc.kit.datastore.data.datasource.PreferencesDataSource
import com.dqc.kit.datastore.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Preferences repository implementation
 * Delegates to platform-specific data source
 */
internal class PreferencesRepositoryImpl(
    private val dataSource: PreferencesDataSource
) : PreferencesRepository {
    
    // String
    override suspend fun getString(key: String, defaultValue: String): String = 
        dataSource.getString(key, defaultValue)
    
    override fun getStringFlow(key: String, defaultValue: String): Flow<String> = 
        dataSource.getStringFlow(key, defaultValue)
    
    override suspend fun putString(key: String, value: String) = 
        dataSource.putString(key, value)
    
    // Int
    override suspend fun getInt(key: String, defaultValue: Int): Int = 
        dataSource.getInt(key, defaultValue)
    
    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> = 
        dataSource.getIntFlow(key, defaultValue)
    
    override suspend fun putInt(key: String, value: Int) = 
        dataSource.putInt(key, value)
    
    // Long
    override suspend fun getLong(key: String, defaultValue: Long): Long = 
        dataSource.getLong(key, defaultValue)
    
    override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> = 
        dataSource.getLongFlow(key, defaultValue)
    
    override suspend fun putLong(key: String, value: Long) = 
        dataSource.putLong(key, value)
    
    // Boolean
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = 
        dataSource.getBoolean(key, defaultValue)
    
    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> = 
        dataSource.getBooleanFlow(key, defaultValue)
    
    override suspend fun putBoolean(key: String, value: Boolean) = 
        dataSource.putBoolean(key, value)
    
    // Float
    override suspend fun getFloat(key: String, defaultValue: Float): Float = 
        dataSource.getFloat(key, defaultValue)
    
    override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> = 
        dataSource.getFloatFlow(key, defaultValue)
    
    override suspend fun putFloat(key: String, value: Float) = 
        dataSource.putFloat(key, value)
    
    // Double
    override suspend fun getDouble(key: String, defaultValue: Double): Double = 
        dataSource.getDouble(key, defaultValue)
    
    override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> = 
        dataSource.getDoubleFlow(key, defaultValue)
    
    override suspend fun putDouble(key: String, value: Double) = 
        dataSource.putDouble(key, value)
    
    // Common
    override suspend fun remove(key: String) = dataSource.remove(key)
    override suspend fun clear() = dataSource.clear()
    override suspend fun contains(key: String): Boolean = dataSource.contains(key)
    override suspend fun getAll(): Map<String, Any?> = dataSource.getAll()
}