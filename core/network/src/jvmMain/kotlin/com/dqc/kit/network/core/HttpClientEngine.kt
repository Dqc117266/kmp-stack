package com.dqc.kit.network.core

import io.ktor.client.engine.okhttp.OkHttp

/**
 * JVM HTTP engine using OkHttp
 */
actual fun getPlatformEngine() = OkHttp.create()