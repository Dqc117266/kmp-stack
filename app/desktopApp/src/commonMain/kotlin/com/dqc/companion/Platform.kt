package com.dqc.companion

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform