# Feature: Recipe 模块

这是一个完整的示例模块，展示了如何使用 Core 模块构建一个遵循 Clean Architecture 和 MVI 架构的功能模块。

## 模块结构

```
feature/recipe/
├── build.gradle.kts              # 模块构建配置（包含 SQLDelight）
├── src/commonMain/
│   ├── kotlin/com/example/recipe/
│   │   ├── data/                 # 数据层
│   │   │   ├── datasource/
│   │   │   │   ├── local/        # 本地数据源（数据库）
│   │   │   │   │   └── RecipeLocalDataSource.kt
│   │   │   │   └── remote/       # 远程数据源（网络）
│   │   │   │       └── RecipeRemoteDataSource.kt
│   │   │   ├── mapper/           # 数据映射器
│   │   │   │   └── RecipeMapper.kt
│   │   │   ├── model/            # 数据模型（DTO）
│   │   │   │   └── RecipeDto.kt
│   │   │   └── repository/       # 仓库实现
│   │   │       └── RecipeRepositoryImpl.kt
│   │   ├── domain/               # 领域层
│   │   │   ├── model/            # 领域模型
│   │   │   │   └── Recipe.kt
│   │   │   ├── repository/       # 仓库接口
│   │   │   │   └── RecipeRepository.kt
│   │   │   └── usecase/          # 用例（业务逻辑）
│   │   │       └── RecipeUseCases.kt
│   │   ├── presentation/         # 表现层
│   │   │   ├── component/        # UI 组件
│   │   │   │   ├── EmptyView.kt
│   │   │   │   ├── ErrorView.kt
│   │   │   │   └── LoadingIndicator.kt
│   │   │   ├── contract/         # MVI 契约（State/Intent/Effect）
│   │   │   │   ├── RecipeListContract.kt
│   │   │   │   └── RecipeDetailContract.kt
│   │   │   ├── screen/           # Compose 屏幕
│   │   │   │   ├── RecipeListScreen.kt
│   │   │   │   └── RecipeDetailScreen.kt
│   │   │   └── viewmodel/        # ViewModel
│   │   │       ├── RecipeListViewModel.kt
│   │   │       └── RecipeDetailViewModel.kt
│   │   └── di/                   # 依赖注入
│   │       └── RecipeModule.kt
│   └── sqldelight/com/example/recipe/data/
│       └── RecipeEntity.sq       # SQLDelight 表定义
```

## 架构说明

### 1. 数据层 (Data Layer)

#### 本地数据源
- 使用 **core:database** 的 `BaseDataSource` 基类
- 使用 SQLDelight 进行数据库操作
- 支持离线缓存、收藏功能

#### 远程数据源
- 使用 **core:network** 提供的 `HttpClient`
- Ktor 客户端进行网络请求
- 自动处理认证和错误

#### 仓库实现
- 协调本地和远程数据源
- 实现领域层定义的仓库接口
- 处理数据转换和缓存策略

### 2. 领域层 (Domain Layer)

#### 领域模型
- 纯净的 Kotlin 数据类
- 不依赖任何框架
- 包含业务逻辑（如 Difficulty 枚举）

#### 用例 (UseCase)
- 使用 **core:domain** 提供的用例基类
- 封装单一业务逻辑
- 支持 Flow 和 Suspend 两种模式

### 3. 表现层 (Presentation Layer)

#### MVI 架构
- **State**: 使用 `UiState` 定义 UI 状态
- **Intent**: 使用 `UiIntent` 定义用户意图
- **Effect**: 使用 `UiEffect` 定义副作用

#### ViewModel
- 继承 **core:presentation** 的 `BaseMviViewModel`
- 通过 `registerIntents()` 注册意图处理器
- 自动管理 Loading 状态和并发请求

#### Compose UI
- 使用 Material3 组件
- 支持响应式布局
- 集成 Coil 图片加载

## 使用方式

### 1. 在 App 模块添加依赖

在 `settings.gradle.kts` 中添加模块：
```kotlin
include(":feature:recipe")
```

### 2. 在 App 模块导入 Koin 模块

```kotlin
// App 的 Koin 配置
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            // Core 模块
            networkModule,
            databaseModule(),
            // ... 其他 core 模块

            // Feature 模块
            recipeModule
        )
    }
}
```

### 3. 在导航中使用

```kotlin
// 使用 Voyager 或其他导航库
class RecipeListScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: RecipeListViewModel = koinViewModel()

        RecipeListScreen(
            viewModel = viewModel,
            onNavigateToDetail = { recipeId ->
                // 导航到详情页
                navigator.push(RecipeDetailScreen(recipeId))
            },
            onShowToast = { message ->
                // 显示 Toast
            }
        )
    }
}
```

## API 设计示例

此模块期望的后端 API 格式：

```kotlin
// 获取食谱列表
GET /api/v1/recipes
Response: {
    "success": true,
    "data": [
        {
            "id": "1",
            "title": "红烧肉",
            "description": "经典家常菜",
            "image_url": "https://example.com/image.jpg",
            "cooking_time": 60,
            "servings": 4,
            "difficulty": "MEDIUM",
            "author": {
                "name": "厨师小王",
                "avatar_url": "..."
            },
            "ingredients": [...],
            "created_at": "2024-01-01T00:00:00Z",
            "updated_at": "2024-01-01T00:00:00Z"
        }
    ]
}

// 搜索食谱
GET /api/v1/recipes/search?q=红烧肉

// 同步数据
POST /api/v1/recipes/sync
Body: [recipe1, recipe2, ...]
```

## 扩展建议

1. **添加更多功能**
   - 食谱创建/编辑
   - 评论功能
   - 评分功能
   - 分享功能

2. **优化性能**
   - 添加分页加载
   - 图片懒加载
   - 数据库索引优化

3. **增强测试**
   - 单元测试
   - 集成测试
   - UI 测试
