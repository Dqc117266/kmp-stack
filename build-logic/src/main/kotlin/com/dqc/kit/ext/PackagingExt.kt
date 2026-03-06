package com.dqc.kit.ext

import com.android.build.api.dsl.Packaging

/**
 * Packaging extension to exclude license and meta files
 */
fun Packaging.excludeLicenseAndMetaFiles() {
    resources.excludes += listOf(
        "META-INF/DEPENDENCIES",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/license.txt",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/notice.txt",
        "META-INF/ASL2.0",
        "META-INF/*.kotlin_module"
    )
}
