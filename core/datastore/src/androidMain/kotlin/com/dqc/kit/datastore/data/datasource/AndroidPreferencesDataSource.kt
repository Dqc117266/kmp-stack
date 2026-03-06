package com.dqc.kit.datastore.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Android DataStore implementation
 */
internal class AndroidPreferencesDataSource(
    private val context: Context
) : PreferencesDataSource {
    
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")
    
    // String
    override suspend fun getString(key: String, defaultValue: String): String {
        return context.dataStore.data.map { 
            it[stringPreferencesKey(key)] ?: defaultValue 
        }.first()
    }
    
    override fun getStringFlow(key: String, defaultValue: String): Flow<String> {
        return context.dataStore.data.map { 
            it[stringPreferencesKey(key)] ?: defaultValue 
        }
    }
    
    override suspend fun putString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
    
    // Int
    override suspend fun getInt(key: String, defaultValue: Int): Int {
        return context.dataStore.data.map { 
            it[intPreferencesKey(key)] ?: defaultValue 
        }.first()
    }
    
    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
        return context.dataStore.data.map { 
            it[intPreferencesKey(key)] ?: defaultValue 
        }
    }
    
    override suspend fun putInt(key: String, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[intPreferencesKey(key)] = value
        }
    }
    
    // Long
    override suspend fun getLong(key: String, defaultValue: Long): Long {
        return context.dataStore.data.map { 
            it[longPreferencesKey(key)] ?: defaultValue 
        }.first()
    }
    
    override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> {
        return context.dataStore.data.map { 
            it[longPreferencesKey(key)] ?: defaultValue 
        }
    }
    
    override suspend fun putLong(key: String, value: Long) {
        context.dataStore.edit { preferences ->
            preferences[longPreferencesKey(key)] = value
        }
    }
    
    // Boolean
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return context.dataStore.data.map { 
            it[booleanPreferencesKey(key)] ?: defaultValue 
        }.first()
    }
    
    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
        return context.dataStore.data.map { 
            it[booleanPreferencesKey(key)] ?: defaultValue 
        }
    }
    
    override suspend fun putBoolean(key: String, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }
    
    // Float
    override suspend fun getFloat(key: String, defaultValue: Float): Float {
        return context.dataStore.data.map { 
            it[floatPreferencesKey(key)] ?: defaultValue 
        }.first()
    }
    
    override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> {
        return context.dataStore.data.map { 
            it[floatPreferencesKey(key)] ?: defaultValue 
        }
    }
    
    override suspend fun putFloat(key: String, value: Float) {
        context.dataStore.edit { preferences ->
            preferences[floatPreferencesKey(key)] = value
        }
    }
    
    // Double
    override suspend fun getDouble(key: String, defaultValue: Double): Double {
        return context.dataStore.data.map { 
            it[doublePreferencesKey(key)] ?: defaultValue 
        }.first()
    }
    
    override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> {
        return context.dataStore.data.map { 
            it[doublePreferencesKey(key)] ?: defaultValue 
        }
    }
    
    override suspend fun putDouble(key: String, value: Double) {
        context.dataStore.edit { preferences ->
            preferences[doublePreferencesKey(key)] = value
        }
    }
    
    // Common
    override suspend fun remove(key: String) {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }
    
    override suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    override suspend fun contains(key: String): Boolean {
        return context.dataStore.data.map { 
            it.contains(stringPreferencesKey(key))
        }.first()
    }
    
    override suspend fun getAll(): Map<String, Any?> {
        return context.dataStore.data.map { preferences ->
            preferences.asMap().mapKeys { it.key.name }
        }.first()
    }
}