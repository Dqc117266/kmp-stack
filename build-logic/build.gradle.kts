import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.dqc.buildlogic"

val javaVersion = "17"

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget(javaVersion)
    }

    jvmToolchain(javaVersion.toInt())
}

dependencies {
    // Android Gradle Plugin
    implementation(libs.android.gradlePlugin)
    
    // Kotlin Gradle Plugin
    implementation(libs.kotlin.gradlePlugin)
    
    // Compose Compiler Plugin
    implementation(libs.compose.gradlePlugin)
    
    // Other build logic dependencies
    implementation(libs.ksp.gradlePlugin)
    implementation(libs.spotless.gradlePlugin)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.test.logger.gradlePlugin)
    implementation(libs.junit5.gradlePlugin)
    implementation(libs.easy.launcher.gradlePlugin)
    implementation(libs.about.libraries.gradlePlugin)
    implementation(libs.navigationSafeArgs)

    // 这一行非常关键：它允许你在插件代码里通过 libs.xxx 访问版本目录
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        // Android Application module (androidApp) - pure Android app entry point
        register("androidApplication") {
            id = "com.dqc.kit.convention.android.application"
            implementationClass = "com.dqc.kit.AndroidApplicationConventionPlugin"
        }

        // KMP Library module (shared/*) - shared KMP code with Android support
        register("kmpLibrary") {
            id = "com.dqc.kit.convention.kmp.library"
            implementationClass = "com.dqc.kit.KmpLibraryConventionPlugin"
        }

        // KMP Compose Library module - shared KMP code with Compose support
        register("kmpComposeLibrary") {
            id = "com.dqc.kit.convention.kmp.compose.library"
            implementationClass = "com.dqc.kit.KmpComposeLibraryConventionPlugin"
        }

        // KMP Application module (composeApp) - Desktop/JVM app
        register("kmpApplication") {
            id = "com.dqc.kit.convention.kmp.application"
            implementationClass = "com.dqc.kit.KmpApplicationConventionPlugin"
        }

        // KMP Feature module - full KMP feature with Compose and all targets
        register("kmpFeature") {
            id = "com.dqc.kit.convention.kmp.feature"
            implementationClass = "com.dqc.kit.KmpFeatureComposeConventionPlugin"
        }
    }
}
