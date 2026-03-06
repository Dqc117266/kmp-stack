# Network Module

Clean Architecture implementation of network layer using Ktor and Koin.

## Structure

```
core/network/
├── commonMain/kotlin/com/dqc/kit/network/
│   ├── core/              # Infrastructure
│   │   ├── config/        # Network configuration
│   │   ├── exception/     # Network exceptions
│   │   └── HttpClientFactory.kt
│   ├── data/              # Data layer
│   │   ├── remote/
│   │   │   ├── api/       # API services
│   │   │   └── dto/       # DTOs and mappers
│   │   └── repository/    # Repository implementations
│   ├── domain/            # Domain layer
│   │   ├── model/         # Business models
│   │   ├── repository/    # Repository interfaces
│   │   └── usecase/       # Use cases
│   └── di/                # Koin modules
└── platform/              # Platform-specific code
```

## Usage

### 1. Initialize Koin with Network Module

```kotlin
import com.dqc.kit.network.core.config.NetworkConfig
import com.dqc.kit.network.di.networkModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            networkModule(
                NetworkConfig(
                    baseUrl = "https://api.example.com/",
                    isDebug = BuildConfig.DEBUG
                )
            )
        )
    }
}
```

### 2. Use Cases in ViewModel

```kotlin
import com.dqc.kit.network.domain.usecase.GetUserUseCase
import org.koin.compose.koinInject

class UserViewModel(
    private val getUserUseCase: GetUserUseCase = koinInject()
) : ViewModel() {
    
    suspend fun loadUser(userId: String) {
        when (val result = getUserUseCase(userId)) {
            is ApiResult.Success -> {
                // Handle success
                val user = result.data
            }
            is ApiResult.Error -> {
                // Handle error
                val error = result.exception
            }
        }
    }
}
```

### 3. Add New API Endpoint

#### Step 1: Create Domain Model

```kotlin
// domain/model/Post.kt
package com.dqc.kit.network.domain.model

data class Post(
    val id: String,
    val title: String,
    val content: String
)
```

#### Step 2: Create Repository Interface

```kotlin
// domain/repository/PostRepository.kt
package com.dqc.kit.network.domain.repository

interface PostRepository {
    suspend fun getPosts(): ApiResult<List<Post>>
}
```

#### Step 3: Create DTO

```kotlin
// data/remote/dto/PostDto.kt
package com.dqc.kit.network.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String
)

fun PostDto.toDomain(): Post {
    return Post(id, title, content)
}
```

#### Step 4: Create API Service

```kotlin
// data/remote/api/PostApi.kt
package com.dqc.kit.network.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class PostApi(private val client: HttpClient) {
    suspend fun getPosts(): List<PostDto> {
        return client.get {
            url.path("posts")
        }.body()
    }
}
```

#### Step 5: Implement Repository

```kotlin
// data/repository/PostRepositoryImpl.kt
package com.dqc.kit.network.data.repository

class PostRepositoryImpl(
    private val postApi: PostApi
) : PostRepository {
    override suspend fun getPosts(): ApiResult<List<Post>> {
        return try {
            val posts = postApi.getPosts().map { it.toDomain() }
            ApiResult.Success(posts)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}
```

#### Step 6: Create Use Case

```kotlin
// domain/usecase/GetPostsUseCase.kt
package com.dqc.kit.network.domain.usecase

class GetPostsUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(): ApiResult<List<Post>> {
        return postRepository.getPosts()
    }
}
```

#### Step 7: Update Koin Module

```kotlin
// di/NetworkModule.kt
fun networkModule(config: NetworkConfig): Module = module {
    // ... existing code ...
    
    single { PostApi(get()) }
    single<PostRepository> { PostRepositoryImpl(get()) }
    single { GetPostsUseCase(get()) }
}
```

## Architecture Principles

1. **Domain Layer** - Contains business logic, repository interfaces, and domain models
2. **Data Layer** - Implements repositories, handles data sources (remote/local)
3. **Clean Architecture** - Dependencies point inward (Data → Domain)

## Benefits

- ✅ Testable (easily mock repositories and use cases)
- ✅ Scalable (easy to add new features)
- ✅ Maintainable (clear separation of concerns)
- ✅ Multiplatform (works on Android, iOS, Desktop)
- ✅ Type-safe (sealed classes for results)