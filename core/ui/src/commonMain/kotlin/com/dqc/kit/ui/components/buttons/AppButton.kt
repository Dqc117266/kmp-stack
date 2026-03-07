package com.dqc.kit.ui.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dqc.kit.ui.theme.AppTheme

/**
 * 按钮变体类型
 */
enum class AppButtonVariant {
    /** 主要按钮 - 用于主要操作 */
    PRIMARY,

    /** 次要按钮 - 用于次要操作 */
    SECONDARY,

    /** 轮廓按钮 - 用于辅助操作 */
    OUTLINE,

    /** 文字按钮 - 用于低优先级操作 */
    TEXT,

    /** 危险按钮 - 用于删除等危险操作 */
    DANGER
}

/**
 * 按钮尺寸
 */
enum class AppButtonSize {
    /** 小尺寸 */
    SMALL,

    /** 中等尺寸（默认） */
    MEDIUM,

    /** 大尺寸 */
    LARGE
}

/**
 * 应用按钮组件
 *
 * 特性：
 * - 支持多种变体（Primary, Secondary, Outline, Text, Danger）
 * - 支持加载状态
 * - 支持前置/后置图标
 * - 完全无状态，所有状态通过参数传入
 *
 * @param text 按钮文本
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param variant 按钮变体
 * @param size 按钮尺寸
 * @param enabled 是否可用
 * @param loading 是否处于加载状态
 * @param leadingIcon 前置图标（可选）
 * @param trailingIcon 后置图标（可选）
 * @param shape 自定义形状（可选）
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.PRIMARY,
    size: AppButtonSize = AppButtonSize.MEDIUM,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    shape: Shape? = null
) {
    val colors = getButtonColors(variant)
    val contentPadding = getButtonContentPadding(size)
    val minHeight = getButtonMinHeight(size)
    val buttonShape = shape ?: AppTheme.shapes.button

    val isEnabled = enabled && !loading

    val buttonContent: @Composable RowScope.() -> Unit = {
        AnimatedContent(
            targetState = loading,
            transitionSpec = {
                fadeIn() + slideInVertically { it / 2 } with
                    fadeOut() + slideOutVertically { it / 2 } using
                    SizeTransform(clip = false)
            }
        ) { isLoading ->
            if (isLoading) {
                LoadingContent(size)
            } else {
                ButtonContent(
                    text = text,
                    size = size,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            }
        }
    }

    when (variant) {
        AppButtonVariant.PRIMARY, AppButtonVariant.DANGER -> {
            Button(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }

        AppButtonVariant.SECONDARY -> {
            Button(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }

        AppButtonVariant.OUTLINE -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }

        AppButtonVariant.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }
    }
}

/**
 * 应用按钮组件（带 Painter 图标版本）
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.PRIMARY,
    size: AppButtonSize = AppButtonSize.MEDIUM,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    shape: Shape? = null
) {
    val colors = getButtonColors(variant)
    val contentPadding = getButtonContentPadding(size)
    val minHeight = getButtonMinHeight(size)
    val buttonShape = shape ?: AppTheme.shapes.button

    val isEnabled = enabled && !loading

    val buttonContent: @Composable RowScope.() -> Unit = {
        AnimatedContent(
            targetState = loading,
            transitionSpec = {
                fadeIn() + slideInVertically { it / 2 } with
                    fadeOut() + slideOutVertically { it / 2 } using
                    SizeTransform(clip = false)
            }
        ) { isLoading ->
            if (isLoading) {
                LoadingContent(size)
            } else {
                ButtonContent(
                    text = text,
                    size = size,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            }
        }
    }

    when (variant) {
        AppButtonVariant.PRIMARY, AppButtonVariant.DANGER -> {
            Button(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }

        AppButtonVariant.SECONDARY -> {
            Button(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }

        AppButtonVariant.OUTLINE -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }

        AppButtonVariant.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.defaultMinSize(minHeight = minHeight),
                enabled = isEnabled,
                shape = buttonShape,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }
    }
}

/**
 * 图标按钮组件
 *
 * @param icon 图标
 * @param contentDescription 内容描述（无障碍）
 * @param onClick 点击回调
 * @param modifier 修饰符
 * @param variant 按钮变体
 * @param size 按钮尺寸
 * @param enabled 是否可用
 * @param loading 是否处于加载状态
 */
@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.PRIMARY,
    size: AppButtonSize = AppButtonSize.MEDIUM,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val iconSize = getIconSize(size)
    val buttonSize = getIconButtonSize(size)
    val colors = getButtonColors(variant)
    val isEnabled = enabled && !loading

    val content: @Composable () -> Unit = {
        Box(
            modifier = Modifier.size(buttonSize),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(iconSize),
                    strokeWidth = 2.dp,
                    color = if (variant == AppButtonVariant.PRIMARY || variant == AppButtonVariant.DANGER) {
                        AppTheme.colors.onPrimary
                    } else {
                        AppTheme.colors.primary
                    }
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }

    when (variant) {
        AppButtonVariant.PRIMARY, AppButtonVariant.DANGER, AppButtonVariant.SECONDARY -> {
            Button(
                onClick = onClick,
                modifier = modifier.size(buttonSize),
                enabled = isEnabled,
                shape = AppTheme.shapes.full,
                colors = colors,
                contentPadding = PaddingValues(0.dp),
                content = { content() }
            )
        }

        AppButtonVariant.OUTLINE -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier.size(buttonSize),
                enabled = isEnabled,
                shape = AppTheme.shapes.full,
                colors = colors,
                contentPadding = PaddingValues(0.dp),
                content = { content() }
            )
        }

        AppButtonVariant.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = modifier.size(buttonSize),
                enabled = isEnabled,
                shape = AppTheme.shapes.full,
                colors = colors,
                contentPadding = PaddingValues(0.dp),
                content = { content() }
            )
        }
    }
}

