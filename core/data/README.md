# Core Data 模块

**数据层基础设施模块**，提供基础的数据访问能力，**不包含任何具体业务逻辑**。

## 模块职责

| 职责 | 说明 |
|------|------|
| ✅ 网络请求工具 | `NetworkResultExt` 提供通用的 HTTP 请求封装 |
| ✅ 错误处理 | `ErrorMapper` 统一映射异常到 DomainError |
| ✅ 数据源基类 | `BaseRemoteDataSource`, `BaseLocalDataSource` |
| ✅ 基础 DTO | `BaseResponse`, `PaginatedResponse` 通用响应结构 |
| ❌ **业务 Repository** | 由 Feature 模块自行实现 |
| ❌ **数据库表定义** | 由 Feature 模块自行配置 SQLDelight |
| ❌ **业务 DTO/Mapper** | 由 Feature 模块自行定义 |

## 架构定位

```
┌─────────────────────────────────────────────────────────────┐
│                    Feature 模块                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ MyRepository │  │  MyDataSource │  │  MyDatabase  │      │
│  │ (业务实现)    │  │  (业务实现)    │  │  (SQLDelight)│      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
└─────────┼─────────────────┼─────────────────┼──────────────┘
          │                 │                 │
          │ extends         │ extends         │ uses
          ▼                 ▼                 ▼
┌─────────────────────────────────────────────────────────────┐
│                    Core Data 模块                           │
│  ┌────────────────────┐  ┌────────────────────┐            │
│  │ BaseRemoteDataSource│  │ BaseLocalDataSource │            │
│  │ ErrorMapper         │  │ NetworkResultExt    │            │
│  │ BaseResponse        │  │                     │            │
│  └────────────────────┘  └────────────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

## 提供给 Feature 模块的工具

### 1. BaseRemoteDataSource

```kotlin
// Feature 模块创建自己的 RemoteDataSource
internal class UserRemoteDataSource(
    httpClient: HttpClient
) : BaseRemoteDataSource(httpClient, "/api/v1") {
    
    suspend fun getUser(id: String): DomainResult<UserDto> = 
        httpClient.executeAndMap {
            get(buildUrl("/users/$id"))
        }
}
```

### 2. BaseLocalDataSource

```kotlin
// Feature 模块创建自己的 LocalDataSource
internal class UserLocalDataSource(
    database: UserDatabase  // Feature 自己生成的 SQLDelight 数据库
) : BaseLocalDataSource<UserDatabase>(database) {
    
    suspend fun saveUser(user: UserEntity) {
        // 使用 database 执行 SQLDelight 查询
    }
}
```

### 3. 网络请求扩展

```kotlin
// executeAndMap 自动处理 BaseResponse 包装和错误映射
val result: DomainResult<UserDto> = httpClient.executeAndMap {
    get("/api/users/123")
}
```

### 4. 错误映射

```kotlin
// ErrorMapper 自动处理 Ktor 异常、网络超时等
try {
    val response = httpClient.get("/api/data")
} catch (e: Throwable) {
    val domainError = ErrorMapper.map(e)
    // DomainError.NetworkError / Unauthorized / ValidationError 等
}
```

## 在 Feature 模块中完整实现数据层

```kotlin
// ===== feature/user/build.gradle.kts =====
plugins {
    id("com.dqc.kit.convention.kmp.library")
    alias(libs.plugins.sqldelight)  // Feature 模块自己配置 SQLDelight
}

sqldelight {
    databases {
        create("UserDatabase") {
            packageName.set("com.dqc.kit.feature.user.data")
        }
    }
}

// ===== feature/user/src/commonMain/sqldelight/com/dqc/kit/feature/user/data/user.sq =====
CREATE TABLE user (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL
);

selectById:
SELECT * FROM user WHERE id = ?;

insertOrReplace:
INSERT OR REPLACE INTO user (id, name, email)
VALUES (?, ?, ?);

// ===== feature/user/src/commonMain/kotlin/data/dto/UserDto.kt =====
@Serializable
internal data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String
)

// ===== feature/user/src/commonMain/kotlin/data/mapper/UserMapper.kt =====
internal fun UserDto.toDomain(): User = User(id, name, email)
internal fun UserEntity.toDomain(): User = User(id, name, email)

// ===== feature/user/src/commonMain/kotlin/data/remote/UserRemoteDataSource.kt =====
internal class UserRemoteDataSource(
    httpClient: HttpClient
) : BaseRemoteDataSource(httpClient, "/api/v1") {
    
    suspend fun getUser(id: String): DomainResult<UserDto> = 
        httpClient.executeAndMap {
            get(buildUrl("/users/$id"))
        }
}

// ===== feature/user/src/commonMain/kotlin/data/local/UserLocalDataSource.kt =====
internal class UserLocalDataSource(
    database: UserDatabase
) : BaseLocalDataSource<UserDatabase>(database) {
    
    private val queries = database.userQueries
    
    suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.Default) {
        queries.insertOrReplace(user.id, user.name, user.email)
    }
    
    suspend fun getUser(id: String): UserEntity? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toEntity()
    }
}

// ===== feature/user/src/commonMain/kotlin/data/repository/UserRepositoryImpl.kt =====
internal class UserRepositoryImpl(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource
) : UserRepository {
    
    override suspend fun getUser(id: String): DomainResult<User> {
        // 1. 尝试本地获取
        local.getUser(id)?.let {
            return DomainResult.Success(it.toDomain())
        }
        
        // 2. 从网络获取
        return when (val result = remote.getUser(id)) {
            is DomainResult.Success -> {
                val user = result.data.toDomain()
                local.saveUser(result.data.toEntity())  // 缓存
                DomainResult.Success(user)
            }
            is DomainResult.Error -> result
        }
    }
}

// ===== feature/user/src/commonMain/kotlin/di/UserModule.kt =====
val userModule = module {
    // 数据库驱动（从 core:data 获取）
    single { DatabaseDriverFactory(get()) }
    
    // SQLDelight 数据库实例
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        UserDatabase(driver)
    }
    
    // DataSource
    single { UserRemoteDataSource(get()) }
    single { UserLocalDataSource(get()) }
    
    // Repository
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}
```

## 依赖关系

```
Feature 模块 (如 :feature:user)
├── implementation(project(":core:data"))      # 基础数据工具
├── implementation(project(":core:domain"))    # 领域实体和接口
├── implementation(project(":core:network"))   # HttpClient
├── implementation(libs.sqldelight.gradle)     # SQLDelight 插件
└── 自己配置 SQLDelight 数据库和 Repository
```

## 更新日志

### 2024-03-08 重构

**重大变更：**

- ❌ 移除了 `UserRepository` 和业务相关代码
- ❌ 移除了 `AppDatabase` 和 `user.sq` 表定义
- ❌ 移除了 `PlatformDataModule`（Android/iOS/JVM）
- ✅ 添加了 `BaseRemoteDataSource` 基类
- ✅ 添加了 `BaseLocalDataSource` 基类
- ✅ `DataModule` 现在为空模块（仅组织依赖）
- ✅ 所有业务数据逻辑迁移到 Feature 模块

**迁移指南：**

如果你之前在 core:data 中有业务代码，请迁移到你的 Feature 模块中：

1. 在 Feature 模块的 `build.gradle.kts` 中添加 SQLDelight 插件配置
2. 创建 `.sq` 文件定义数据库表
3. 将 Repository、DataSource、DTO、Mapper 移动到 Feature 模块
4. 在 Feature 模块的 Koin Module 中注册 Repository
5. 从 Application 的 Koin 初始化中移除 `coreDataModule()` 调用
