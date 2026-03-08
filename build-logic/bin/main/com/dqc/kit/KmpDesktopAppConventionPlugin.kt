package com.dqc.kit

import com.dqc.kit.ext.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

/**
 * KMP Desktop App Convention Plugin
 *
 * 仅配置桌面端应用，使用 desktopMain 作为唯一源集
 * 不创建 commonMain、jvmMain 等标准 KMP 源集
 */
class KmpDesktopAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 应用必要插件
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("org.jetbrains.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<KotlinMultiplatformExtension> {
                // 只配置桌面端 JVM 目标，命名为 desktop
                jvm("desktop") {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }

                // 配置 sourceSets，desktopMain 由 jvm("desktop") 自动创建
                sourceSets {
                    named("desktopMain") {
                        dependencies {
                            // Material 3
                            implementation(libs.compose.material3)
                            // Compose 基础
                            implementation(libs.compose.runtime)
                            implementation(libs.compose.foundation)
                            implementation(libs.compose.ui)
                            implementation(libs.compose.uiToolingPreview)
                            implementation(libs.compose.components.resources)
                            // Lifecycle
                            implementation(libs.androidx.lifecycle.viewmodelCompose)
                            implementation(libs.androidx.lifecycle.runtimeCompose)
                            // Coroutines
                            implementation(libs.kotlinx.coroutines.core)
                            implementation(libs.kotlinx.coroutinesSwing)
                        }
                    }
                }
            }
        }
    }
}
