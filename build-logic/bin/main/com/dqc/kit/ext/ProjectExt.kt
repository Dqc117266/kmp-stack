package com.dqc.kit.ext

import org.gradle.api.Project

/**
 * Helper to get project dependencies by path
 */
fun Project.project(path: String): Project = rootProject.project(path)

/**
 * 根据模块的物理路径，自动生成 Android 的 namespace。
 * 例如：路径为 ":feature:capture" -> 生成 "com.dqc.kit.feature.capture"
 */
internal fun Project.generateModuleNamespace(): String {
    val baseNamespace = "com.dqc.kit"
    // 获取相对路径并替换冒号为点，过滤掉空字符
    val modulePath = path.split(":")
        .filter { it.isNotEmpty() }
        .joinToString(".")

    return if (modulePath.isEmpty()) {
        baseNamespace
    } else {
        "$baseNamespace.$modulePath"
    }
}