package com.dqc.kit.ui.foundation

import androidx.compose.ui.text.font.FontFamily

/**
 * JVM 平台字体加载实现（Desktop）
 */
actual fun getPlatformFontFamily(): FontFamily? {
    // Desktop 使用系统默认字体
    // 如需自定义字体，需要将字体文件打包到资源目录：
    // return FontFamily(
    //     Font("fonts/your_font.ttf", FontWeight.Normal)
    // )
    return null
}
