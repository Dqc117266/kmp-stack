package com.dqc.test

import com.dqc.kit.domain.result.DomainError
import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * DomainResult 测试扩展
 *
 * 提供快捷方法用于在测试中快速解包和处理结果
 */

/**
 * 断言结果是 Success 并返回数据
 *
 * @throws AssertionError 如果结果不是 Success
 *
 * 使用示例：
 * ```kotlin
 * @Test
 * fun testFetchUser() = runTest {
 *     val result = repository.fetchUser("123")
 *     val user = result.assertSuccess()
 *     assertEquals("张三", user.name)
 * }
 * ```
 */
@OptIn(ExperimentalContracts::class)
fun <T> DomainResult<T>.assertSuccess(): T {
    contract {
        returns() implies (this@assertSuccess is DomainResult.Success<T>)
    }
    return when (this) {
        is DomainResult.Success -> data
        is DomainResult.Error -> throw AssertionError(
            "Expected Success but got Error: ${error.message}"
        )
//        is DomainResult.Loading -> throw AssertionError(
//            "Expected Success but got Loading"
//        )
    }
}

/**
 * 断言结果是 Error 并返回异常
 *
 * @throws AssertionError 如果结果不是 Error
 */
@OptIn(ExperimentalContracts::class)
fun <T> DomainResult<T>.assertError(): DomainError {
    contract {
        returns() implies (this@assertError is DomainResult.Error)
    }
    return when (this) {
        is DomainResult.Error -> error
        is DomainResult.Success -> throw AssertionError(
            "Expected Error but got Success: $data"
        )
    }
}

///**
// * 断言结果是 Loading
// *
// * @throws AssertionError 如果结果不是 Loading
// */
//@OptIn(ExperimentalContracts::class)
//fun <T> DomainResult<T>.assertLoading(): Unit {
//    contract {
//        returns() implies (this@assertLoading is DomainResult.Loading)
//    }
//    return when (this) {
//        is DomainResult.Success -> throw AssertionError(
//            "Expected Loading but got Success: $data"
//        )
//        is DomainResult.Error -> throw AssertionError(
//            "Expected Loading but got Error: ${error.message}"
//        )
//    }
//}

/**
 * 安全地获取成功数据，失败时返回 null
 */
fun <T> DomainResult<T>.getOrNull(): T? =
    (this as? DomainResult.Success)?.data

/**
 * 获取成功数据，失败时返回默认值
 */
fun <T> DomainResult<T>.getOrDefault(defaultValue: @UnsafeVariance T): T =
    (this as? DomainResult.Success)?.data ?: defaultValue

/**
 * 获取成功数据，失败时抛出异常
 */
//fun <T> DomainResult<T>.getOrThrow(): T = when (this) {
//    is DomainResult.Success -> data
//    is DomainResult.Error -> throw throw
//}

/**
 * 对 Flow<DomainResult<T>> 的扩展：收集第一个成功结果
 */
suspend fun <T> Flow<DomainResult<T>>.awaitFirstSuccess(): T {
    return first { it is DomainResult.Success }
        .let { (it as DomainResult.Success).data }
}

/**
 * 对 Flow<DomainResult<T>> 的扩展：收集所有结果并返回数据列表
 */
suspend fun <T> Flow<DomainResult<T>>.awaitAllSuccess(): List<T> {
    val results = mutableListOf<T>()
    collect { result ->
        if (result is DomainResult.Success) {
            results.add(result.data)
        }
    }
    return results
}

/**
 * TestScope 扩展：快速测试 DomainResult
 *
 * 使用示例：
 * ```kotlin
 * @Test
 * fun testWithResult() = runTest {
 *     testResult {
 *         val result = useCase.execute()
 *         result.assertSuccess()
 *     }
 * }
 * ```
 */
//inline fun TestScope.testResult(
//    crossinline block: suspend TestScope.() -> DomainResult<*>
//): DomainResult<*> = runTest {
//    block()
//}

/**
 * 辅助函数：创建成功的 DomainResult
 */
fun <T> successResult(data: T): DomainResult<T> = DomainResult.Success(data)

/**
 * 辅助函数：创建失败的 DomainResult
 */
//fun errorResult(
//    message: String,
//    cause: Throwable? = null
//): DomainResult<Nothing> = DomainResult.Error(
//    error = com.dqc.kit.domain.result.DomainResult.DomainError.Unknown(cause, message)
//)
//
///**
// * 辅助函数：创建 Loading 状态的 DomainResult
// */
//fun loadingResult(): DomainResult<Nothing> =
//    DomainResult.Loading

/**
 * 检查是否为成功结果
 */
val <T> DomainResult<T>.isSuccess: Boolean
    get() = this is DomainResult.Success

/**
 * 检查是否为错误结果
 */
//val <T> DomainResult<T>.isError: Boolean
//    get = this is DomainResult.Error

/**
 * 检查是否为加载中状态
 */
//val <T> DomainResult<T>.isLoading: Boolean
//    get() = this is DomainResult.Loading