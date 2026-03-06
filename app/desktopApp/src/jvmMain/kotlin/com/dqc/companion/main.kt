package com.dqc.companion

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dqc.base.BuildConfig
import com.dqc.companion.di.initKoin
import com.dqc.companion.shared.App

fun main() {
    initKoin(
        baseUrl = BuildConfig.BASE_URL
    )

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Companion",
        ) {
            App()
        }
    }
}