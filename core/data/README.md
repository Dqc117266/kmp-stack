# core:data 模块使用指南

## 概述

`core:data` 是整个架构的数据基础设施层，负责：
- 网络数据获取（通过 Ktor）
- 本地数据缓存（通过 SQLDelight）
- DTO 到领域实体的转换
- 统一错误处理

## 架构原则

### 1. 单向数据流
```
[Remote API] → [DTO] → [Mapper] → [Entity] → [Repository] → [Domain Layer]
                    ↓
[Local DB] ←─────[Cache] ←─────────────┘
```

### 2. 严格的分层边界
- **DTO (Data Transfer Object)**: 仅模块内部可见 (`internal`)
- **Entity**: 来自 `core:domain`，纯净的业务模型
- **Mapper**: 负责 DTO → Entity 的转换

## 依赖注入配置

### Android
```kotlin
// Application.kt
startKoin {
    androidContext(this@MyApplication)
    modules(
        // 平台特定模块
        androidDataStoreModule(),  // 来自 core:datastore
        androidDataModule(),       // 来自 core:data

        // 核心模块
        coreNetworkModule(
            baseUrl = "https://api.example.com",
            isDebug = BuildConfig.DEBUG
        ),
        coreDataModule(basePath = "/api/v1"),

        // 应用模块
        appModule
    )
}
```

### iOS
```kotlin
// 在 iOS 主入口初始化
fun initKoin() = startKoin {
    modules(
        iosDataStoreModule(),
        iosDataModule(),
        coreNetworkModule(
            baseUrl = "https://api.example.com",
            isDebug = false
        ),
        coreDataModule(basePath = "/api/v1")
    )
}
```

### JVM (Desktop)
```kotlin
// Desktop App
fun main() = application {
    startKoin {
        modules(
            jvmDataStoreModule(),
            jvmDataModule(),
            coreNetworkModule(
                baseUrl = "https://api.example.com",
                isDebug = true
            ),
            coreDataModule(basePath = "/api/v1")
        )
    }
    // ...
}
```

## 使用 Repository

```kotlin
class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun login(username: String, password: String) {
        viewModelScope.launch {
            when (val result = userRepository.login(username, password)) {
                is DomainResult.Success -> {
                    // 登录成功
                    val session = result.data
                    println("Welcome ${session.user.name}!")
                }
                is DomainResult.Error -> {
                    // 处理错误
                    when (result.error) {
                        is DomainError.Unauthorized -> showError("Invalid credentials")
                        is DomainError.NetworkError -> showError("No internet connection")
                        is DomainError.ValidationError -> showError(result.error.message)
                        else -> showError("Unknown error")
                    }
                }
            }
        }
    }
}
```

## Repository 接口

### UserRepository

```kotlin
interface UserRepository {
    // 登录/注册
    suspend fun login(username: String, password: String): DomainResult<UserSessionEntity>
    suspend fun register(username: String, email: String, password: String): DomainResult<UserEntity>

    // 用户信息
    suspend fun getCurrentUser(): DomainResult<UserEntity>
    fun observeCurrentUser(): Flow<UserEntity>
    suspend fun getUserById(userId: String): DomainResult<UserEntity>
    suspend fun updateUser(user: UserEntity): DomainResult<UserEntity>

    // 认证管理
    suspend fun refreshToken(): DomainResult<String>
    suspend fun logout(): DomainResult<Unit>
    suspend fun isLoggedIn(): Boolean
}
```

## 错误处理

```kotlin
// DomainResult 提供丰富的处理函数
userRepository.getCurrentUser()
    .onSuccess { user ->
        updateUI(user)
    }
    .onError { error ->
        when (error) {
            is DomainError.Unauthorized -> navigateToLogin()
            is DomainError.NetworkError -> showRetryButton()
            else -> showError(error.message)
        }
    }
    .recover { error ->
        // 返回默认值
        UserEntity.empty()
    }
```

## 扩展新的数据源

### 1. 创建 DTO
```kotlin
// network/dto/ProductDto.kt
@Serializable
internal data class ProductResponse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("price") val price: Double
)
```

### 2. 创建 Mapper
```kotlin
// network/mapper/ProductMapper.kt
internal fun ProductResponse.toDomain(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    price = price
)
```

### 3. 创建 Remote DataSource
```kotlin
// network/datasource/ProductRemoteDataSource.kt
internal class ProductRemoteDataSource(
    private val httpClient: HttpClient
) {
    suspend fun getProducts(): DomainResult<List<ProductResponse>> {
        return httpClient.executeAndMap {
            get("/api/v1/products")
        }
    }
}
```

### 4. 创建 Local DataSource (可选)
```kotlin
// local/datasource/ProductLocalDataSource.kt
internal class ProductLocalDataSource(database: AppDatabase) {
    // 实现缓存逻辑
}
```

### 5. 创建 Repository 实现
```kotlin
// repository/ProductRepositoryImpl.kt
internal class ProductRepositoryImpl(
    private val remote: ProductRemoteDataSource,
    private val local: ProductLocalDataSource
) : ProductRepository {
    override suspend fun getProducts(): DomainResult<List<ProductEntity>> {
        return remote.getProducts().map { list ->
            list.map { it.toDomain() }
        }.onSuccess { list ->
            local.saveProducts(list) // 缓存
        }
    }
}
```

### 6. 注册到 DI
```kotlin
// di/DataModule.kt
single { ProductRemoteDataSource(get()) }
single { ProductLocalDataSource(get()) }
single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
```

## 数据库 Schema

数据库 Schema 位于 `src/commonMain/sqldelight/com/dqc/kit/data/local/database/` 目录。

### 添加新表
创建 `.sq` 文件，例如 `product.sq`:
```sql
CREATE TABLE product (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    price REAL NOT NULL,
    created_at INTEGER NOT NULL
);

selectAll:
SELECT * FROM product;

insertOrReplace:
INSERT OR REPLACE INTO product (id, name, price, created_at)
VALUES (?, ?, ?, ?);
```

## 注意事项

1. **DTO 可见性**: 所有 DTO 必须使用 `internal` 修饰符，避免泄露到模块外部
2. **错误映射**: 使用 `ErrorMapper` 将底层异常转换为 `DomainError`
3. **线程安全**: 所有数据库操作使用 `Dispatchers.Default` 调度器
4. **缓存策略**: Repository 优先返回本地缓存，同时异步更新
