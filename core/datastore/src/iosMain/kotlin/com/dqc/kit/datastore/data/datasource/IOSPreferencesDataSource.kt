package com.dqc.kit.datastore.data.datasource

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

/**
 * iOS NSUserDefaults implementation
 */
internal class IOSPreferencesDataSource : PreferencesDataSource {
    
    private val defaults = NSUserDefaults.standardUserDefaults()
    private val flows = mutableMapOf<String, MutableStateFlow<Any?>>()
    
    // String
    override suspend fun getString(key: String, defaultValue: String): String {
        return defaults.stringForKey(key) ?: defaultValue
    }
    
    override fun getStringFlow(key: String, defaultValue: String): Flow<String> {
        return getOrCreateFlow(key, defaults.stringForKey(key) ?: defaultValue).asStateFlow() as Flow<String>
    }
    
    override suspend fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
        updateFlow(key, value)
    }
    
    // Int
    override suspend fun getInt(key: String, defaultValue: Int): Int {
        return if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key).toInt()
        } else defaultValue
    }
    
    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
        val value = if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key).toInt()
        } else defaultValue
        return getOrCreateFlow(key, value).asStateFlow() as Flow<Int>
    }
    
    override suspend fun putInt(key: String, value: Int) {
        defaults.setInteger(value.toLong(), forKey = key)
        updateFlow(key, value)
    }
    
    // Long
    override suspend fun getLong(key: String, defaultValue: Long): Long {
        return if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key)
        } else defaultValue
    }
    
    override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> {
        val value = if (defaults.objectForKey(key) != null) {
            defaults.integerForKey(key)
        } else defaultValue
        return getOrCreateFlow(key, value).asStateFlow() as Flow<Long>
    }
    
    override suspend fun putLong(key: String, value: Long) {
        defaults.setInteger(value, forKey = key)
        updateFlow(key, value)
    }
    
    // Boolean
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else defaultValue
    }
    
    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
        val value = if (defaults.objectForKey(key) != null) {
            defaults.boolForKey(key)
        } else defaultValue
        return getOrCreateFlow(key, value).asStateFlow() as Flow<Boolean>
    }
    
    override suspend fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, forKey = key)
        updateFlow(key, value)
    }
    
    // Float
    override suspend fun getFloat(key: String, defaultValue: Float): Float {
        return if (defaults.objectForKey(key) != null) {
            defaults.floatForKey(key)
        } else defaultValue
    }
    
    override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> {
        val value = if (defaults.objectForKey(key) != null) {
            defaults.floatForKey(key)
        } else defaultValue
        return getOrCreateFlow(key, value).asStateFlow() as Flow<Float>
    }
    
    override suspend fun putFloat(key: String, value: Float) {
        defaults.setFloat(value, forKey = key)
        updateFlow(key, value)
    }
    
    // Double
    override suspend fun getDouble(key: String, defaultValue: Double): Double {
        return if (defaults.objectForKey(key) != null) {
            defaults.doubleForKey(key)
        } else defaultValue
    }
    
    override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> {
        val value = if (defaults.objectForKey(key) != null) {
            defaults.doubleForKey(key)
        } else defaultValue
        return getOrCreateFlow(key, value).asStateFlow() as Flow<Double>
    }
    
    override suspend fun putDouble(key: String, value: Double) {
        defaults.setDouble(value, forKey = key)
        updateFlow(key, value)
    }
    
    // Common
    override suspend fun remove(key: String) {
        defaults.removeObjectForKey(key)
        updateFlow(key, null)
    }
    
    override suspend fun clear() {
        defaults.dictionaryRepresentation().keys.forEach { key ->
            defaults.removeObjectForKey(key as String)
        }
        flows.clear()
    }
    
    override suspend fun contains(key: String): Boolean {
        return defaults.objectForKey(key) != null
    }
    
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getAll(): Map<String, Any?> {
        return defaults.dictionaryRepresentation().mapKeys { it.key as String }
    }
    
    private fun <T> getOrCreateFlow(key: String, initialValue: T): MutableStateFlow<T> {
        @Suppress("UNCHECKED_CAST")
        return flows.getOrPut(key) { MutableStateFlow(initialValue) } as MutableStateFlow<T>
    }
    
    private fun updateFlow(key: String, value: Any?) {
        flows[key]?.value = value
    }
}