package com.dqc.test

/**
 * Mock 辅助扩展
 *
 * 提供便捷的 Mock 创建和配置方法
 *
 * 注意：这些扩展主要适用于 JVM 平台，
 * 对于非 JVM 平台，建议使用 Fake 实现
 */

/**
 * 创建简单的 Fake 实现
 *
 * 使用示例：
 * ```kotlin
 * class FakeUserRepository : UserRepository {
 *     private val users = mutableMapOf<String, User>()
 *
 *     override suspend fun getUser(id: String): User {
 *         return users[id] ?: throw NotFoundException()
 *     }
 *
 *     fun addUser(user: User) {
 *         users[user.id] = user
 *     }
 * }
 * ```
 */
abstract class FakeRepository<T> {
    protected val data = mutableMapOf<String, T>()

    fun add(key: String, value: T) {
        data[key] = value
    }

    fun addAll(items: Map<String, T>) {
        data.putAll(items)
    }

    fun clear() {
        data.clear()
    }

    fun get(key: String): T? = data[key]

    fun getAll(): List<T> = data.values.toList()

    fun remove(key: String): T? = data.remove(key)
}

/**
 * Stub 配置 DSL
 */
class StubBuilder<T, R> {
    private val stubs = mutableListOf<Pair<(T) -> Boolean, R>>()
    private var defaultAnswer: ((T) -> R)? = null

    /**
     * 当参数匹配时返回指定值
     */
    fun whenMatches(predicate: (T) -> Boolean, returnValue: R) {
        stubs.add(predicate to returnValue)
    }

    /**
     * 当参数等于指定值时返回
     */
    fun whenEquals(input: T, returnValue: R) {
        whenMatches({ it == input }, returnValue)
    }

    /**
     * 设置默认返回值
     */
    fun default(answer: (T) -> R) {
        defaultAnswer = answer
    }

    /**
     * 构建并返回结果
     */
    fun execute(input: T): R {
        for ((predicate, result) in stubs) {
            if (predicate(input)) {
                return result
            }
        }
        return defaultAnswer?.invoke(input)
            ?: throw IllegalStateException("No stub found for input: $input")
    }
}

/**
 * 创建简单的 Stub
 *
 * 使用示例：
 * ```kotlin
 * val stub = stub<String, Int> {
 *     whenEquals("one", 1)
 *     whenEquals("two", 2)
 *     default { -1 }
 * }
 *
 * assertEquals(1, stub.execute("one"))
 * assertEquals(-1, stub.execute("unknown"))
 * ```
 */
inline fun <T, R> stub(block: StubBuilder<T, R>.() -> Unit): (T) -> R {
    val builder = StubBuilder<T, R>()
    builder.block()
    return { input -> builder.execute(input) }
}

/**
 * 可配置的 Test Double
 */
class ConfigurableTestDouble<T> {
    private val responses = mutableMapOf<String, Any>()
    private val callLog = mutableListOf<Pair<String, List<Any?>>>()

    /**
     * 记录方法调用
     */
    fun recordCall(methodName: String, vararg args: Any?) {
        callLog.add(methodName to args.toList())
    }

    /**
     * 设置方法返回值
     */
    fun <R> setResponse(methodName: String, response: R) {
        responses[methodName] = response as Any
    }

    /**
     * 获取方法返回值
     */
    @Suppress("UNCHECKED_CAST")
    fun <R> getResponse(methodName: String): R {
        return responses[methodName] as R
    }

    /**
     * 获取方法调用次数
     */
    fun getCallCount(methodName: String): Int {
        return callLog.count { it.first == methodName }
    }

    /**
     * 验证方法被调用
     */
    fun verifyCalled(methodName: String, times: Int = 1) {
        val actualTimes = getCallCount(methodName)
        if (actualTimes != times) {
            throw AssertionError(
                "Expected $methodName to be called $times times, but was called $actualTimes times"
            )
        }
    }

    /**
     * 验证方法从未被调用
     */
    fun verifyNeverCalled(methodName: String) {
        verifyCalled(methodName, 0)
    }

    /**
     * 获取最后一次调用的参数
     */
    fun getLastCallArgs(methodName: String): List<Any?>? {
        return callLog.lastOrNull { it.first == methodName }?.second
    }

    /**
     * 清除所有记录
     */
    fun clear() {
        responses.clear()
        callLog.clear()
    }
}

/**
 * 创建测试用的回调收集器
 */
class CallbackCollector<T> {
    private val callbacks = mutableListOf<T>()

    val collected: List<T>
        get() = callbacks.toList()

    val first: T?
        get() = callbacks.firstOrNull()

    val last: T?
        get() = callbacks.lastOrNull()

    val count: Int
        get() = callbacks.size

    fun onCallback(value: T) {
        callbacks.add(value)
    }

    fun clear() {
        callbacks.clear()
    }

    fun assertCallbackCount(expected: Int) {
        if (callbacks.size != expected) {
            throw AssertionError(
                "Expected callback to be called $expected times, but was called ${callbacks.size} times"
            )
        }
    }

    fun assertNoCallbacks() {
        assertCallbackCount(0)
    }
}

/**
 * 创建 Lambda 收集器
 */
fun <T> createCallbackCollector(): Pair<CallbackCollector<T>, (T) -> Unit> {
    val collector = CallbackCollector<T>()
    return collector to collector::onCallback
}

/**
 * 模拟延迟的辅助函数
 */
//fun simulateDelay(millis: Long = 100) {
//    Thread.sleep(millis)
//}

/**
 * 创建顺序返回值
 */
class SequentialAnswer<T>(vararg values: T) {
    private val queue = ArrayDeque(values.toList())
    private var defaultAnswer: (() -> T)? = null

    fun next(): T {
        return queue.removeFirstOrNull()
            ?: defaultAnswer?.invoke()
            ?: throw IllegalStateException("No more answers available")
    }

    fun setDefault(answer: () -> T) {
        defaultAnswer = answer
    }

    fun reset(vararg values: T) {
        queue.clear()
        queue.addAll(values)
    }

    fun remainingCount(): Int = queue.size
}

/**
 * 创建抛出异常的 Stub
 */
class ExceptionStub(private val exception: Throwable) {
    fun throwException(): Nothing = throw exception
}

fun throwStub(exception: Throwable = RuntimeException("Stub exception")): ExceptionStub {
    return ExceptionStub(exception)
}
