# core:network - Ktor + Koin Clean Architecture

## Architecture

```
commonMain/
├── core/
│   ├── HttpClientEngine.kt      # expect fun getPlatformEngine()
│   ├── HttpClient.kt            # createHttpClient() with plugins
│   └── ...
├── data/
│   ├── NetworkResult.kt         # sealed class NetworkResult<T>
│   └── repository/
│       └── BaseRepository.kt    # safeApiCall wrapper
└── di/
    └── NetworkModule.kt         # coreNetworkModule()

androidMain/
└── core/
    └── HttpClientEngine.kt      # actual fun getPlatformEngine() = OkHttp

iosMain/
└── core/
    └── HttpClientEngine.kt      # actual fun getPlatformEngine() = Darwin
```

## Setup

### 1. Initialize Koin

```kotlin
import com.dqc.kit.network.di.coreNetworkModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            coreNetworkModule(
                baseUrl = "https://api.example.com/",
                isDebug = BuildConfig.DEBUG
            )
        )
    }
}
```

### 2. Create Repository

```kotlin
import com.dqc.kit.network.data.NetworkResult
import com.dqc.kit.network.data.repository.BaseRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserRepository : BaseRepository(), KoinComponent {
    
    private val client: HttpClient by inject()
    
    suspend fun getUser(id: String): NetworkResult<User> = 
        safeApiCall { 
            client.get("users/$id").body<User>()
        }
    
    suspend fun getUsers(): NetworkResult<List<User>> = 
        safeApiCall {
            client.get("users").body<List<User>>()
        }
}
```

### 3. Use in ViewModel

```kotlin
import com.dqc.kit.network.data.NetworkResult

class UserViewModel : ViewModel() {
    
    private val repository = UserRepository()
    
    fun loadUser(id: String) = viewModelScope.launch {
        when (val result = repository.getUser(id)) {
            is NetworkResult.Success -> {
                val user = result.data
                // Update UI with user
            }
            is NetworkResult.Error -> {
                val errorMessage = result.message
                // Show error
            }
        }
    }
}
```

## Features

✅ Clean Architecture - Data layer with safeApiCall  
✅ Multiplatform - Android (OkHttp), iOS (Darwin), JVM (OkHttp)  
✅ Ktor Plugins - ContentNegotiation (JSON), Logging (INFO), Timeout  
✅ Error Handling - Automatic exception to NetworkResult.Error conversion  
✅ Koin DI - Single HttpClient instance with platform engine  

## Extension

### Custom Interceptor

```kotlin
fun createHttpClient(engine, baseUrl, isDebug) = HttpClient(engine) {
    // ... existing config
    
    install(HttpRequestInterceptor) {
        intercept { request, handler ->
            request.headers.append("Authorization", "Bearer $token")
            handler(request)
        }
    }
}
```