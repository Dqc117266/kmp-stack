package com.dqc.kit.network.auth

/**
 * Token provider interface for authentication
 */
interface TokenProvider {
    /**
     * Get current access token
     * @return token string or null if not logged in
     */
    suspend fun getAccessToken(): String?
    
    /**
     * Get current refresh token
     * @return refresh token string or null
     */
    suspend fun getRefreshToken(): String?
    
    /**
     * Refresh access token using refresh token
     * @return new access token or null if refresh failed
     */
    suspend fun refreshToken(): String?
    
    /**
     * Save new tokens after login or refresh
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String?)
    
    /**
     * Clear tokens on logout
     */
    suspend fun clearTokens()
    
    /**
     * Check if user is authenticated
     */
    suspend fun isAuthenticated(): Boolean
}