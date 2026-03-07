plugins {
    id("com.dqc.kit.convention.kmp.library")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Domain layer - 纯净的实体和接口
            api(projects.core.domain)

            // Network layer - 处理 HTTP 通信
            api(projects.core.network)

            // DataStore - 键值对存储
            implementation(projects.core.datastore)

            // Common utilities
            implementation(projects.core.common)

            // Logging
            implementation(projects.core.logging)

            // SQLDelight - 跨平台数据库
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            // Ktor client (for type-safe requests)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Kotlinx Serialization
            implementation(libs.kotlinx.serialization.json)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            // DI
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            // SQLDelight Android driver
            implementation(libs.sqldelight.android.driver)
        }

        jvmMain.dependencies {
            // SQLDelight JVM driver (SQLite JDBC)
            implementation(libs.sqldelight.sqlite.driver)
        }

        iosMain.dependencies {
            // SQLDelight Native driver
            implementation(libs.sqldelight.native.driver)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
        }
    }
}

// SQLDelight configuration
sqldelight {
    databases {
        create("AppDatabase") {
            // Database package
            packageName.set("com.dqc.kit.data.local.database")

            // Schema directory for migrations
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))

            // Generated sources directory
            deriveSchemaFromMigrations.set(false)

            // Migration version
            verifyMigrations.set(true)
        }
    }
}
