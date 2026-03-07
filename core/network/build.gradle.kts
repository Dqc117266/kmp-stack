plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Common module for shared utilities
            api(projects.core.common)
            
            // DataStore module for token persistence
            implementation(projects.core.datastore)
            
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.serialization)
            
            // Kotlinx (inherited from core:common)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.cio)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}