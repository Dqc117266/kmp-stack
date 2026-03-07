plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Kotlin Test - 仅使用 api 导出
            api(libs.kotlin.test)

            // Coroutines Test
            api(libs.kotlinx.coroutines.test)

            // Turbine for Flow testing
            api(libs.turbine)

            // Koin Test
            api(libs.koin.test)

            // KotlinX DateTime (测试数据工厂使用)
            api(libs.kotlinx.datetime)

            api(projects.core.domain)
        }

        androidMain.dependencies {
            // Android 特定测试库
            api(libs.androidx.testExt.junit)
            api(libs.androidx.espresso.core)
        }

        jvmMain.dependencies {
            // JVM 特定测试库
            api(libs.junit5.api)
            api(libs.junit5.engine)

            // MockK for mocking (JVM only,但 api 导出用于 JVM 平台)
            api(libs.mockk)
            api(libs.mockk.common)
        }
    }
}
