package com.dqc.kit.ui.foundation

import androidx.compose.ui.graphics.Color

/**
 * 应用颜色系统
 *
 * 采用语义化命名，与具体色值解耦。
 * 通过 lightColors/darkColors 函数创建具体实例。
 *
 * @property primary 主色 - 用于主要操作、按钮、强调
 * @property onPrimary 主色上的文字/图标颜色
 * @property primaryContainer 主色容器背景
 * @property onPrimaryContainer 主色容器上的文字
 *
 * @property secondary 次色 - 用于次要操作、筛选标签
 * @property onSecondary 次色上的文字/图标颜色
 * @property secondaryContainer 次色容器背景
 * @property onSecondaryContainer 次色容器上的文字
 *
 * @property tertiary 第三色 - 用于平衡和对比
 * @property onTertiary 第三色上的文字/图标颜色
 *
 * @property background 主背景色
 * @property onBackground 主背景上的文字
 * @property surface 卡片、Sheet、对话框背景
 * @property onSurface 表面上的主要文字
 * @property surfaceVariant 表面变体背景
 * @property onSurfaceVariant 表面变体上的文字
 *
 * @property error 错误色
 * @property onError 错误色上的文字
 * @property errorContainer 错误容器背景
 * @property onErrorContainer 错误容器上的文字
 *
 * @property outline 边框、分割线颜色
 * @property outlineVariant 弱化边框颜色
 *
 * @property scrim 遮罩层颜色（如弹窗背景）
 * @property inverseSurface 反转表面色（用于 Snackbar 等）
 * @property inverseOnSurface 反转表面上的文字
 * @property inversePrimary 反转主色
 */
class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,

    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,

    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,

    val background: Color,
    val onBackground: Color,

    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,

    val surfaceTint: Color,

    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    val outline: Color,
    val outlineVariant: Color,

    val scrim: Color,

    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,

    val isLight: Boolean
) {
    /**
     * 创建颜色的副本，修改指定属性
     */
    fun copy(
        primary: Color = this.primary,
        onPrimary: Color = this.onPrimary,
        primaryContainer: Color = this.primaryContainer,
        onPrimaryContainer: Color = this.onPrimaryContainer,
        secondary: Color = this.secondary,
        onSecondary: Color = this.onSecondary,
        secondaryContainer: Color = this.secondaryContainer,
        onSecondaryContainer: Color = this.onSecondaryContainer,
        tertiary: Color = this.tertiary,
        onTertiary: Color = this.onTertiary,
        tertiaryContainer: Color = this.tertiaryContainer,
        onTertiaryContainer: Color = this.onTertiaryContainer,
        background: Color = this.background,
        onBackground: Color = this.onBackground,
        surface: Color = this.surface,
        onSurface: Color = this.onSurface,
        surfaceVariant: Color = this.surfaceVariant,
        onSurfaceVariant: Color = this.onSurfaceVariant,
        surfaceTint: Color = this.surfaceTint,
        error: Color = this.error,
        onError: Color = this.onError,
        errorContainer: Color = this.errorContainer,
        onErrorContainer: Color = this.onErrorContainer,
        outline: Color = this.outline,
        outlineVariant: Color = this.outlineVariant,
        scrim: Color = this.scrim,
        inverseSurface: Color = this.inverseSurface,
        inverseOnSurface: Color = this.inverseOnSurface,
        inversePrimary: Color = this.inversePrimary,
        isLight: Boolean = this.isLight
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
        isLight = isLight
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppColors) return false
        return primary == other.primary &&
            onPrimary == other.onPrimary &&
            primaryContainer == other.primaryContainer &&
            onPrimaryContainer == other.onPrimaryContainer &&
            secondary == other.secondary &&
            onSecondary == other.onSecondary &&
            secondaryContainer == other.secondaryContainer &&
            onSecondaryContainer == other.onSecondaryContainer &&
            tertiary == other.tertiary &&
            onTertiary == other.onTertiary &&
            tertiaryContainer == other.tertiaryContainer &&
            onTertiaryContainer == other.onTertiaryContainer &&
            background == other.background &&
            onBackground == other.onBackground &&
            surface == other.surface &&
            onSurface == other.onSurface &&
            surfaceVariant == other.surfaceVariant &&
            onSurfaceVariant == other.onSurfaceVariant &&
            surfaceTint == other.surfaceTint &&
            error == other.error &&
            onError == other.onError &&
            errorContainer == other.errorContainer &&
            onErrorContainer == other.onErrorContainer &&
            outline == other.outline &&
            outlineVariant == other.outlineVariant &&
            scrim == other.scrim &&
            inverseSurface == other.inverseSurface &&
            inverseOnSurface == other.inverseOnSurface &&
            inversePrimary == other.inversePrimary &&
            isLight == other.isLight
    }

    override fun hashCode(): Int {
        var result = primary.hashCode()
        result = 31 * result + onPrimary.hashCode()
        result = 31 * result + primaryContainer.hashCode()
        result = 31 * result + onPrimaryContainer.hashCode()
        result = 31 * result + secondary.hashCode()
        result = 31 * result + onSecondary.hashCode()
        result = 31 * result + secondaryContainer.hashCode()
        result = 31 * result + onSecondaryContainer.hashCode()
        result = 31 * result + tertiary.hashCode()
        result = 31 * result + onTertiary.hashCode()
        result = 31 * result + tertiaryContainer.hashCode()
        result = 31 * result + onTertiaryContainer.hashCode()
        result = 31 * result + background.hashCode()
        result = 31 * result + onBackground.hashCode()
        result = 31 * result + surface.hashCode()
        result = 31 * result + onSurface.hashCode()
        result = 31 * result + surfaceVariant.hashCode()
        result = 31 * result + onSurfaceVariant.hashCode()
        result = 31 * result + surfaceTint.hashCode()
        result = 31 * result + error.hashCode()
        result = 31 * result + onError.hashCode()
        result = 31 * result + errorContainer.hashCode()
        result = 31 * result + onErrorContainer.hashCode()
        result = 31 * result + outline.hashCode()
        result = 31 * result + outlineVariant.hashCode()
        result = 31 * result + scrim.hashCode()
        result = 31 * result + inverseSurface.hashCode()
        result = 31 * result + inverseOnSurface.hashCode()
        result = 31 * result + inversePrimary.hashCode()
        result = 31 * result + isLight.hashCode()
        return result
    }
}

