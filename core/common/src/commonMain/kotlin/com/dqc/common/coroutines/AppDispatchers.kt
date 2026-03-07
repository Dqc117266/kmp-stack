@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.dqc.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * 跨平台的 CoroutineDispatcher 抽象
 *
 * 使用 expect/actual 模式为不同平台提供正确的调度器实现：
 * - Android: Main = Main, Default = Default, IO = IO
 * - iOS: Main = MainQueueDispatcher, Default/IO = Default
 * - JVM: 与 Android 相同
 *
 * 设计用于依赖注入，方便单元测试时替换为 TestDispatcher
 */
expect object AppDispatchers {
    /**
     * 主线程调度器 - 用于 UI 操作
     * Android: Main dispatcher
     * iOS: MainQueueDispatcher (确保在主线程执行)
     */
    val Main: CoroutineDispatcher

    /**
     * 默认调度器 - 用于 CPU 密集型任务
     * 基于线程池，线程数等于 CPU 核心数
     */
    val Default: CoroutineDispatcher

    /**
     * IO 调度器 - 用于阻塞 IO 操作
     * 基于线程池，可根据需要创建大量线程
     */
    val IO: CoroutineDispatcher
}

/**
 * 调度器提供者接口 - 用于依赖注入
 *
 * 示例用法 (Koin):
 * ```kotlin
 * single<DispatcherProvider> { DefaultDispatcherProvider() }
 *
 * class MyRepository(
 *     private val dispatchers: DispatcherProvider
 * ) {
 *     suspend fun fetchData() = withContext(dispatchers.io) {
 *         // 网络请求
 *     }
 * }
 * ```
 *
 * 单元测试:
 * ```kotlin
 * class TestDispatcherProvider : DispatcherProvider {
 *     override val main = StandardTestDispatcher()
 *     override val default = StandardTestDispatcher()
 *     override val io = StandardTestDispatcher()
 * }
 * ```
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

/**
 * 默认调度器提供者实现
 */
class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = AppDispatchers.Main
    override val default: CoroutineDispatcher = AppDispatchers.Default
    override val io: CoroutineDispatcher = AppDispatchers.IO
}
