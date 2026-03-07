# core:di 模块

核心依赖注入模块，作为项目的**依赖注入中心**，负责汇总所有基础层模块并提供统一的启动入口。

## 模块定位

```
┌─────────────────────────────────────────────────────┐
│  壳工程 (androidApp / iOS / Desktop)                 │
│  ┌─────────────────────────────────────────────┐    │
│  │  Feature 模块 (动态注入)                      │    │
│  │  ┌─────┐ ┌─────┐ ┌─────┐                   │    │
│  │  │Home │ │User │ │...  │                   │    │
│  │  └─────┘ └─────┘ └─────┘                   │    │
│  └─────────────────────────────────────────────┘    │
│                     │                               │
│                     ▼                               │
│         ┌─────────────────┐                         │
│         │   core:di       │ ◄── 依赖注入中心         │
│         │  (你正在这里)    │                         │
│         └─────────────────┘                         │
│                     │                               │
│         ┌───────────┼───────────┐                   │
│         ▼           ▼           ▼                   │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐            │
│  │core:data │ │core:nw   │ │core:store│ ...        │
│  └──────────┘ └──────────┘ └──────────┘            │
└─────────────────────────────────────────────────────┘
```

## 架构规则

1. **单向依赖原则**: core:di 只依赖其他 core 模块，严禁依赖任何 feature 模块
2. **平台隔离启动**: 提供 commonMain 的 `initKoin` 函数，支持 Android/iOS 平台特定配置
3. **模块化声明**: 采用 Koin 的 `includes` 模式，汇总各 core 模块的 module 声明

## 文件结构

```
core/di/
├── build.gradle.kts
└── src/
    ├── commonMain/kotlin/com/dqc/kit/di/
    │   ├── CoreModules.kt      # 汇总所有底层核心模块
    │   └── DIHelper.kt         # 跨平台启动逻辑入口
    ├── androidMain/kotlin/com/dqc/kit/di/
    │   └── KoinAndroid.kt      # Android Context 注入
    └── iosMain/kotlin/com/dqc/kit/di/
        └── KoinIOS.kt          # Swift 友好的桥接函数
```

## 使用方法

### Android 应用初始化

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 方式1: 简单初始化
        initKoinAndroid(
            context = this,
            baseUrl = "https://api.example.com",
            isDebug = BuildConfig.DEBUG
        )

        // 方式2: 带 Feature 模块
        initKoinAndroid(
            context = this,
            isDebug = BuildConfig.DEBUG,
            baseUrl = "https://api.example.com",
            enableAuth = true,
            additionalModules = listOf(
                homeFeatureModule,
                profileFeatureModule
            )
        )

        // 方式3: 完整配置
        initKoinAndroid(
            context = this,
            config = CoreModuleConfig(
                baseUrl = "https://api.example.com",
                basePath = "/api/v1",
                isDebug = BuildConfig.DEBUG,
                enableAuth = true
            ),
            additionalModules = listOf(homeFeatureModule)
        )
    }
}
```

### iOS 应用初始化 (Swift)

```swift
import shared

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // 方式1: 简单初始化
        KoinIOSKt.doInitKoinIos(
            baseUrl: "https://api.example.com",
            isDebug: true
        )

        // 方式2: 带 Feature 模块
        KoinIOSKt.doInitKoinIosWithModules(
            baseUrl: "https://api.example.com",
            isDebug: true,
            enableAuth: true,
            additionalModules: [homeModule, profileModule]
        )

        return true
    }
}
```

### Desktop/JVM 应用初始化

```kotlin
fun main() {
    // 通用初始化
    DIHelper.initKoin(
        baseUrl = "https://api.example.com",
        isDebug = true,
        additionalModules = listOf(desktopFeatureModule)
    )

    // 或者使用配置对象
    DIHelper.initKoin(
        config = CoreModuleConfig(
            baseUrl = "https://api.example.com",
            isDebug = true
        )
    )
}
```

### 获取依赖

```kotlin
// 通过注入
class MyViewModel(
    private val userRepository: UserRepository,
    private val httpClient: HttpClient
) : ViewModel()

// 通过 Koin 获取
val userRepository: UserRepository = getKoin().get()
val httpClient: HttpClient = getKoin().get()
```

## 核心模块清单

| 模块 | 说明 | 导入方式 |
|------|------|----------|
| core:data | Repository 实现、数据源 | 自动包含 |
| core:domain | 纯净的实体和接口 | 自动包含 |
| core:network | HTTP 客户端、认证管理 | 自动包含 |
| core:datastore | 键值对存储 | 自动包含 |
| core:database | SQLDelight 数据库 | 自动包含 |
| core:logging | Kermit 日志 | 自动包含 |
| core:common | 通用工具类 | 自动包含 |

## 配置参数

```kotlin
class CoreModuleConfig(
    val basePath: String = "/api/v1",      // API 路径前缀
    val baseUrl: String,                    // 服务器基础 URL
    val isDebug: Boolean = false,           // 调试模式（控制日志）
    val enableAuth: Boolean = true          // 是否启用认证功能
)
```

## 注意事项

1. **初始化时机**: Koin 必须在应用启动时初始化，且只能初始化一次
2. **模块顺序**: 底层模块（DataStore/Database）会先初始化，上层模块（Data）依赖它们
3. **平台特定**: Android 需要传入 Context，iOS 使用桥接函数
4. **线程安全**: Koin 初始化是线程安全的，但建议在主线程调用
