package com.dqc.test

import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Flow 测试扩展示例 (使用 Turbine)
 */
class FlowTestExample {

    @Test
    fun testSimpleFlow() = runTest {
        val flow = flowOf(1, 2, 3)

        flow.testValues {
            assertEquals(1, awaitItem())
            assertEquals(2, awaitItem())
            assertEquals(3, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun testStateFlow() = runTest {
        val stateFlow = MutableStateFlow("initial")

        stateFlow.testValues {
            assertEquals("initial", awaitItem())

            stateFlow.value = "updated"
            assertEquals("updated", awaitItem())

            cancel()
        }
    }

    @Test
    fun testFlowWithItems() = runTest {
        val flow = flow {
            emit(1)
            emit(2)
            emit(3)
        }

        val items = flow.collectItems(3)

        assertEquals(listOf(1, 2, 3), items)
    }

    @Test
    fun testFirstItem() = runTest {
        val flow = flowOf("first", "second", "third")

        val first = flow.firstItem()

        assertEquals("first", first)
    }

    @Test
    fun testDomainResultFlow() = runTest {
        val flow = flow {
            emit(DomainResult.Loading)
            emit(DomainResult.Success(createUser { name = "张三" }))
        }

        flow.testValues {
            val loading = awaitItem()
            assertTrue(loading.isLoading)

            val success = awaitItem()
            val user = (success as DomainResult.Success).data
            assertEquals("张三", user.name)

            awaitComplete()
        }
    }
}
