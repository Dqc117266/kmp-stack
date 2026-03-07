package com.dqc.kit.network.example

import com.dqc.kit.network.auth.AuthManager
import com.dqc.kit.network.data.NetworkResult
import com.dqc.kit.network.data.util.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// ============================================================================
// EXAMPLE: Complete authentication flow implementation
// ============================================================================

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class LoginResponse(val accessToken: String, val refreshToken: String, val user: UserDto)

@Serializable
data class UserDto(val id: String, val name: String, val email: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class RefreshTokenResponse(val accessToken: String)

/**
 * Example: Auth API for login/logout
 */
class AuthApi(private val client: HttpClient) {
    
    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("auth/login") {
            setBody(request)
        }.body()
    }
    
    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse {
        return client.post("auth/refresh") {
            setBody(RefreshTokenRequest(refreshToken))
        }.body()
    }
    
    suspend fun logout() {
        client.post("auth/logout")
    }
}

/**
 * Example: Production-ready AuthManager wrapper
 */
class ProductionAuthManager : KoinComponent {
    
    private val client: HttpClient by inject()
    private val authManager: AuthManager by inject()
    
    /**
     * Login with credentials
     */
    suspend fun login(username: String, password: String): NetworkResult<UserDto> {
        return try {
            val authApi = AuthApi(client)
            val response = authApi.login(LoginRequest(username, password))
            authManager.loginWithTokens(response.accessToken, response.refreshToken)
            NetworkResult.Success(response.user)
        } catch (e: Exception) {
            NetworkResult.Error(null, "Login failed: ${e.message}")
        }
    }
    
    /**
     * Refresh token manually (usually auto-handled by interceptor)
     */
    suspend fun refreshToken(refreshToken: String): NetworkResult<String> {
        return try {
            val authApi = AuthApi(client)
            val response = authApi.refreshToken(refreshToken)
            authManager.loginWithTokens(response.accessToken, refreshToken)
            NetworkResult.Success(response.accessToken)
        } catch (e: Exception) {
            NetworkResult.Error(null, "Token refresh failed")
        }
    }
    
    /**
     * Logout and clear tokens
     */
    suspend fun logout(): NetworkResult<Unit> {
        return try {
            val authApi = AuthApi(client)
            authApi.logout()
            authManager.logout()
            NetworkResult.Success(Unit)
        } catch (e: Exception) {
            // Still clear local tokens even if server logout fails
            authManager.logout()
            NetworkResult.Success(Unit)
        }
    }
}

/**
 * Example: Protected API with automatic auth
 */
class ProtectedRepository : KoinComponent {
    
    private val client: HttpClient by inject()
    
    /**
     * This request automatically includes Authorization header
     * Token refresh is handled automatically on 401
     */
    suspend fun getUserProfile(): NetworkResult<UserDto> = safeApiCall {
        client.get("user/profile").body<UserDto>()
    }
    
    suspend fun getDashboard(): NetworkResult<DashboardDto> = safeApiCall {
        client.get("dashboard").body<DashboardDto>()
    }
    
    suspend fun updateProfile(name: String, email: String): NetworkResult<UserDto> = safeApiCall {
        client.post("user/profile") {
            setBody(mapOf("name" to name, "email" to email))
        }.body<UserDto>()
    }
}

@Serializable
data class DashboardDto(val stats: DashboardStats, val notifications: List<Notification>)

@Serializable
data class DashboardStats(val totalUsers: Int, val totalRevenue: Double)

@Serializable
data class Notification(val id: String, val title: String, val message: String)

// ============================================================================
// USAGE EXAMPLE in ViewModel
// ============================================================================

/*
class LoginViewModel : ViewModel() {
    
    private val authManager: ProductionAuthManager by inject()
    
    fun login(username: String, password: String) = viewModelScope.launch {
        _uiState.value = LoginUiState.Loading
        
        when (val result = authManager.login(username, password)) {
            is NetworkResult.Success -> {
                _uiState.value = LoginUiState.Success(result.data)
                // Navigate to home screen
            }
            is NetworkResult.Error -> {
                _uiState.value = LoginUiState.Error(result.message)
            }
        }
    }
}

class HomeViewModel : ViewModel() {
    
    private val repository: ProtectedRepository by inject()
    
    fun loadProfile() = viewModelScope.launch {
        when (val result = repository.getUserProfile()) {
            is NetworkResult.Success -> {
                // Update UI with profile data
                _profile.value = result.data
            }
            is NetworkResult.Error -> {
                if (result.code == 401) {
                    // Token expired and refresh failed
                    // Navigate to login
                }
            }
        }
    }
}
*/