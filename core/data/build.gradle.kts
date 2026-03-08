plugins {
    id("com.dqc.kit.convention.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Domain layer - 纯净的实体和接口
            api(projects.core.domain)

            // Network layer - 处理 HTTP 通信
            api(projects.core.network)

            // Common utilities
            implementation(projects.core.common)

            // Logging
            implementation(projects.core.logging)

            // SQLDelight - 跨平台数据库（仅提供运行时和扩展，不配置具体数据库）
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)

            // Ktor - 网络请求客户端（core:data 提供的工具类需要）
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)

            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
        }
    }
}
