plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core modules - 底层核心模块
            api(projects.core.domain)
            api(projects.core.data)
            api(projects.core.network)
            api(projects.core.datastore)
            api(projects.core.database)
            api(projects.core.logging)
            api(projects.core.common)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            // Koin Android - 提供 androidContext() 支持
            api(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
