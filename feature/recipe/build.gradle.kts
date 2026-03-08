plugins {
    id("com.dqc.kit.convention.kmp.library")
    alias(libs.plugins.sqldelight)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core modules
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.presentation)
            implementation(projects.core.logging)
        }
    }
}

sqldelight {
    databases {
        create("RecipeDatabase") {
            packageName.set("com.example.recipe.data")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            // 开发阶段禁用迁移验证
            verifyMigrations.set(false)
            // 使用同步 API
            generateAsync.set(false)
        }
    }
}
