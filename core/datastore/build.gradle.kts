plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferences)

        }
//        androidMain.dependencies {
//            implementation(libs.androidx.datastore.preferences)
//        }
    }
}