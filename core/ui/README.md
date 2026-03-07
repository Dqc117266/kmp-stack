# core:ui 模块

基于 Compose Multiplatform 的工业级设计系统，遵循 Atomic Design 原则。

## 特性

- **跨平台一致**：Android、iOS、Desktop 统一视觉体验
- **Stateless 组件**：所有组件无状态，通过参数控制
- **响应式布局**：自动适配手机、平板、桌面
- **完整的设计令牌**：Color、Typography、Spacing、Shape
- **无障碍支持**：语义化标签、焦点管理

## 架构原则

### 1. 单向数据流
```
[父组件] → 状态参数 → [UI 组件]
               ↑
[父组件] ← 事件回调 ← [UI 组件]
```

### 2. CompositionLocal 注入
```kotlin
// 通过 CompositionLocalProvider 注入主题
AppTheme(darkTheme = isSystemInDarkTheme()) {
    // 子组件通过 AppTheme.colors/typography 访问
    Text(
        text = "Hello",
        color = AppTheme.colors.primary,
        style = AppTheme.typography.bodyLarge
    )
}
```

## 使用方式

### 基础用法

```kotlin
// 应用主题包装
AppTheme(darkTheme = isSystemInDarkTheme()) {
    Scaffold(
        topBar = { AppTopAppBar(title = "首页") },
        bottomBar = {
            AppNavigationBar(
                items = navigationItems,
                selectedRoute = selectedRoute,
                onItemSelected = { route -> /* 切换导航 */ }
            )
        }
    ) { padding ->
        // 页面内容
    }
}
```

### 按钮组件

```kotlin
// 主要按钮
AppButton(
    text = "确认",
    onClick = { /* 点击处理 */ },
    variant = AppButtonVariant.PRIMARY
)

// 加载状态
AppButton(
    text = "保存",
    onClick = { /* 点击处理 */ },
    loading = isLoading,
    enabled = !isLoading
)

// 带图标
AppButton(
    text = "添加",
    onClick = { /* 点击处理 */ },
    leadingIcon = Icons.Default.Add,
    variant = AppButtonVariant.SECONDARY
)

// 图标按钮
AppIconButton(
    icon = Icons.Default.Delete,
    contentDescription = "删除",
    onClick = { /* 删除操作 */ },
    variant = AppButtonVariant.DANGER
)
```

### 输入框组件

```kotlin
// 基础输入框
AppTextField(
    value = text,
    onValueChange = { text = it },
    label = "用户名",
    placeholder = "请输入用户名"
)

// 带错误提示
AppTextField(
    value = email,
    onValueChange = { email = it },
    label = "邮箱",
    errorMessage = if (isValid) null else "邮箱格式不正确"
)

// 密码输入框
AppTextField(
    value = password,
    onValueChange = { password = it },
    label = "密码",
    isPassword = true
)

// 便捷组件
AppEmailField(
    value = email,
    onValueChange = { email = it },
    errorMessage = errorMessage
)

AppPasswordField(
    value = password,
    onValueChange = { password = it },
    errorMessage = errorMessage
)

AppSearchField(
    value = searchQuery,
    onValueChange = { searchQuery = it },
    onSearch = { /* 执行搜索 */ }
)
```

### 响应式布局

```kotlin
@Composable
fun MyScreen() {
    val windowSizeClass = rememberWindowSizeClass()

    when (windowSizeClass) {
        WindowSizeClass.COMPACT -> {
            // 手机布局
            PhoneLayout()
        }
        WindowSizeClass.MEDIUM -> {
            // 平板布局
            TabletLayout()
        }
        WindowSizeClass.EXPANDED -> {
            // 桌面布局
            DesktopLayout()
        }
    }
}

// 或使用响应式值
val padding = responsiveSpacing(
    compact = 16.dp,
    medium = 24.dp,
    expanded = 32.dp
)
```

### UiText 资源处理

```kotlin
// 在 ViewModel 中
class MyViewModel {
    fun getErrorMessage(error: Throwable): UiText {
        return when (error) {
            is NetworkError -> UiText.StringResource(Res.string.error_network)
            is ValidationError -> UiText.DynamicString(error.message)
            else -> UiText.StringResource(Res.string.error_unknown)
        }
    }
}

// 在 Composable 中
val errorMessage: UiText = viewModel.errorMessage
Text(text = errorMessage.asString())
```

## 设计令牌 (Design Tokens)

### 颜色系统
```kotlin
// 通过 AppTheme.colors 访问
val colors = AppTheme.colors

// 主要颜色
colors.primary              // 主色
colors.onPrimary            // 主色上的文字
colors.primaryContainer     // 主色容器背景

// 背景与表面
colors.background           // 主背景色
colors.surface              // 卡片背景
colors.surfaceVariant       // 表面变体

// 功能色
colors.error                // 错误色
colors.outline              // 边框色
colors.scrim                // 遮罩色
```

