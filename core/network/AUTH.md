# Authentication Guide

## Quick Setup

```kotlin
// 1. Initialize with auth enabled
startKoin {
    modules(
        coreNetworkModule(
            baseUrl = "https://api.example.com/",
            isDebug = BuildConfig.DEBUG,
            enableAuth = true  // Enable authentication
        )
    )
}

// 2. Inject and use AuthManager
val authManager: AuthManager by inject()

// Login
authManager.login("user", "password")

// Check auth state
val isLoggedIn = authManager.isLoggedIn()

// Logout
authManager.logout()
```

## TokenProvider Interface

```kotlin
interface TokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun refreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String?)
    suspend fun clearTokens()
    suspend fun isAuthenticated(): Boolean
}
```

## Custom Token Storage

```kotlin
// Implement your own TokenProvider
class SecureTokenProvider : TokenProvider {
    
    private val settings: Settings = createSettings()
    
    override suspend fun getAccessToken(): String? {
        return settings.getStringOrNull("access_token")
    }
    
    override suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        settings["access_token"] = accessToken
        settings["refresh_token"] = refreshToken
    }
    
    // ... implement other methods
}

// Use in DI
fun coreNetworkModule(baseUrl: String, isDebug: Boolean) = module {
    single<TokenProvider> { SecureTokenProvider() }
    // ... rest of configuration
}
```

## API with Auth

```kotlin
class UserRepository : BaseRepository(), KoinComponent {
    private val client: HttpClient by inject()
    
    // Requests automatically include Authorization header
    suspend fun getProfile() = safeApiCall {
        client.get("user/profile").body<User>()
    }
}
```

## Auth Flow

1. **Login** → `AuthManager.login()` → Save tokens
2. **API Call** → Auto-add `Authorization: Bearer {token}`
3. **401 Response** → Auto-refresh token → Retry request
4. **Refresh Failed** → Clear tokens → Return 401 error
5. **Logout** → `AuthManager.logout()` → Clear tokens