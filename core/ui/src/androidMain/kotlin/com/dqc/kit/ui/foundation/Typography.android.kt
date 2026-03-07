package com.dqc.kit.ui.foundation

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * Android 平台字体加载实现
 */
actual fun getPlatformFontFamily(): FontFamily? {
    // Android 使用系统默认字体
    // 如需自定义字体，请放在 res/font 目录并使用以下方式加载：
    // return FontFamily(
    //     Font(R.font.your_font_regular, FontWeight.Normal),
    //     Font(R.font.your_font_medium, FontWeight.Medium),
    //     Font(R.font.your_font_bold, FontWeight.Bold)
    // )
    return null
}
