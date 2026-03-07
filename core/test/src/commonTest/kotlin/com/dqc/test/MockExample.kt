package com.dqc.test

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Mock 和 Stub 使用示例
 */
class MockExample {

    @Test
    fun testSimpleStub() {
        val stub = stub<String, Int> {
            whenEquals("one", 1)
            whenEquals("two", 2)
            default { -1 }
        }

        assertEquals(1, stub("one"))
        assertEquals(2, stub("two"))
        assertEquals(-1, stub("unknown"))
    }

    @Test
    fun testStubWithPredicate() {
        val stub = stub<Int, String> {
            whenMatches({ it > 0 }, "positive")
            whenMatches({ it < 0 }, "negative")
            default { "zero" }
        }

        assertEquals("positive", stub(5))
        assertEquals("negative", stub(-3))
        assertEquals("zero", stub(0))
    }

    @Test
    fun testCallbackCollector() {
        val (collector, callback) = createCallbackCollector<String>()

        callback("first")
        callback("second")
        callback("third")

        assertEquals(3, collector.count)
        assertEquals("first", collector.first)
        assertEquals("third", collector.last)
        collector.assertCallbackCount(3)
    }

    @Test
    fun testSequentialAnswer() {
        val answer = SequentialAnswer("first", "second", "third")

        assertEquals("first", answer.next())
        assertEquals("second", answer.next())
        assertEquals("third", answer.next())
        assertEquals(0, answer.remainingCount())
    }

    @Test
    fun testSequentialAnswerWithDefault() {
        val answer = SequentialAnswer(1, 2)
        answer.setDefault { -1 }

        assertEquals(1, answer.next())
        assertEquals(2, answer.next())
        assertEquals(-1, answer.next())  // 使用默认值
        assertEquals(-1, answer.next())  // 继续使用默认值
    }

    @Test
    fun testFakeRepository() = runTest {
        val fakeRepo = object : FakeRepository<TestUser>() {
            suspend fun getUser(id: String): TestUser {
                return get(id) ?: throw RuntimeException("User not found")
            }
        }

        fakeRepo.add("1", createUser { name = "张三" })
        fakeRepo.add("2", createUser { name = "李四" })

        val user = fakeRepo.getUser("1")
        assertEquals("张三", user.name)

        assertFailsWith<RuntimeException> {
            fakeRepo.getUser("999")
        }
    }

    @Test
    fun testConfigurableTestDouble() {
        val testDouble = ConfigurableTestDouble<String>()

        testDouble.setResponse("fetchUser", "张三")
        testDouble.recordCall("fetchUser", "user-123")
        testDouble.recordCall("fetchUser", "user-456")

        assertEquals("张三", testDouble.getResponse<String>("fetchUser"))
        testDouble.verifyCalled("fetchUser", 2)

        val lastArgs = testDouble.getLastCallArgs("fetchUser")
        assertEquals(listOf("user-456"), lastArgs)
    }

    @Test
    fun testSuspendFunctionTester() = runTest {
        val tester = SuspendFunctionTester()

        tester.track("fetchData") {
            delay(100)
        }

        tester.track("processData") {
            delay(50)
        }

        assertTrue(tester.wasExecuted("fetchData"))
        assertTrue(tester.wasExecuted("processData"))
        assertEquals(listOf("fetchData", "processData"), tester.getExecutionOrder())
    }
}
