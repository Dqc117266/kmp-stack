package com.dqc.test

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Flow 测试扩展
 *
 * 基于 Turbine 提供更便捷的 Flow 测试 API
 */

/**
 * 测试 Flow 并验证所有发射的值
 *
 * 使用示例：
 * ```kotlin
 * @Test
 * fun testUserFlow() = runTest {
 *     viewModel.userFlow.testValues {
 *         // 第一个值
 *         assertEquals(Loading, awaitItem())
 *
 *         // 第二个值
 *         val user = awaitItem()
 *         assertEquals("张三", user.name)
 *
 *         // 完成
 *         awaitComplete()
 *     }
 * }
 * ```
 */
suspend fun <T> Flow<T>.testValues(
    timeout: Duration = 5.seconds,
    validate: suspend ReceiveTurbine<T>.() -> Unit
) {
    test(timeout = timeout) {
        validate()
    }
}

/**
 * 测试 Flow 并收集指定数量的值
 *
 * 使用示例：
 * ```kotlin
 * @Test
 * fun testPagination() = runTest {
 *     val items = repository.itemsFlow.collectItems(3)
 *     assertEquals(3, items.size)
 * }
 * ```
 */
suspend fun <T> Flow<T>.collectItems(
    count: Int,
    timeout: Duration = 5.seconds
): List<T> = test(timeout = timeout) {
    val items = mutableListOf<T>()
    repeat(count) {
        items.add(awaitItem())
    }
    cancel()
    items
}

/**
 * 测试 Flow 并收集第一个值
 */
suspend fun <T> Flow<T>.firstItem(
    timeout: Duration = 5.seconds
): T = test(timeout = timeout) {
    val item = awaitItem()
    cancel()
    item
}

/**
 * 断言下一个值满足条件
 */
suspend fun <T> ReceiveTurbine<T>.assertNext(
    predicate: (T) -> Boolean,
    message: String? = null
): T {
    val item = awaitItem()
    if (!predicate(item)) {
        throw AssertionError(
            message ?: "Expected item to satisfy predicate, but got: $item"
        )
    }
    return item
}

/**
 * 断言下一个值等于预期值
 */
suspend fun <T> ReceiveTurbine<T>.assertNextEquals(
    expected: T,
    message: String? = null
): T {
    val item = awaitItem()
    if (item != expected) {
        throw AssertionError(
            message ?: "Expected $expected but got $item"
        )
    }
    return item
}

/**
 * 断言没有更多值（立即完成或取消）
 */
suspend fun <T> ReceiveTurbine<T>.assertNoMoreItems() {
    when (val event = awaitEvent()) {
        is app.cash.turbine.Event.Complete -> { /* 预期行为 */
        }

        is app.cash.turbine.Event.Error -> throw AssertionError(
            "Flow failed with error",
            event.throwable
        )

        is app.cash.turbine.Event.Item -> throw AssertionError(
            "Expected no more items but got: ${event.value}"
        )
    }
}

/**
 * 断言 Flow 在指定时间内没有发射值
 */
suspend fun <T> ReceiveTurbine<T>.assertNoValue(timeout: Duration = 100.milliseconds) {
    expectNoEvents(timeout)
}

/**
 * Turbine 测试的通用模式
 */
object FlowTestPatterns {

    /**
     * 测试 Loading -> Success 模式
     */
    suspend fun <T> ReceiveTurbine<DomainResult<T>>.awaitLoadingThenSuccess(): T {
        assertNext { it.isSuccess || it.getOrNull() == null } // 可能是 Loading 状态或初始值
        val successItem: DomainResult<T> = awaitItem()
        return (successItem as DomainResult.Success<T>).data
    }

    /**
     * 测试 Loading -> Error 模式
     */
    suspend fun <T> ReceiveTurbine<DomainResult<T>>.awaitLoadingThenError(): com.dqc.kit.domain.result.DomainError {
        assertNext { it.isSuccess || it.getOrNull() == null }
        val errorItem: DomainResult<T> = awaitItem()
        return (errorItem as DomainResult.Error).error
    }

    /**
     * 测试 Success -> Success（数据更新）模式
     */
    suspend fun <T> ReceiveTurbine<DomainResult<T>>.awaitDataUpdates(): Pair<T, T> {
        val firstItem: DomainResult<T> = awaitItem()
        val first = (firstItem as DomainResult.Success<T>).data
        val secondItem: DomainResult<T> = awaitItem()
        val second = (secondItem as DomainResult.Success<T>).data
        return first to second
    }
}