### 字体系统
```kotlin
// 通过 AppTheme.typography 访问
val typography = AppTheme.typography

// 展示文本
typography.displayLarge     // 57sp
typography.displayMedium    // 45sp
typography.displaySmall     // 36sp

// 标题文本
typography.headlineLarge    // 32sp
typography.headlineMedium   // 28sp
typography.headlineSmall    // 24sp

// 标题
typography.titleLarge       // 22sp
typography.titleMedium      // 16sp
typography.titleSmall       // 14sp

// 正文
typography.bodyLarge        // 16sp（默认）
typography.bodyMedium       // 14sp
typography.bodySmall        // 12sp

// 标签
typography.labelLarge       // 14sp
typography.labelMedium      // 12sp
typography.labelSmall       // 11sp
```

### 间距系统
```kotlin
// 通过 AppTheme.spacing 访问
val spacing = AppTheme.spacing

spacing.xxxs    // 2dp
spacing.xxs     // 4dp
spacing.xs      // 8dp
spacing.s       // 12dp
spacing.m       // 16dp
spacing.l       // 24dp
spacing.xl      // 32dp
spacing.xxl     // 48dp
spacing.xxxl    // 64dp
spacing.xxxxl   // 96dp
```

### 形状系统
```kotlin
// 通过 AppTheme.shapes 访问
val shapes = AppTheme.shapes

shapes.none         // 0dp
shapes.extraSmall   // 2dp
shapes.small        // 4dp
shapes.medium       // 8dp
shapes.large        // 12dp
shapes.extraLarge   // 16dp
shapes.full         // 50%（圆形）
```

## 自定义主题

```kotlin
// 自定义颜色
val customColors = lightColors(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6)
)

// 自定义字体
val customTypography = defaultTypography(
    fontFamily = myCustomFont
)

// 应用自定义主题
AppTheme(
    darkTheme = isSystemInDarkTheme(),
    colors = customColors,
    typography = customTypography,
    spacing = spaciousSpacing(),  // 宽松间距
    shapes = AppShapes.Soft       // 柔和圆角
) {
    // 应用内容
}
```

## 组件列表

### 基础组件 (Atoms)
- `AppButton` - 按钮（支持多种变体、加载状态）
- `AppIconButton` - 图标按钮
- `AppTextField` - 文本输入框
- `AppSearchField` - 搜索框（便捷组件）
- `AppEmailField` - 邮箱输入框（便捷组件）
- `AppPasswordField` - 密码输入框（便捷组件）

### 导航组件 (Molecules)
- `AppNavigationBar` - 自适应导航栏（自动切换底部/侧边）
- `AppBottomNavigationBar` - 底部导航栏
- `AppNavigationRail` - 侧边导航栏
- `AppTopAppBar` - 顶部应用栏
- `AppTopNavigationBar` - 顶部选项卡导航

### 工具类
- `UiText` - 字符串资源包装
- `WindowSizeClass` - 窗口尺寸分类
- `responsiveSpacing` - 响应式间距
- `rememberWindowSizeClass` - 记住窗口尺寸

## 目录结构

```
core/ui/
├── commonMain/
│   ├── foundation/
│   │   ├── Color.kt          # 颜色系统
│   │   ├── Typography.kt     # 字体系统
│   │   ├── Spacing.kt        # 间距系统
│   │   └── Shape.kt          # 形状系统
│   ├── components/
│   │   ├── buttons/
│   │   │   └── AppButton.kt  # 按钮组件
│   │   ├── inputs/
│   │   │   └── AppTextField.kt # 输入框组件
│   │   └── navigation/
│   │       └── AppNavigationBar.kt # 导航组件
│   ├── theme/
│   │   └── AppTheme.kt       # 主题入口
│   ├── res/
│   │   └── UiText.kt         # 资源处理
│   └── util/
│       └── WindowSizeClass.kt # 响应式工具
├── androidMain/               # Android 平台实现
├── iosMain/                   # iOS 平台实现
└── jvmMain/                   # Desktop 平台实现
```

## 注意事项

1. **无状态原则**：组件不持有任何状态，所有状态通过参数传入
2. **事件上提**：用户交互通过 Lambda 回调通知父组件
3. **主题访问**：始终通过 `AppTheme.xxx` 访问主题属性，不要硬编码
4. **平台差异**：字体加载等平台特定逻辑已封装，无需关心
5. **资源访问**：使用 `UiText` 处理字符串资源，支持 ViewModel 中使用
