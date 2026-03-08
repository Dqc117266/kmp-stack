@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.dqc.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Android 平台实现
 */
actual object AppDispatchers {
    actual val Main: CoroutineDispatcher = Dispatchers.Main
    actual val Default: CoroutineDispatcher = Dispatchers.Default
    actual val IO: CoroutineDispatcher = Dispatchers.IO
}
