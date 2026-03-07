plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Kermit logging
            api(libs.kermit)
            // Kotlin Reflect (for KClass)
            implementation(kotlin("reflect"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
