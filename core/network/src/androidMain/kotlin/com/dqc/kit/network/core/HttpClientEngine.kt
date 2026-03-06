package com.dqc.kit.network.core

import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android HTTP engine using OkHttp
 */
actual fun getPlatformEngine() = OkHttp.create()