package com.dqc.test

import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * DomainResult 扩展使用示例
 */
class DomainResultExample {

    private fun fetchUserSuccess(): DomainResult<TestUser> {
        return DomainResult.Success(createUser { name = "张三" })
    }

    private fun fetchUserError(): DomainResult<TestUser> {
        return DomainResult.Error(DomainResult.DomainError.BusinessError("USER_NOT_FOUND", "User not found"))
    }

    @Test
    fun testAssertSuccess() = runTest {
        val result = fetchUserSuccess()
        val user = (result as DomainResult.Success).data

        assertEquals("张三", user.name)
    }

    @Test
    fun testAssertError() = runTest {
        val result = fetchUserError()
        val error = (result as DomainResult.Error).error

        assertEquals("User not found", error.message)
    }

    @Test
    fun testMapSuccess() = runTest {
        val result = fetchUserSuccess()
        val nameResult = if (result is DomainResult.Success) {
            DomainResult.Success(result.data.name)
        } else {
            result as DomainResult.Error
        }

        assertEquals("张三", (nameResult as DomainResult.Success).data)
    }

    @Test
    fun testGetOrDefault() = runTest {
        val successResult = fetchUserSuccess()
        val errorResult = fetchUserError()

        val user1 = successResult.getOrDefault(createUser { name = "Default" })
        val user2 = errorResult.getOrDefault(createUser { name = "Default" })

        assertEquals("张三", user1.name)
        assertEquals("Default", user2.name)
    }

    @Test
    fun testResultTypeChecks() = runTest {
        val success = fetchUserSuccess()
        val error = fetchUserError()

        assertTrue(success.isSuccess)
        assertTrue(error.isError)
        assertTrue(!success.isLoading)
    }
}
