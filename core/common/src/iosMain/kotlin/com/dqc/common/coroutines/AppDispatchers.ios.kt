package com.dqc.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * iOS 平台实现
 *
 * iOS 特殊处理：
 * - Main: 使用 Dispatchers.Main (确保在主线程执行 UI 操作)
 * - Default: 使用 Dispatchers.Default (线程池)
 * - IO: 在 iOS 上没有专门的 IO dispatcher，使用 Default
 *
 * 注意：kotlinx-coroutines 1.6.0+ 在 iOS 上已支持多线程，不再需要特殊的冻结处理
 */
actual object AppDispatchers {
    actual val Main: CoroutineDispatcher = Dispatchers.Main
    actual val Default: CoroutineDispatcher = Dispatchers.Default
    actual val IO: CoroutineDispatcher = Dispatchers.IO
}
