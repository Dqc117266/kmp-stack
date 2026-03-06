package com.dqc.kit

import com.dqc.kit.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * KMP Application Convention Plugin for AGP 9.0+
 *
 * This plugin configures a KMP application module with Desktop/JVM support.
 * This is for the desktop app module that doesn't need Android or iOS.
 *
 * Features:
 * - Applies org.jetbrains.kotlin.multiplatform plugin
 * - Applies Compose plugins
 * - Configures JVM Desktop target only
 * - Sets up Compose Desktop
 */
class KmpApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // Apply plugins
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.compose.hot-reload")

            // Configure KMP
            extensions.configure<KotlinMultiplatformExtension> {
                // JVM/Desktop target only
                jvm {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }

                // Apply hierarchy template
                applyDefaultHierarchyTemplate()

                // Configure source sets
                sourceSets.getByName("commonMain").dependencies {
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.ui)
                    implementation(libs.compose.components.resources)
                    implementation(libs.compose.uiToolingPreview)
                    implementation(libs.androidx.lifecycle.viewmodelCompose)
                    implementation(libs.androidx.lifecycle.runtimeCompose)
                }

                sourceSets.getByName("jvmMain").dependencies {
                    // Desktop dependencies configured in module build.gradle.kts
                }

                sourceSets.getByName("commonTest").dependencies {
                    implementation(libs.kotlin.test)
                }
            }
        }
    }
}
