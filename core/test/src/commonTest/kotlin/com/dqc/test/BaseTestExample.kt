package com.dqc.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 协程测试基类使用示例
 */
class BaseTestExample : BaseTest(dispatcherType = DispatcherType.Unconfined) {

    @Test
    fun testWithRunTest() = runTest {
        // 使用基类提供的 runTest，自动使用配置好的调度器
        val result = fetchData()
        assertEquals("data", result)
    }

    @Test
    fun testWithDelay() = runTest {
        var completed = false

        launch {
            delay(1000)
            completed = true
        }

        // 使用 Standard 调度器时可以控制时间
        if (dispatcherType == DispatcherType.Standard) {
            advanceTimeBy(1000)
            runCurrent()
        }

        assertTrue(completed)
    }

    @Test
    fun testMultipleCoroutines() = runTest {
        val results = mutableListOf<String>()

        launch { results.add("A") }
        launch { results.add("B") }
        launch { results.add("C") }

        // Unconfined 调度器会立即执行
        if (dispatcherType == DispatcherType.Standard) {
            runCurrent()
        }

        assertEquals(3, results.size)
    }

    private suspend fun fetchData(): String {
        delay(100)
        return "data"
    }
}

/**
 * 使用 Standard 调度器的示例（需要手动控制时间）
 */
class BaseTestStandardExample : BaseTest(dispatcherType = DispatcherType.Standard) {

    @Test
    fun testWithTimeControl() = runTest {
        val flow = kotlinx.coroutines.flow.flow {
            emit("start")
            delay(5000)
            emit("after 5s")
            delay(5000)
            emit("after 10s")
        }

        flow.testValues {
            assertEquals("start", awaitItem())

            // 快进 5 秒
            advanceTimeBy(5000)
            assertEquals("after 5s", awaitItem())

            // 再快进 5 秒
            advanceTimeBy(5000)
            assertEquals("after 10s", awaitItem())

            awaitComplete()
        }
    }
}
