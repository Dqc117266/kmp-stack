package com.dqc.kmpstack

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform