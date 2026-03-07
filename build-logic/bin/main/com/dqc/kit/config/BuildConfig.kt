package com.dqc.kit.config

import org.gradle.api.JavaVersion

/**
 * Build configuration constants
 */
object BuildConfig {
    val JAVA_VERSION: JavaVersion = JavaVersion.VERSION_17
    const val JVM_TOOLCHAIN_VERSION = 17
    const val COMPILE_SDK = 36
    const val MIN_SDK = 24
    const val TARGET_SDK = 36
}
