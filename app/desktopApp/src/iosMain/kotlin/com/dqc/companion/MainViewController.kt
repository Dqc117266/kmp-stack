package com.dqc.companion

import androidx.compose.ui.window.ComposeUIViewController
import com.dqc.companion.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Initialize Koin when the view controller is created
        initKoin(
            baseUrl = "https://api.example.com/",
            enableLogging = true
        )
    }
) { App() }