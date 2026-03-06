package com.dqc.kit.network.data

import com.dqc.kit.network.auth.TokenProvider

/**
 * Authentication exception types
 */
sealed class AuthException : Exception() {
    data class Unauthorized(override val message: String = "Unauthorized") : AuthException()
    data class TokenExpired(override val message: String = "Token expired") : AuthException()
    data class RefreshFailed(override val message: String = "Token refresh failed") : AuthException()
}