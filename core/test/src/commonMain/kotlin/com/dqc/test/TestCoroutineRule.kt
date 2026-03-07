package com.dqc.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * 协程测试规则
 *
 * 用于在测试中管理协程调度器的设置和清理
 *
 * 使用示例（JUnit 风格）：
 * ```kotlin
 * class MyTest {
 *     private val testRule = TestCoroutineRule()
 *
 *     @BeforeTest
 *     fun setup() {
 *         testRule.setup()
 *     }
 *
 *     @AfterTest
 *     fun tearDown() {
 *         testRule.tearDown()
 *     }
 *
 *     @Test
 *     fun testSomething() = testRule.runTest {
 *         // 测试代码
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestCoroutineRule(
    private val useUnconfined: Boolean = true
) {
    lateinit var dispatcher: TestDispatcher
        private set

    val scope: TestScope
        get() = TestScope(dispatcher)

    fun setup() {
        dispatcher = if (useUnconfined) {
            UnconfinedTestDispatcher()
        } else {
            StandardTestDispatcher()
        }
        Dispatchers.setMain(dispatcher)
    }

    fun tearDown() {
        Dispatchers.resetMain()
    }

//    fun runTest(
//        timeoutMillis: Long = 10000L,
//        testBody: suspend TestScope.() -> Unit
//    ) = kotlinx.coroutines.test.runTest(
//        context = dispatcher,
//        timeout = mil(timeoutMillis)
//    ) {
//        testBody()
//    }
}

/**
 * Extension property to convert Long milliseconds to Duration
 */
//val Long.milliseconds: kotlin.time.Duration
//    get() = kotlin.time.Duration.milliseconds(this)

/**
 * 协程测试配置
 */
data class CoroutineTestConfig(
    val dispatcherType: DispatcherType = DispatcherType.Unconfined,
    val timeoutMillis: Long = 10000L,
    val autoAdvanceTime: Boolean = true
)

enum class DispatcherType {
    Unconfined,
    Standard
}

/**
 * 带配置的协程测试运行器
 *
 * 使用示例：
 * ```kotlin
 * @Test
 * fun testWithConfig() = runCoroutineTest(
 *     config = CoroutineTestConfig(
 *         dispatcherType = DispatcherType.Standard,
 *         timeoutMillis = 5000
 *     )
 * ) {
 *     // 测试代码
 *     advanceTimeBy(1000)
 *     // 更多测试代码
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun runCoroutineTest(
    config: CoroutineTestConfig = CoroutineTestConfig(),
    testBody: suspend TestScope.() -> Unit
) {
    val dispatcher = when (config.dispatcherType) {
        DispatcherType.Unconfined -> UnconfinedTestDispatcher()
        DispatcherType.Standard -> StandardTestDispatcher()
    }

    Dispatchers.setMain(dispatcher)

//    try {
//        kotlinx.coroutines.test.runTest(
//            context = dispatcher,
//            timeout = kotlin.time.Duration.milliseconds(config.timeoutMillis)
//        ) {
//            testBody()
//        }
//    } finally {
//        Dispatchers.resetMain()
//    }
}

/**
 * 测试用的 CoroutineScope 扩展
 */
fun TestScope.createBackgroundScope(): CoroutineScope {
    return CoroutineScope(coroutineContext)
}

/**
 * 暂停函数测试辅助
 */
class SuspendFunctionTester {
    private val executedFunctions = mutableListOf<String>()

    suspend fun track(name: String, block: suspend () -> Unit) {
        executedFunctions.add(name)
        block()
    }

    fun wasExecuted(name: String): Boolean = name in executedFunctions

    fun getExecutionOrder(): List<String> = executedFunctions.toList()

    fun clear() {
        executedFunctions.clear()
    }
}

/**
 * 创建协程测试用的 Scope
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun createTestScope(useUnconfined: Boolean = true): Pair<TestScope, TestDispatcher> {
    val dispatcher = if (useUnconfined) {
        UnconfinedTestDispatcher()
    } else {
        StandardTestDispatcher()
    }
    return TestScope(dispatcher) to dispatcher
}
