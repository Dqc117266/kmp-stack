package com.dqc.kit.network.core

import io.ktor.client.engine.HttpClientEngine

/**
 * Platform-specific HTTP client engine
 * Android: OkHttp, iOS: Darwin
 */
expect fun getPlatformEngine(): HttpClientEngine