package com.dqc.kit.ui.foundation

import androidx.compose.ui.text.font.FontFamily

/**
 * iOS 平台字体加载实现
 */
actual fun getPlatformFontFamily(): FontFamily? {
    // iOS 使用系统默认字体（San Francisco）
    // 如需自定义字体，需要：
    // 1. 将字体文件添加到 iOS 项目的 Resources
    // 2. 在 Info.plist 中注册字体
    // 3. 使用 platform.UIKit.UIFont 加载
    return null
}
