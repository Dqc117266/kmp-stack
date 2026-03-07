package com.dqc.kit.ui.foundation

import androidx.compose.ui.graphics.Color

/**
 * 创建浅色主题颜色配置
 *
 * @param primary 主色
 * @param onPrimary 主色上的文字
 * @param primaryContainer 主色容器背景
 * @param onPrimaryContainer 主色容器上的文字
 * @param secondary 次色
 * @param onSecondary 次色上的文字
 * @param secondaryContainer 次色容器背景
 * @param onSecondaryContainer 次色容器上的文字
 * @param tertiary 第三色
 * @param onTertiary 第三色上的文字
 * @param tertiaryContainer 第三色容器背景
 * @param onTertiaryContainer 第三色容器上的文字
 * @param background 主背景色
 * @param onBackground 主背景上的文字
 * @param surface 表面色
 * @param onSurface 表面上的文字
 * @param surfaceVariant 表面变体
 * @param onSurfaceVariant 表面变体上的文字
 * @param surfaceTint 表面色调
 * @param error 错误色
 * @param onError 错误色上的文字
 * @param errorContainer 错误容器背景
 * @param onErrorContainer 错误容器上的文字
 * @param outline 边框颜色
 * @param outlineVariant 弱化边框颜色
 * @param scrim 遮罩颜色
 * @param inverseSurface 反转表面色
 * @param inverseOnSurface 反转表面上的文字
 * @param inversePrimary 反转主色
 * @return AppColors 实例
 */
fun lightColors(
    primary: Color = LightPrimary,
    onPrimary: Color = LightOnPrimary,
    primaryContainer: Color = LightPrimaryContainer,
    onPrimaryContainer: Color = LightOnPrimaryContainer,
    secondary: Color = LightSecondary,
    onSecondary: Color = LightOnSecondary,
    secondaryContainer: Color = LightSecondaryContainer,
    onSecondaryContainer: Color = LightOnSecondaryContainer,
    tertiary: Color = LightTertiary,
    onTertiary: Color = LightOnTertiary,
    tertiaryContainer: Color = LightTertiaryContainer,
    onTertiaryContainer: Color = LightOnTertiaryContainer,
    background: Color = LightBackground,
    onBackground: Color = LightOnBackground,
    surface: Color = LightSurface,
    onSurface: Color = LightOnSurface,
    surfaceVariant: Color = LightSurfaceVariant,
    onSurfaceVariant: Color = LightOnSurfaceVariant,
    surfaceTint: Color = LightPrimary,
    error: Color = LightError,
    onError: Color = LightOnError,
    errorContainer: Color = LightErrorContainer,
    onErrorContainer: Color = LightOnErrorContainer,
    outline: Color = LightOutline,
    outlineVariant: Color = LightOutlineVariant,
    scrim: Color = Scrim,
    inverseSurface: Color = LightInverseSurface,
    inverseOnSurface: Color = LightInverseOnSurface,
    inversePrimary: Color = LightInversePrimary
): AppColors = AppColors(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    surfaceTint = surfaceTint,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = scrim,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    inversePrimary = inversePrimary,
    isLight = true
)

/**
 * 创建深色主题颜色配置
 *
 * @param primary 主色
 * @param onPrimary 主色上的文字
 * @param primaryContainer 主色容器背景
 * @param onPrimaryContainer 主色容器上的文字
 * @param secondary 次色
 * @param onSecondary 次色上的文字
 * @param secondaryContainer 次色容器背景
 * @param onSecondaryContainer 次色容器上的文字
 * @param tertiary 第三色
 * @param onTertiary 第三色上的文字
 * @param tertiaryContainer 第三色容器背景
 * @param onTertiaryContainer 第三色容器上的文字
 * @param background 主背景色
 * @param onBackground 主背景上的文字
 * @param surface 表面色
 * @param onSurface 表面上的文字
 * @param surfaceVariant 表面变体
 * @param onSurfaceVariant 表面变体上的文字
 * @param surfaceTint 表面色调
 * @param error 错误色
 * @param onError 错误色上的文字
 * @param errorContainer 错误容器背景
 * @param onErrorContainer 错误容器上的文字
 * @param outline 边框颜色
 * @param outlineVariant 弱化边框颜色
 * @param scrim 遮罩颜色
 * @param inverseSurface 反转表面色
 * @param inverseOnSurface 反转表面上的文字
 * @param inversePrimary 反转主色
 * @return AppColors 实例
 */
fun darkColors(
    primary: Color = DarkPrimary,
    onPrimary: Color = DarkOnPrimary,
    primaryContainer: Color = DarkPrimaryContainer,
    onPrimaryContainer: Color = DarkOnPrimaryContainer,
    secondary: Color = DarkSecondary,
    onSecondary: Color = DarkOnSecondary,
    secondaryContainer: Color = DarkSecondaryContainer,
    onSecondaryContainer: Color = DarkOnSecondaryContainer,
    tertiary: Color = DarkTertiary,
    onTertiary: Color = DarkOnTertiary,
    tertiaryContainer: Color = DarkTertiaryContainer,
    onTertiaryContainer: Color = DarkOnTertiaryContainer,
    background: Color = DarkBackground,
    onBackground: Color = DarkOnBackground,
    surface: Color = DarkSurface,
    onSurface: Color = DarkOnSurface,
    surfaceVariant: Color = DarkSurfaceVariant,
    onSurfaceVariant: Color = DarkOnSurfaceVariant,
    surfaceTint: Color = DarkPrimary,
    error: Color = DarkError,
    onError: Color = DarkOnError,
    errorContainer: Color = DarkErrorContainer,
    onErrorContainer: Color = DarkOnErrorContainer,
    outline: Color = DarkOutline,
    outlineVariant: Color = DarkOutlineVariant,
    scrim: Color = Scrim,
    inverseSurface: Color = DarkInverseSurface,
    inverseOnSurface: Color = DarkInverseOnSurface,
    inversePrimary: Color = DarkInversePrimary
): AppColors = AppColors(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    surfaceTint = surfaceTint,
    error = error,
    onError = onError,
    errorContainer = errorContainer,
    onErrorContainer = onErrorContainer,
    outline = outline,
    outlineVariant = outlineVariant,
    scrim = scrim,
    inverseSurface = inverseSurface,
    inverseOnSurface = inverseOnSurface,
    inversePrimary = inversePrimary,
    isLight = false
)
