# Core:Domain 模块

纯净的 Kotlin Multiplatform 领域层模块，作为业务逻辑的核心。

## 📋 特性

- **纯净依赖**：仅依赖 Kotlin 标准库、kotlinx-coroutines 和 kotlinx-datetime
- **MVI 架构**：完整的 MVI 基础封装（State、Intent、Effect）
- **UseCase 模式**：支持同步、异步、Flow 等多种 UseCase 类型
- **领域层结果**：独立的 DomainResult 类型，与网络错误解耦
- **KMP 支持**：可在 Android、iOS、JVM（Desktop）平台使用

## 📁 目录结构

```
core/domain/src/commonMain/kotlin/com/dqc/kit/domain/
├── base/                    # MVI 基础组件
│   ├── UiState.kt          # 状态接口
│   ├── UiIntent.kt         # 意图接口
│   ├── UiEffect.kt         # 副作用接口
│   └── BaseMviViewModel.kt # 基础 ViewModel
├── usecase/                 # UseCase 基础类
│   ├── UseCase.kt          # 同步 UseCase
│   ├── SuspendUseCase.kt   # 异步 UseCase
│   └── FlowUseCase.kt      # Flow UseCase
├── entity/                  # 业务实体
│   └── UserEntity.kt       # 用户实体示例
├── repository/              # Repository 接口
│   └── UserRepository.kt   # 用户仓库接口
├── result/                  # 领域层结果
│   └── DomainResult.kt     # 结果封装
└── example/                 # 完整示例
    ├── LoginContract.kt    # 登录 MVI 契约
    ├── LoginUseCase.kt     # 登录 UseCase
    └── LoginViewModel.kt   # 登录 ViewModel
```

## 🚀 使用方法

### 1. 定义业务实体

```kotlin
// entity/ProductEntity.kt
data class ProductEntity(
    val id: String,
    val name: String,
    val price: Double,
    val createdAt: Instant
)
```

### 2. 定义 Repository 接口

```kotlin
// repository/ProductRepository.kt
interface ProductRepository {
    suspend fun getProducts(): DomainResult<List<ProductEntity>>
    suspend fun getProductById(id: String): DomainResult<ProductEntity>
    fun observeProducts(): Flow<List<ProductEntity>>
}
```

### 3. 创建 UseCase

```kotlin
// usecase/GetProductsUseCase.kt
class GetProductsUseCase(
    private val repository: ProductRepository
) : SuspendUseCaseWithParam<String?, DomainResult<List<ProductEntity>>> {
    
    override suspend operator fun invoke(category: String?): DomainResult<List<ProductEntity>> {
        return repository.getProducts()
            .map { products ->
                if (category != null) {
                    products.filter { it.category == category }
                } else {
                    products
                }
            }
    }
}
```

### 4. 实现 ViewModel

```kotlin
// 定义契约
sealed class ProductUiState : UiState {
    object Loading : ProductUiState()
    data class Success(val products: List<ProductEntity>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

sealed class ProductUiIntent : UiIntent {
    object LoadProducts : ProductUiIntent()
    data class SelectCategory(val category: String) : ProductUiIntent()
}

sealed class ProductUiEffect : UiEffect {
    data class ShowToast(val message: String) : ProductUiEffect()
    data class NavigateToDetail(val productId: String) : ProductUiEffect()
}

// 实现 ViewModel
class ProductViewModel(
    private val getProductsUseCase: GetProductsUseCase
) : BaseMviViewModel<ProductUiState, ProductUiIntent, ProductUiEffect>(
    initialState = ProductUiState.Loading
) {
    
    override fun handleIntent(intent: ProductUiIntent) {
        when (intent) {
            is ProductUiIntent.LoadProducts -> loadProducts()
            is ProductUiIntent.SelectCategory -> selectCategory(intent.category)
        }
    }
    
    private fun loadProducts() {
        launch {
            updateState { ProductUiState.Loading }
            
            when (val result = getProductsUseCase(null)) {
                is DomainResult.Success -> {
                    updateState { ProductUiState.Success(result.data) }
                }
                is DomainResult.Error -> {
                    updateState { ProductUiState.Error(result.error.message) }
                    sendEffect(ProductUiEffect.ShowToast(result.error.message))
                }
                is DomainResult.Loading -> {
                    // 已经是 Loading 状态
                }
            }
        }
    }
    
    private fun selectCategory(category: String) {
        // 处理分类选择...
    }
}
```

### 5. 在 UI 层使用

```kotlin
@Composable
fun ProductScreen(
    viewModel: ProductViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // 处理副作用
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ProductUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ProductUiEffect.NavigateToDetail -> {
                    navigator.navigateToDetail(effect.productId)
                }
            }
        }
    }
    
    // 根据状态渲染 UI
    when (val currentState = state) {
        is ProductUiState.Loading -> LoadingView()
        is ProductUiState.Success -> ProductList(
            products = currentState.products,
            onProductClick = { /* 处理点击 */ }
        )
        is ProductUiState.Error -> ErrorView(
            message = currentState.message,
            onRetry = { viewModel.handleIntent(ProductUiIntent.LoadProducts) }
        )
    }
}
```

## 📦 依赖关系

```
core:domain (最底层，纯净)
    ↑
core:network → 实现 domain 的 Repository 接口
core:datastore → 实现 domain 的 Repository 接口
    ↑
feature:xxx → 调用 UseCase 和观察 State
```

### 依赖配置

在 `core:network` 和 `core:datastore` 的 `build.gradle.kts` 中添加：

```kotlin
commonMain.dependencies {
    implementation(project(":core:domain"))
    // ... 其他依赖
}
```

在 `feature:xxx` 的 `build.gradle.kts` 中添加：

```kotlin
commonMain.dependencies {
    implementation(project(":core:domain"))
    // ... 其他依赖
}
```

## ⚠️ 注意事项

1. **BaseViewModel 不是 AndroidX ViewModel**：这是一个纯净的 KMP 实现，不依赖 AndroidX。
   - Android 平台需要手动在 `onCleared()` 时调用 `viewModel.onCleared()`
   - 或使用包装器将其与 AndroidX ViewModel 集成

2. **Repository 实现由 data 层完成**：
   - `core:network` 实现网络相关的 Repository
   - `core:datastore` 实现本地存储相关的 Repository
   - 不要在 domain 层直接依赖 Ktor 或 DataStore

3. **DomainResult 与 NetworkResult**：
   - DomainResult 是领域层的结果类型，与 HTTP 状态码无关
   - 在 Repository 实现层，将 NetworkResult 转换为 DomainResult

## 📝 完整示例

参见 `example/` 目录下的完整登录流程实现：
- `LoginContract.kt` - MVI 契约定义
- `LoginUseCase.kt` - UseCase 实现
- `LoginViewModel.kt` - ViewModel 实现

## 🔧 扩展建议

1. **添加更多 UseCase 类型**：
   - `PagedUseCase` - 分页数据加载
   - `CachedUseCase` - 带缓存的 UseCase

2. **添加 Repository 组合器**：
   - `OfflineFirstRepository` - 离线优先的数据获取策略

3. **增强 BaseViewModel**：
   - 添加错误处理中间件
   - 添加日志记录功能

## 🐛 调试技巧

1. **打印 State 变化**：
```kotlin
init {
    launch {
        uiState.collect { state ->
            println("State changed to: $state")
        }
    }
}
```

2. **使用 Kotlin Flow 调试工具**：
```kotlin
// 在 Flow 链中添加日志
flow
    .onEach { println("Before transform: $it") }
    .map { transform(it) }
    .onEach { println("After transform: $it") }
```
