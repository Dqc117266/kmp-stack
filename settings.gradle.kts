@file:Suppress("UnstableApiUsage") // 加上这一行

rootProject.name = "kmp-stack"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":app:androidApp")
include(":app:desktopApp")
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:presentation")
include(":core:datastore")
include(":core:network")
include(":core:database")
include(":core:logging")
include(":core:test")
include(":core:ui")
include(":core:di")
