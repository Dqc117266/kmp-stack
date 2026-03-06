import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id("com.dqc.kit.convention.kmp.application")
}

kotlin {
    sourceSets {
        getByName("jvmMain").dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.dqc.companion.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.dqc.companion"
            packageVersion = "1.0.0"
        }
    }
}
