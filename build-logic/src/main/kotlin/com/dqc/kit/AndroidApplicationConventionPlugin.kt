package com.dqc.kit

import com.android.build.api.dsl.ApplicationExtension
import com.dqc.kit.ext.versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Android Application Convention Plugin for AGP 9.0+
 *
 * This plugin configures a pure Android application module.
 * It does NOT include KMP plugin - use this for the androidApp module only.
 *
 * Features:
 * - Applies com.android.application plugin
 * - Configures Android settings (namespace, compileSdk, minSdk, targetSdk)
 * - Enables Compose
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<ApplicationExtension> {
                // 移除 namespace = "com.dqc.companion"，交由壳工程自行决定
                compileSdk = versions.android.compileSdk.get().toInt()

                defaultConfig {
                    // 移除 applicationId = "..."，交由壳工程的 build.gradle.kts 决定
                    minSdk = versions.android.minSdk.get().toInt()
                    targetSdk = versions.android.compileSdk.get().toInt()
                }

                buildFeatures {
                    compose = true
                }

                compileOptions {
                    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_17
                    targetCompatibility = org.gradle.api.JavaVersion.VERSION_17
                }
            }
        }
    }
}
