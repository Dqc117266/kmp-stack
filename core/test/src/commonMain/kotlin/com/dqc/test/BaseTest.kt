package com.dqc.test

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * KMP 测试基类
 *
 * 自动处理协程调度器的设置和重置，支持 Unconfined 和 Standard 两种模式
 *
 * 使用示例：
 * ```kotlin
 * class MyViewModelTest : BaseTest(dispatcherType = DispatcherType.Unconfined) {
 *     @Test
 *     fun testSomething() = runTest {
 *         // 你的测试代码
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseTest(
    private val dispatcherType: DispatcherType = DispatcherType.Unconfined
) {
    /**
     * 测试调度器类型
     */
    enum class DispatcherType {
        /** 立即执行，适合简单的同步测试 */
        Unconfined,
        /** 标准调度器，需要手动推进时间，适合复杂的时间相关测试 */
        Standard
    }

    /**
     * 当前测试使用的调度器
     */
    lateinit var testDispatcher: TestDispatcher
        private set

    /**
     * 基于当前调度器的测试 Scope
     */
    val testScope: TestScope
        get() = TestScope(testDispatcher)

    @BeforeTest
    open fun setup() {
        testDispatcher = when (dispatcherType) {
            DispatcherType.Unconfined -> UnconfinedTestDispatcher()
            DispatcherType.Standard -> StandardTestDispatcher()
        }
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    open fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * 在测试 Scope 中运行测试
     *
     * @param timeoutMillis 超时时间（毫秒）
     * @param testBody 测试体
     */
    fun runTest(
        timeoutMillis: Long = 10000L,
        testBody: suspend TestScope.() -> Unit
    ) = kotlinx.coroutines.test.runTest(
        context = testDispatcher,
        timeout = timeoutMillis.toLong().milliseconds
    ) {
        testBody()
    }
}
