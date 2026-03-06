package com.dqc.kit.network.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory token provider for demo/testing
 * In production, use encrypted storage (Keychain/Keystore)
 */
class DefaultTokenProvider : TokenProvider {
    
    private var _accessToken: String? = null
    private var _refreshToken: String? = null
    
    private val _authState = MutableStateFlow(false)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()
    
    override suspend fun getAccessToken(): String? = _accessToken
    
    override suspend fun getRefreshToken(): String? = _refreshToken
    
    override suspend fun refreshToken(): String? {
        return null
    }
    
    override suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        _accessToken = accessToken
        _refreshToken = refreshToken
        _authState.value = true
    }
    
    override suspend fun clearTokens() {
        _accessToken = null
        _refreshToken = null
        _authState.value = false
    }
    
    override suspend fun isAuthenticated(): Boolean = _accessToken != null
}