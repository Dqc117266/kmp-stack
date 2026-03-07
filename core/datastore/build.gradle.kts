plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Common module for shared utilities
            api(projects.core.common)
            
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferences)
        }
    }
}