/**
 * 按钮内容区域
 */
@Composable
private fun RowScope.ButtonContent(
    text: String,
    size: AppButtonSize,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    val iconSize = getIconSize(size)
    val textStyle = when (size) {
        AppButtonSize.SMALL -> AppTheme.typography.labelMedium
        AppButtonSize.MEDIUM -> AppTheme.typography.labelLarge
        AppButtonSize.LARGE -> AppTheme.typography.titleSmall
    }

    leadingIcon?.let { icon ->
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
    }

    Text(
        text = text,
        style = textStyle
    )

    trailingIcon?.let { icon ->
        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * 按钮内容区域（Painter 图标版本）
 */
@Composable
private fun RowScope.ButtonContent(
    text: String,
    size: AppButtonSize,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null
) {
    val iconSize = getIconSize(size)
    val textStyle = when (size) {
        AppButtonSize.SMALL -> AppTheme.typography.labelMedium
        AppButtonSize.MEDIUM -> AppTheme.typography.labelLarge
        AppButtonSize.LARGE -> AppTheme.typography.titleSmall
    }

    leadingIcon?.let { icon ->
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
    }

    Text(
        text = text,
        style = textStyle
    )

    trailingIcon?.let { icon ->
        Spacer(modifier = Modifier.width(AppTheme.spacing.xs))
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * 加载状态内容
 */
@Composable
private fun RowScope.LoadingContent(size: AppButtonSize) {
    val indicatorSize = getIconSize(size)
    val strokeWidth = when (size) {
        AppButtonSize.SMALL -> 2.dp
        AppButtonSize.MEDIUM -> 2.dp
        AppButtonSize.LARGE -> 3.dp
    }

    CircularProgressIndicator(
        modifier = Modifier.size(indicatorSize),
        strokeWidth = strokeWidth
    )
}

/**
 * 获取按钮颜色
 */
@Composable
private fun getButtonColors(variant: AppButtonVariant): ButtonColors {
    return when (variant) {
        AppButtonVariant.PRIMARY -> ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.primary,
            contentColor = AppTheme.colors.onPrimary,
            disabledContainerColor = AppTheme.colors.onSurface.copy(alpha = 0.12f),
            disabledContentColor = AppTheme.colors.onSurface.copy(alpha = 0.38f)
        )

        AppButtonVariant.SECONDARY -> ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.secondaryContainer,
            contentColor = AppTheme.colors.onSecondaryContainer,
            disabledContainerColor = AppTheme.colors.onSurface.copy(alpha = 0.12f),
            disabledContentColor = AppTheme.colors.onSurface.copy(alpha = 0.38f)
        )

        AppButtonVariant.OUTLINE -> ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = AppTheme.colors.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = AppTheme.colors.onSurface.copy(alpha = 0.38f)
        )

        AppButtonVariant.TEXT -> ButtonDefaults.textButtonColors(
            containerColor = Color.Transparent,
            contentColor = AppTheme.colors.primary,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = AppTheme.colors.onSurface.copy(alpha = 0.38f)
        )

        AppButtonVariant.DANGER -> ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.error,
            contentColor = AppTheme.colors.onError,
            disabledContainerColor = AppTheme.colors.onSurface.copy(alpha = 0.12f),
            disabledContentColor = AppTheme.colors.onSurface.copy(alpha = 0.38f)
        )
    }
}

/**
 * 获取按钮内容内边距
 */
private fun getButtonContentPadding(size: AppButtonSize): PaddingValues {
    return when (size) {
        AppButtonSize.SMALL -> PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )

        AppButtonSize.MEDIUM -> PaddingValues(
            horizontal = 24.dp,
            vertical = 12.dp
        )

        AppButtonSize.LARGE -> PaddingValues(
            horizontal = 32.dp,
            vertical = 16.dp
        )
    }
}

/**
 * 获取按钮最小高度
 */
private fun getButtonMinHeight(size: AppButtonSize): Dp {
    return when (size) {
        AppButtonSize.SMALL -> 36.dp
        AppButtonSize.MEDIUM -> 48.dp
        AppButtonSize.LARGE -> 56.dp
    }
}

/**
 * 获取图标尺寸
 */
private fun getIconSize(size: AppButtonSize): Dp {
    return when (size) {
        AppButtonSize.SMALL -> 16.dp
        AppButtonSize.MEDIUM -> 20.dp
        AppButtonSize.LARGE -> 24.dp
    }
}

/**
 * 获取图标按钮尺寸
 */
private fun getIconButtonSize(size: AppButtonSize): Dp {
    return when (size) {
        AppButtonSize.SMALL -> 36.dp
        AppButtonSize.MEDIUM -> 48.dp
        AppButtonSize.LARGE -> 56.dp
    }
}
