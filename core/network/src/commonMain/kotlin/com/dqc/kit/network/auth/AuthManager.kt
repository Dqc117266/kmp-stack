package com.dqc.kit.network.auth

import com.dqc.kit.network.data.AuthException
import kotlinx.coroutines.flow.StateFlow

/**
 * Authentication manager for handling login, logout and token operations
 * Works with TokenProvider to manage authentication state
 */
class AuthManager(private val tokenProvider: TokenProvider) {

    /**
     * Check if user is currently authenticated
     */
    suspend fun isAuthenticated(): Boolean {
        return tokenProvider.isAuthenticated()
    }

    /**
     * Get current access token
     */
    suspend fun getAccessToken(): String? {
        return tokenProvider.getAccessToken()
    }

    /**
     * Get current refresh token
     */
    suspend fun getRefreshToken(): String? {
        return tokenProvider.getRefreshToken()
    }

    /**
     * Login with tokens received from server
     * @param accessToken The access token
     * @param refreshToken The refresh token (optional)
     */
    suspend fun loginWithTokens(accessToken: String, refreshToken: String? = null) {
        tokenProvider.saveTokens(accessToken, refreshToken)
    }

    /**
     * Login with tokens and expiration time
     * @param accessToken The access token
     * @param refreshToken The refresh token (optional)
     * @param expiresInSeconds Token expiration time in seconds
     */
    suspend fun loginWithTokens(
        accessToken: String,
        refreshToken: String?,
        expiresInSeconds: Long
    ) {
        if (tokenProvider is PreferencesTokenProvider) {
            tokenProvider.saveTokensWithExpiry(accessToken, refreshToken, expiresInSeconds)
        } else {
            tokenProvider.saveTokens(accessToken, refreshToken)
        }
    }

    /**
     * Logout and clear all tokens
     */
    suspend fun logout() {
        tokenProvider.clearTokens()
    }

    /**
     * Refresh access token using refresh token
     * @return New access token or null if refresh failed
     * @throws AuthException.RefreshFailed if refresh fails
     */
    suspend fun refreshToken(): String? {
        return tokenProvider.refreshToken()
            ?: throw AuthException.RefreshFailed()
    }

    /**
     * Force refresh token even if current token is not expired
     * @return New access token or null if refresh failed
     */
    suspend fun forceRefreshToken(): String? {
        return tokenProvider.refreshToken()
    }

    /**
     * Get authentication state as Flow (for UI observation)
     * Only available when using PreferencesTokenProvider
     */
    fun observeAuthState(): StateFlow<Boolean>? {
        return (tokenProvider as? PreferencesTokenProvider)?.observeAuthState()
    }

    /**
     * Check if token is expired
     * Only available when using PreferencesTokenProvider with expiration storage
     */
    suspend fun isTokenExpired(): Boolean {
        return (tokenProvider as? PreferencesTokenProvider)?.isTokenExpired() ?: false
    }

    /**
     * Ensure valid token for API call
     * Refreshes token if expired
     * @return Valid access token or null
     */
    suspend fun ensureValidToken(): String? {
        if (isTokenExpired()) {
            return refreshToken()
        }
        return getAccessToken()
    }
}
