package com.dqc.kit.network.auth

import com.dqc.kit.datastore.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * Token provider implementation using DataStore/Preferences
 * Stores tokens securely in platform-specific storage:
 * - Android: Encrypted DataStore
 * - iOS: NSUserDefaults (with Keychain for sensitive data in production)
 * - JVM: Java Preferences API
 */
class PreferencesTokenProvider(
    private val preferences: PreferencesRepository
) : TokenProvider {

    companion object {
        private const val KEY_ACCESS_TOKEN = "auth_access_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        private const val KEY_TOKEN_EXPIRES_AT = "auth_token_expires_at"
    }

    private val mutex = Mutex()
    private val _authState = MutableStateFlow(false)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    init {
        // Initialize auth state from stored token
        CoroutineScope(Dispatchers.Default).launch {
            _authState.value = isAuthenticated()
        }
    }

    override suspend fun getAccessToken(): String? {
        return preferences.getString(KEY_ACCESS_TOKEN, "")
            .takeIf { it.isNotEmpty() }
    }

    override suspend fun getRefreshToken(): String? {
        return preferences.getString(KEY_REFRESH_TOKEN, "")
            .takeIf { it.isNotEmpty() }
    }

    override suspend fun refreshToken(): String? {
        return mutex.withLock {
            val refreshToken = getRefreshToken() ?: return@withLock null

            try {
                // Call your refresh token API here
                // val response = authApi.refreshToken(refreshToken)
                // saveTokens(response.accessToken, refreshToken)
                // response.accessToken

                // Placeholder implementation - replace with actual API call
                null
            } catch (e: Exception) {
                // Refresh failed, clear tokens
                clearTokens()
                null
            }
        }
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        mutex.withLock {
            preferences.putString(KEY_ACCESS_TOKEN, accessToken)
            refreshToken?.let {
                preferences.putString(KEY_REFRESH_TOKEN, it)
            }
            _authState.value = true
        }
    }

    override suspend fun clearTokens() {
        mutex.withLock {
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
            preferences.remove(KEY_TOKEN_EXPIRES_AT)
            _authState.value = false
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }

    /**
     * Get auth state as Flow for UI observation
     */
    fun observeAuthState(): StateFlow<Boolean> = authState

    /**
     * Check if token is expired (if expiration time is stored)
     */
    suspend fun isTokenExpired(): Boolean {
        val expiresAt = preferences.getLong(KEY_TOKEN_EXPIRES_AT, 0L)
        return expiresAt > 0 && Clock.System.now().toEpochMilliseconds() >= expiresAt
    }

    /**
     * Save token with expiration time
     */
    suspend fun saveTokensWithExpiry(
        accessToken: String,
        refreshToken: String?,
        expiresInSeconds: Long
    ) {
        mutex.withLock {
            preferences.putString(KEY_ACCESS_TOKEN, accessToken)
            refreshToken?.let {
                preferences.putString(KEY_REFRESH_TOKEN, it)
            }
            val expiresAt = Clock.System.now().toEpochMilliseconds() + (expiresInSeconds * 1000)
            preferences.putLong(KEY_TOKEN_EXPIRES_AT, expiresAt)
            _authState.value = true
        }
    }
}
