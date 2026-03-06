package com.dqc.kit.ext

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.the

/**
 * Returns "libs" from version catalog.
 */
val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

/**
 * Returns "versions" from version catalog.
 */
val Project.versions: LibrariesForLibs.VersionAccessors
    get() = the<LibrariesForLibs>().versions

/**
 * Helper to add dependencies using version catalog Provider (for DependencyHandlerScope)
 */
fun DependencyHandlerScope.implementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("implementation", provider)
}

fun DependencyHandlerScope.api(provider: Provider<MinimalExternalModuleDependency>) {
    add("api", provider)
}

fun DependencyHandlerScope.testImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("testImplementation", provider)
}

fun DependencyHandlerScope.androidMainImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("androidMainImplementation", provider)
}

fun DependencyHandlerScope.commonMainImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("commonMainImplementation", provider)
}

fun DependencyHandlerScope.commonTestImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("commonTestImplementation", provider)
}

fun DependencyHandlerScope.jvmMainImplementation(provider: Provider<MinimalExternalModuleDependency>) {
    add("jvmMainImplementation", provider)
}

/**
 * Helper to add dependencies using version catalog Provider (for plain DependencyHandler)
 */
fun DependencyHandler.implementation(provider: Provider<MinimalExternalModuleDependency>): Dependency? {
    return add("implementation", provider)
}

fun DependencyHandler.api(provider: Provider<MinimalExternalModuleDependency>): Dependency? {
    return add("api", provider)
}

fun DependencyHandler.testImplementation(provider: Provider<MinimalExternalModuleDependency>): Dependency? {
    return add("testImplementation", provider)
}

fun DependencyHandler.androidMainImplementation(provider: Provider<MinimalExternalModuleDependency>): Dependency? {
    return add("androidMainImplementation", provider)
}

fun DependencyHandler.commonMainImplementation(provider: Provider<MinimalExternalModuleDependency>): Dependency? {
    return add("commonMainImplementation", provider)
}

fun DependencyHandler.commonTestImplementation(provider: Provider<MinimalExternalModuleDependency>): Dependency? {
    return add("commonTestImplementation", provider)
}

fun DependencyHandler.jvmMainImplementation(provider: Provider<MinimalExternalModuleDependency>): Dependency? {
    return add("jvmMainImplementation", provider)
}

// Project dependencies (remain the same)
fun DependencyHandlerScope.implementation(project: Project): Dependency? =
    add("implementation", project)

fun DependencyHandlerScope.api(project: Project): Dependency? =
    add("api", project)

fun DependencyHandler.implementation(project: Project): Dependency? =
    add("implementation", project)

fun DependencyHandler.api(project: Project): Dependency? =
    add("api", project)

// Extension to get project by path
fun Project.projectBy(path: String): Project = rootProject.project(path)
