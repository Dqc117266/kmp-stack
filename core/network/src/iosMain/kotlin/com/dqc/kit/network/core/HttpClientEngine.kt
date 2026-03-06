package com.dqc.kit.network.core

import io.ktor.client.engine.darwin.Darwin

/**
 * iOS HTTP engine using Darwin
 */
actual fun getPlatformEngine() = Darwin.create()