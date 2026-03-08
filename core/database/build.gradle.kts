plugins {
    id("com.dqc.kit.convention.kmp.library")
    // 注意：此模块不配置 sqldelight 插件，因为不生成数据库
    // 其他模块在自己的 build.gradle.kts 中配置 sqldelight 插件
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // SQLDelight - 提供 API 给其他模块使用
            api(libs.sqldelight.runtime)
            api(libs.sqldelight.coroutines.extensions)

            // Coroutines
            api(libs.kotlinx.coroutines.core)

            // DateTime
            api(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            api(libs.sqldelight.android.driver)
        }

        iosMain.dependencies {
            api(libs.sqldelight.native.driver)
        }

        jvmMain.dependencies {
            api(libs.sqldelight.sqlite.driver)
        }
    }
}

// 注意：此模块不配置 sqldelight 数据库
// 其他模块需要在自己的 build.gradle.kts 中配置：
// sqldelight {
//     databases {
//         create("MyDatabase") {
//             packageName.set("com.example.myapp.data")
//             ...
//         }
//     }
// }
