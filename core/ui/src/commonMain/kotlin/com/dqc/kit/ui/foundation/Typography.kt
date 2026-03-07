package com.dqc.kit.ui.foundation

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 应用字体系统
 *
 * 基于 Material 3 Type Scale 规范：
 * - Display Large/Medium/Small: 大标题展示
 * - Headline Large/Medium/Small: 页面标题
 * - Title Large/Medium/Small: 卡片/列表标题
 * - Body Large/Medium/Small: 正文文本
 * - Label Large/Medium/Small: 按钮、标签、辅助文本
 *
 * @property displayLarge 最大展示文本
 * @property displayMedium 中等展示文本
 * @property displaySmall 小展示文本
 * @property headlineLarge 大标题
 * @property headlineMedium 中标题
 * @property headlineSmall 小标题
 * @property titleLarge 大标题（用于卡片/列表项）
 * @property titleMedium 中标题
 * @property titleSmall 小标题
 * @property bodyLarge 大正文（默认文本）
 * @property bodyMedium 中正文
 * @property bodySmall 小正文
 * @property labelLarge 大标签（用于按钮、输入框）
 * @property labelMedium 中标签
 * @property labelSmall 小标签（用于辅助文本、徽章）
 */
class AppTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,

    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,

    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,

    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,

    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle
) {
    /**
     * 转换为 Material 3 Typography
     */
    fun toMaterialTypography(): Typography = Typography(
        displayLarge = displayLarge,
        displayMedium = displayMedium,
        displaySmall = displaySmall,
        headlineLarge = headlineLarge,
        headlineMedium = headlineMedium,
        headlineSmall = headlineSmall,
        titleLarge = titleLarge,
        titleMedium = titleMedium,
        titleSmall = titleSmall,
        bodyLarge = bodyLarge,
        bodyMedium = bodyMedium,
        bodySmall = bodySmall,
        labelLarge = labelLarge,
        labelMedium = labelMedium,
        labelSmall = labelSmall
    )

    /**
     * 创建字体系统的副本
     */
    fun copy(
        displayLarge: TextStyle = this.displayLarge,
        displayMedium: TextStyle = this.displayMedium,
        displaySmall: TextStyle = this.displaySmall,
        headlineLarge: TextStyle = this.headlineLarge,
        headlineMedium: TextStyle = this.headlineMedium,
        headlineSmall: TextStyle = this.headlineSmall,
        titleLarge: TextStyle = this.titleLarge,
        titleMedium: TextStyle = this.titleMedium,
        titleSmall: TextStyle = this.titleSmall,
        bodyLarge: TextStyle = this.bodyLarge,
        bodyMedium: TextStyle = this.bodyMedium,
        bodySmall: TextStyle = this.bodySmall,
        labelLarge: TextStyle = this.labelLarge,
        labelMedium: TextStyle = this.labelMedium,
        labelSmall: TextStyle = this.labelSmall
    ): AppTypography = AppTypography(
        displayLarge = displayLarge,
        displayMedium = displayMedium,
        displaySmall = displaySmall,
        headlineLarge = headlineLarge,
        headlineMedium = headlineMedium,
        headlineSmall = headlineSmall,
        titleLarge = titleLarge,
        titleMedium = titleMedium,
        titleSmall = titleSmall,
        bodyLarge = bodyLarge,
        bodyMedium = bodyMedium,
        bodySmall = bodySmall,
        labelLarge = labelLarge,
        labelMedium = labelMedium,
        labelSmall = labelSmall
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppTypography) return false
        return displayLarge == other.displayLarge &&
            displayMedium == other.displayMedium &&
            displaySmall == other.displaySmall &&
            headlineLarge == other.headlineLarge &&
            headlineMedium == other.headlineMedium &&
            headlineSmall == other.headlineSmall &&
            titleLarge == other.titleLarge &&
            titleMedium == other.titleMedium &&
            titleSmall == other.titleSmall &&
            bodyLarge == other.bodyLarge &&
            bodyMedium == other.bodyMedium &&
            bodySmall == other.bodySmall &&
            labelLarge == other.labelLarge &&
            labelMedium == other.labelMedium &&
            labelSmall == other.labelSmall
    }

    override fun hashCode(): Int {
        var result = displayLarge.hashCode()
        result = 31 * result + displayMedium.hashCode()
        result = 31 * result + displaySmall.hashCode()
        result = 31 * result + headlineLarge.hashCode()
        result = 31 * result + headlineMedium.hashCode()
        result = 31 * result + headlineSmall.hashCode()
        result = 31 * result + titleLarge.hashCode()
        result = 31 * result + titleMedium.hashCode()
        result = 31 * result + titleSmall.hashCode()
        result = 31 * result + bodyLarge.hashCode()
        result = 31 * result + bodyMedium.hashCode()
        result = 31 * result + bodySmall.hashCode()
        result = 31 * result + labelLarge.hashCode()
        result = 31 * result + labelMedium.hashCode()
        result = 31 * result + labelSmall.hashCode()
        return result
    }
}

/**
 * 默认字体系统
 * 使用系统默认字体
 */
fun defaultTypography(fontFamily: FontFamily? = null): AppTypography {
    val defaultFont = fontFamily ?: FontFamily.Default

    return AppTypography(
        displayLarge = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 45.sp,
            lineHeight = 52.sp,
            letterSpacing = 0.sp
        ),
        displaySmall = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = defaultFont,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
}

/**
 * 紧凑字体系统（用于小屏幕或信息密度高的界面）
 */
fun compactTypography(fontFamily: FontFamily? = null): AppTypography {
    val defaultFont = fontFamily ?: FontFamily.Default
    val base = defaultTypography(defaultFont)

    return base.copy(
        displayLarge = base.displayLarge.copy(fontSize = 48.sp, lineHeight = 56.sp),
        displayMedium = base.displayMedium.copy(fontSize = 40.sp, lineHeight = 48.sp),
        displaySmall = base.displaySmall.copy(fontSize = 32.sp, lineHeight = 40.sp),
        headlineLarge = base.headlineLarge.copy(fontSize = 28.sp, lineHeight = 36.sp),
        headlineMedium = base.headlineMedium.copy(fontSize = 24.sp, lineHeight = 32.sp),
        headlineSmall = base.headlineSmall.copy(fontSize = 20.sp, lineHeight = 28.sp)
    )
}

/**
 * 平台特定的字体加载
 * 需要在各自平台实现
 */
expect fun getPlatformFontFamily(): FontFamily?

/**
 * 获取应用的默认字体
 */
@Composable
fun appFontFamily(): FontFamily {
    return getPlatformFontFamily() ?: FontFamily.Default
}
