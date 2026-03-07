plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // 仅依赖 kotlinx-coroutines 和 kotlinx-datetime
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}