/**
 * 浅色主题颜色
 */
fun lightColors(
    primary: Color = Color(0xFF6750A4),
    onPrimary: Color = Color(0xFFFFFFFF),
    primaryContainer: Color = Color(0xFFEADDFF),
    onPrimaryContainer: Color = Color(0xFF21005D),

    secondary: Color = Color(0xFF625B71),
    onSecondary: Color = Color(0xFFFFFFFF),
    secondaryContainer: Color = Color(0xFFE8DEF8),
    onSecondaryContainer: Color = Color(0xFF1D192B),

    tertiary: Color = Color(0xFF7D5260),
    onTertiary: Color = Color(0xFFFFFFFF),
    tertiaryContainer: Color = Color(0xFFFFD8E4),
    onTertiaryContainer: Color = Color(0xFF31111D),

    background: Color = Color(0xFFFFFBFE),
    onBackground: Color = Color(0xFF1C1B1F),

    surface: Color = Color(0xFFFFFBFE),
    onSurface: Color = Color(0xFF1C1B1F),
    surfaceVariant: Color = Color(0xFFE7E0EC),
    onSurfaceVariant: Color = Color(0xFF49454F),

    surfaceTint: Color = primary,

    error: Color = Color(0xFFB3261E),
    onError: Color = Color(0xFFFFFFFF),
    errorContainer: Color = Color(0xFFF9DEDC),
    onErrorContainer: Color = Color(0xFF410E0B),

    outline: Color = Color(0xFF79747E),
    outlineVariant: Color = Color(0xFFCAC4D0),

    scrim: Color = Color(0xFF000000),

    inverseSurface: Color = Color(0xFF313033),
    inverseOnSurface: Color = Color(0xFFF4EFF4),
    inversePrimary: Color = Color(0xFFD0BCFF)
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
 * 深色主题颜色
 */
fun darkColors(
    primary: Color = Color(0xFFD0BCFF),
    onPrimary: Color = Color(0xFF381E72),
    primaryContainer: Color = Color(0xFF4F378B),
    onPrimaryContainer: Color = Color(0xFFEADDFF),

    secondary: Color = Color(0xFFCCC2DC),
    onSecondary: Color = Color(0xFF332D41),
    secondaryContainer: Color = Color(0xFF4A4458),
    onSecondaryContainer: Color = Color(0xFFE8DEF8),

    tertiary: Color = Color(0xFFEFB8C8),
    onTertiary: Color = Color(0xFF492532),
    tertiaryContainer: Color = Color(0xFF633B48),
    onTertiaryContainer: Color = Color(0xFFFFD8E4),

    background: Color = Color(0xFF1C1B1F),
    onBackground: Color = Color(0xFFE6E1E5),

    surface: Color = Color(0xFF1C1B1F),
    onSurface: Color = Color(0xFFE6E1E5),
    surfaceVariant: Color = Color(0xFF49454F),
    onSurfaceVariant: Color = Color(0xFFCAC4D0),

    surfaceTint: Color = primary,

    error: Color = Color(0xFFF2B8B5),
    onError: Color = Color(0xFF601410),
    errorContainer: Color = Color(0xFF8C1D18),
    onErrorContainer: Color = Color(0xFFF9DEDC),

    outline: Color = Color(0xFF938F99),
    outlineVariant: Color = Color(0xFF49454F),

    scrim: Color = Color(0xFF000000),

    inverseSurface: Color = Color(0xFFE6E1E5),
    inverseOnSurface: Color = Color(0xFF313033),
    inversePrimary: Color = Color(0xFF6750A4)
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
