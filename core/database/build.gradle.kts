plugins {
    id("com.dqc.kit.convention.kmp.library")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)

            // DateTime
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.timelinesolutions.kmpstack.core.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
            generateAsync.set(true)
        }
    }
}
