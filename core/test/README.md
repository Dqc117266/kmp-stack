# Core Test Module

KMP 统一测试脚手架模块，为全平台提供一致的测试能力。

## 功能特性

- **BaseTest**: 自动处理协程调度器的设置和重置
- **TestDataFactory**: DSL 风格生成测试数据
- **DomainResultExt**: DomainResult 快捷解包扩展
- **FlowTestExt**: 基于 Turbine 的 Flow 测试扩展
- **MockExt**: Mock 和 Stub 辅助工具
- **TestCoroutineRule**: 协程测试规则

## 快速开始

### 1. 添加依赖

在需要测试的模块的 `build.gradle.kts` 中添加：

```kotlin
kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(projects.core.test)
        }
    }
}
```

### 2. 基础测试类

```kotlin
class MyViewModelTest : BaseTest(dispatcherType = DispatcherType.Unconfined) {
    @Test
    fun testSomething() = runTest {
        // 测试代码自动在主调度器上运行
    }
}
```

### 3. 生成测试数据

```kotlin
val user = createUser(
    id = "user-1",
    name = "张三"
) {
    email = "zhangsan@example.com"
}

val orders = createList(3) { index ->
    createOrder { status = OrderStatus.CONFIRMED }
}
```

### 4. 测试 DomainResult

```kotlin
@Test
fun testFetchUser() = runTest {
    val result = repository.fetchUser("123")
    
    // 解包数据，如果不是 Success 会抛出 AssertionError
    val user = result.assertSuccess()
    assertEquals("张三", user.name)
    
    // 或测试错误情况
    val error = result.assertError()
    assertEquals("User not found", error.message)
}
```

### 5. 测试 Flow

```kotlin
@Test
fun testUserFlow() = runTest {
    viewModel.userFlow.testValues {
        assertEquals(Loading, awaitItem())
        
        val user = awaitItem()
        assertEquals("张三", user.name)
        
        awaitComplete()
    }
}
```

## 最佳实践

### 选择调度器类型

- **Unconfined**: 适合大多数简单测试，协程立即执行
- **Standard**: 需要手动控制时间，适合测试延迟、超时等场景

### 测试 Fake vs Mock

- **Fake**: 适用于所有平台，推荐优先使用
- **Mock**: 仅在 JVM 平台可用，使用 MockK

### 测试命名规范

```kotlin
@Test
fun `should return user when id exists`() = runTest { }

@Test
fun `should throw error when user not found`() = runTest { }
```
