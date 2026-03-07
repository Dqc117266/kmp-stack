plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // 依赖 domain 模块
            implementation(projects.core.domain)

            // 纯 Kotlin 依赖
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
    }
}
