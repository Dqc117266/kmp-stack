package com.dqc.kit.datastore.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.prefs.Preferences

/**
 * JVM Preferences implementation
 * Uses Java Preferences API for Desktop
 */
internal class JvmPreferencesDataSource(
    private val nodeName: String = "com.dqc.kit.datastore"
) : PreferencesDataSource {
    
    private val preferences: Preferences = Preferences.userRoot().node(nodeName)
    private val flows = mutableMapOf<String, MutableStateFlow<Any?>>()
    
    // String
    override suspend fun getString(key: String, defaultValue: String): String {
        return preferences.get(key, defaultValue)
    }
    
    override suspend fun getStringFlow(key: String, defaultValue: String): Flow<String> {
        return getOrCreateFlow(key, getString(key, defaultValue)).asStateFlow() as Flow<String>
    }
    
    override suspend fun putString(key: String, value: String) {
        preferences.put(key, value)
        preferences.flush()
        updateFlow(key, value)
    }
    
    // Int
    override suspend fun getInt(key: String, defaultValue: Int): Int {
        return preferences.getInt(key, defaultValue)
    }
    
    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
        return getOrCreateFlow(key, getInt(key, defaultValue)).asStateFlow() as Flow<Int>
    }
    
    override suspend fun putInt(key: String, value: Int) {
        preferences.putInt(key, value)
        preferences.flush()
        updateFlow(key, value)
    }
    
    // Long
    override suspend fun getLong(key: String, defaultValue: Long): Long {
        return preferences.getLong(key, defaultValue)
    }
    
    override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> {
        return getOrCreateFlow(key, getLong(key, defaultValue)).asStateFlow() as Flow<Long>
    }
    
    override suspend fun putLong(key: String, value: Long) {
        preferences.putLong(key, value)
        preferences.flush()
        updateFlow(key, value)
    }
    
    // Boolean
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }
    
    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
        return getOrCreateFlow(key, getBoolean(key, defaultValue)).asStateFlow() as Flow<Boolean>
    }
    
    override suspend fun putBoolean(key: String, value: Boolean) {
        preferences.putBoolean(key, value)
        preferences.flush()
        updateFlow(key, value)
    }
    
    // Float
    override suspend fun getFloat(key: String, defaultValue: Float): Float {
        return preferences.getFloat(key, defaultValue)
    }
    
    override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> {
        return getOrCreateFlow(key, getFloat(key, defaultValue)).asStateFlow() as Flow<Float>
    }
    
    override suspend fun putFloat(key: String, value: Float) {
        preferences.putFloat(key, value)
        preferences.flush()
        updateFlow(key, value)
    }
    
    // Double
    override suspend fun getDouble(key: String, defaultValue: Double): Double {
        return preferences.getDouble(key, defaultValue)
    }
    
    override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> {
        return getOrCreateFlow(key, getDouble(key, defaultValue)).asStateFlow() as Flow<Double>
    }
    
    override suspend fun putDouble(key: String, value: Double) {
        preferences.putDouble(key, value)
        preferences.flush()
        updateFlow(key, value)
    }
    
    // Common
    override suspend fun remove(key: String) {
        preferences.remove(key)
        preferences.flush()
        updateFlow(key, null)
    }
    
    override suspend fun clear() {
        preferences.clear()
        preferences.flush()
        flows.clear()
    }
    
    override suspend fun contains(key: String): Boolean {
        return preferences.get(key, null) != null
    }
    
    override suspend fun getAll(): Map<String, Any?> {
        return preferences.keys().associateWith { key ->
            preferences.get(key, null)
        }
    }
    
    private fun <T> getOrCreateFlow(key: String, initialValue: T): MutableStateFlow<T> {
        @Suppress("UNCHECKED_CAST")
        return flows.getOrPut(key) { MutableStateFlow(initialValue) } as MutableStateFlow<T>
    }
    
    private fun updateFlow(key: String, value: Any?) {
        flows[key]?.value = value
    }
}