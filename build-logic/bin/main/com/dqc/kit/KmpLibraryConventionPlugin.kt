package com.dqc.kit

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.dqc.kit.ext.generateModuleNamespace
import com.dqc.kit.ext.libs
import com.dqc.kit.ext.versions
import org.gradle.kotlin.dsl.invoke

class KmpLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            // 1. 核心变更：使用 AGP 9.0 推荐的 KMP Android 库插件
            pluginManager.apply("org.jetbrains.kotlin.multiplatform")
            pluginManager.apply("com.android.kotlin.multiplatform.library")

            // 2. 统一在 kotlin 扩展块中配置
            extensions.configure<KotlinMultiplatformExtension> {

                // 3. 使用新的 androidLibrary 块代替旧的 androidTarget()
                // 这是 AGP 9.0+ 的标准写法，解决了 "androidTarget is deprecated" 警告
                extensions.configure<KotlinMultiplatformAndroidLibraryTarget> {
                    namespace = generateModuleNamespace()
                    compileSdk = versions.android.compileSdk.get().toInt()

                    minSdk = versions.android.minSdk.get().toInt()

                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }

                jvm {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }

                // iOS 配置
//                iosX64()
                iosArm64()
                iosSimulatorArm64()

                // 默认层次结构模板
                applyDefaultHierarchyTemplate()

                sourceSets {
                    commonMain.dependencies {
                        implementation(libs.koin.core)
                        implementation(libs.koin.compose.viewmodel)
                    }
                    androidMain.dependencies {
                        implementation(libs.koin.android)
                        implementation(libs.koin.compose)
                    }
                }
            }
        }
    }
}