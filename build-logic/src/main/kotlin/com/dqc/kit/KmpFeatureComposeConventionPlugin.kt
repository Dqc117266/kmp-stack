package com.dqc.kit

import com.dqc.kit.config.BuildConfig
import com.dqc.kit.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP Feature Compose Convention Plugin
 *
 * This plugin configures a KMP feature module with Compose support.
 * It automatically sets up:
 * - iOS targets (x64, arm64, simulatorArm64) with framework
 * - Android library configuration
 * - JVM target for Desktop
 * - All common dependencies (Compose, Lifecycle, etc.)
 *
 * Usage:
 * ```kotlin
 * plugins {
 *     id("com.dqc.kit.convention.kmp.feature")
 * }
 *
 * kotlin {
 *     androidLibrary {
 *         namespace = "com.example.feature"
 *         compileSdk = 36
 *         minSdk = 24
 *     }
 * }
 * ```
 */
class KmpFeatureComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.dqc.kit.convention.kmp.compose.library")

            extensions.configure<KotlinMultiplatformExtension> {
                // 配置 iOS 框架导出 (如果该 Feature 需要独立被 iOS 引用)
                listOf(
                    iosX64(),
                    iosArm64(),
                    iosSimulatorArm64()
                ).forEach { iosTarget ->
                    iosTarget.binaries.framework {
                        baseName = project.name.replaceFirstChar { it.uppercase() }
                        isStatic = true
                    }
                }

                sourceSets.apply {
                    commonMain.dependencies {
                        // 每一个 Feature 都必须遵循我们的 MVI 规范
                        // 所以这里统一引入你自己的 core:mvi 模块
//                        implementation(project(":core:mvi"))

                        // 注意：如果需要网络请求，不要在这里写 Ktor！
                        // 应该让需要网络的 Feature 自己去 implementation(project(":core:network"))
                    }
                }
            }
        }
    }
}
