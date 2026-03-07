package com.dqc.kit.ui.theme

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.dqc.kit.ui.foundation.AppColors
import com.dqc.kit.ui.foundation.AppShapes
import com.dqc.kit.ui.foundation.AppSpacing
import com.dqc.kit.ui.foundation.AppTypography
import com.dqc.kit.ui.foundation.darkColors
import com.dqc.kit.ui.foundation.defaultTypography
import com.dqc.kit.ui.foundation.lightColors

/**
 * CompositionLocal 定义
 * 用于在 Compose 树中向下传递主题属性
 */

/**
 * LocalAppColors 提供当前主题的颜色
 */
val LocalAppColors = staticCompositionLocalOf { lightColors() }

/**
 * LocalAppTypography 提供当前主题的字体
 */
val LocalAppTypography = staticCompositionLocalOf { defaultTypography() }

/**
 * LocalAppSpacing 提供当前主题的间距
 */
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing.Default }

/**
 * LocalAppShapes 提供当前主题的形状
 */
val LocalAppShapes = staticCompositionLocalOf { AppShapes.Default }

/**
 * AppTheme 入口
 *
 * 提供统一的主题访问方式，类似 MaterialTheme。
 * 使用方式：
 * ```kotlin
 * AppTheme(darkTheme = isSystemInDarkTheme()) {
 *     // 你的 UI 内容
 * }
 * ```
 *
 * 在组件中获取主题属性：
 * ```kotlin
 * val colors = AppTheme.colors
 * val typography = AppTheme.typography
 * ```
 */
object AppTheme {
    /**
     * 当前主题的颜色
     */
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    /**
     * 当前主题的字体
     */
    val typography: AppTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    /**
     * 当前主题的间距
     */
    val spacing: AppSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current

    /**
     * 当前主题的形状
     */
    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    /**
     * 当前内容颜色（用于文本和图标）
     */
    val contentColor: androidx.compose.ui.graphics.Color
        @Composable
        @ReadOnlyComposable
        get() = LocalContentColor.current
}

/**
 * 应用主题包装器
 *
 * @param darkTheme 是否使用深色主题
 * @param colors 自定义颜色（可选，默认根据 darkTheme 选择）
 * @param typography 自定义字体（可选，默认使用系统字体）
 * @param spacing 自定义间距（可选，默认使用标准间距）
 * @param shapes 自定义形状（可选，默认使用标准形状）
 * @param content 主题包裹的内容
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    colors: AppColors? = null,
    typography: AppTypography? = null,
    spacing: AppSpacing? = null,
    shapes: AppShapes? = null,
    content: @Composable () -> Unit
) {
    // 使用提供的颜色或根据主题模式选择默认颜色
    val themeColors = colors ?: if (darkTheme) darkColors() else lightColors()

    // 使用提供的字体或默认字体
    val themeTypography = typography ?: defaultTypography()

    // 使用提供的间距或默认间距
    val themeSpacing = spacing ?: AppSpacing.Default

    // 使用提供的形状或默认形状
    val themeShapes = shapes ?: AppShapes.Default

    // 提供所有 CompositionLocals
    CompositionLocalProvider(
        LocalAppColors provides themeColors,
        LocalAppTypography provides themeTypography,
        LocalAppSpacing provides themeSpacing,
        LocalAppShapes provides themeShapes,
        LocalContentColor provides themeColors.onBackground
    ) {
        content()
    }
}

/**
 * 应用主题变体 - 浅色主题
 */
@Composable
fun AppThemeLight(
    typography: AppTypography? = null,
    spacing: AppSpacing? = null,
    shapes: AppShapes? = null,
    content: @Composable () -> Unit
) {
    AppTheme(
        darkTheme = false,
        typography = typography,
        spacing = spacing,
        shapes = shapes,
        content = content
    )
}

/**
 * 应用主题变体 - 深色主题
 */
@Composable
fun AppThemeDark(
    typography: AppTypography? = null,
    spacing: AppSpacing? = null,
    shapes: AppShapes? = null,
    content: @Composable () -> Unit
) {
    AppTheme(
        darkTheme = true,
        typography = typography,
        spacing = spacing,
        shapes = shapes,
        content = content
    )
}
