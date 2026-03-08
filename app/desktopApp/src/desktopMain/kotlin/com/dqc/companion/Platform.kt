package com.dqc.companion

interface Platform {
    val name: String
}

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

fun getPlatform(): Platform = JVMPlatform()
