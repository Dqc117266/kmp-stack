plugins {
    id("com.dqc.kit.convention.kmp.library")
    id("com.dqc.kit.convention.kmp.compose.library")
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            // Android-specific Compose
            api(libs.androidx.activity.compose)
        }

        jvmMain.dependencies {
            // JVM-specific dependencies (for Desktop)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

compose.resources {
    packageOfResClass = "com.dqc.kit.ui.generated.resources"
    generateResClass = auto
}
