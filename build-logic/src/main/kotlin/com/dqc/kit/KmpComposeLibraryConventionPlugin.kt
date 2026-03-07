package com.dqc.kit

import com.dqc.kit.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * KMP Compose Library Convention Plugin for AGP 9.0+
 *
 * This plugin configures a KMP library module with Compose support.
 * It applies both KMP and Compose plugins.
 *
 * Usage:
 * ```kotlin
 * plugins {
 *     id("com.dqc.kit.convention.kmp.compose.library")
 * }
 *
 * kotlin {
 *     androidLibrary {
 *         namespace = "com.example.mylibrary"
 *         compileSdk = 36
 *         minSdk = 24
 *     }
 *
 *     jvm()
 *     iosArm64()
 *     iosSimulatorArm64()
 *
 *     sourceSets {
 *         commonMain.dependencies {
 *             implementation(compose.runtime)
 *             implementation(compose.foundation)
 *             implementation(compose.material3)
 *         }
 *     }
 * }
 * ```
 */
class KmpComposeLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 依赖基础的 KMP 库插件
            pluginManager.apply("com.dqc.kit.convention.kmp.library")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.apply {
                    commonMain.dependencies {
                        // 纯粹的 UI 基础库，绝不包含 Ktor/Koin 等业务库
                        implementation(libs.compose.runtime)
                        implementation(libs.compose.foundation)
                        implementation(libs.compose.material3)
                        implementation(libs.compose.ui)
                        implementation(libs.compose.components.resources)
                        implementation(libs.navigation.compose)

                        // 跨平台 ViewModel 支持
                        implementation(libs.androidx.lifecycle.viewmodelCompose)
                        implementation(libs.androidx.lifecycle.runtimeCompose)
                    }
                }
            }
        }
    }
}
