package com.dqc.kit.ui.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dqc.kit.ui.theme.AppTheme
import org.jetbrains.compose.resources.painterResource

/**
 * 文本输入框变体
 */
enum class AppTextFieldVariant {
    /** 填充样式（默认背景） */
    FILLED,

    /** 轮廓样式（带边框） */
    OUTLINED
}

/**
 * 文本输入框尺寸
 */
enum class AppTextFieldSize {
    /** 小尺寸 */
    SMALL,

    /** 中等尺寸（默认） */
    MEDIUM,

    /** 大尺寸 */
    LARGE
}

/**
 * 应用文本输入框组件
 *
 * 特性：
 * - 支持多种变体（Filled, Outlined）
 * - 支持错误状态显示
 * - 支持前置/后置图标
 * - 支持清除按钮
 * - 完全无状态，所有状态通过参数传入
 *
 * @param value 输入值
 * @param onValueChange 值变化回调
 * @param modifier 修饰符
 * @param variant 输入框变体
 * @param size 输入框尺寸
 * @param label 标签文本（可选）
 * @param placeholder 占位文本（可选）
 * @param helperText 辅助文本（可选）
 * @param errorMessage 错误消息（可选，不为空时显示错误状态）
 * @param leadingIcon 前置图标（可选）
 * @param trailingIcon 后置图标（可选）
 * @param isPassword 是否为密码输入
 * @param keyboardOptions 键盘选项
 * @param keyboardActions 键盘动作
 * @param singleLine 是否单行
 * @param maxLines 最大行数
 * @param enabled 是否可用
 * @param readOnly 是否只读
 * @param showClearButton 是否显示清除按钮（默认 true）
 * @param shape 自定义形状（可选）
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    variant: AppTextFieldVariant = AppTextFieldVariant.OUTLINED,
    size: AppTextFieldSize = AppTextFieldSize.MEDIUM,
    label: String? = null,
    placeholder: String? = null,
    helperText: String? = null,
    errorMessage: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    showClearButton: Boolean = true,
    shape: Shape? = null
) {
    val isError = errorMessage != null
    val fieldShape = shape ?: when (variant) {
        AppTextFieldVariant.FILLED -> AppTheme.shapes.textField
        AppTextFieldVariant.OUTLINED -> AppTheme.shapes.textField
    }

    // 密码可见性状态
    var passwordVisible by remember { mutableStateOf(false) }

    // 视觉转换（密码隐藏或显示）
    val visualTransformation = if (isPassword && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    // 构建前置图标
    val leadingIconComposable: @Composable (() -> Unit)? = leadingIcon?.let {
        @Composable {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.size(getTextFieldIconSize(size)),
                tint = if (isError) {
                    AppTheme.colors.error
                } else {
                    AppTheme.colors.onSurfaceVariant
                }
            )
        }
    }

    // 构建后置图标
    val trailingIconComposable: @Composable (() -> Unit)? = when {
        isPassword -> {
            @Composable {
                PasswordVisibilityToggle(
                    visible = passwordVisible,
                    onToggle = { passwordVisible = !passwordVisible },
                    size = size
                )
            }
        }

        showClearButton && value.isNotEmpty() -> {
            @Composable {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xxs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 清除按钮
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(getTextFieldIconSize(size) + 8.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Clear,
                            contentDescription = "Clear",
                            modifier = Modifier.size(getTextFieldIconSize(size)),
                            tint = AppTheme.colors.onSurfaceVariant
                        )
                    }

                    // 自定义后置图标
                    trailingIcon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(getTextFieldIconSize(size)),
                            tint = if (isError) {
                                AppTheme.colors.error
                            } else {
                                AppTheme.colors.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        trailingIcon != null -> {
            @Composable {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(getTextFieldIconSize(size)),
                    tint = if (isError) {
                        AppTheme.colors.error
                    } else {
                        AppTheme.colors.onSurfaceVariant
                    }
                )
            }
        }

        else -> null
    }

    // 文本样式
    val textStyle = when (size) {
        AppTextFieldSize.SMALL -> AppTheme.typography.bodyMedium
        AppTextFieldSize.MEDIUM -> AppTheme.typography.bodyLarge
        AppTextFieldSize.LARGE -> AppTheme.typography.titleMedium
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        when (variant) {
            AppTextFieldVariant.FILLED -> {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = textStyle,
                    label = label?.let { { Text(it) } },
                    placeholder = placeholder?.let { { Text(it) } },
                    leadingIcon = leadingIconComposable,
                    trailingIcon = trailingIconComposable,
                    isError = isError,
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    shape = fieldShape,
                    colors = getFilledTextFieldColors()
                )
            }

            AppTextFieldVariant.OUTLINED -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = enabled,
                    readOnly = readOnly,
                    textStyle = textStyle,
                    label = label?.let { { Text(it) } },
                    placeholder = placeholder?.let { { Text(it) } },
                    leadingIcon = leadingIconComposable,
                    trailingIcon = trailingIconComposable,
                    isError = isError,
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    shape = fieldShape,
                    colors = getOutlinedTextFieldColors()
                )
            }
        }

        // 辅助文本或错误消息
        AnimatedVisibility(
            visible = helperText != null || errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = errorMessage ?: helperText ?: "",
                style = AppTheme.typography.bodySmall,
                color = if (errorMessage != null) {
                    AppTheme.colors.error
                } else {
                    AppTheme.colors.onSurfaceVariant
                },
                modifier = Modifier.padding(
                    start = if (variant == AppTextFieldVariant.OUTLINED) 16.dp else 0.dp,
                    top = 4.dp
                )
            )
        }
    }
}

/**
 * 密码可见性切换按钮
 */
@Composable
private fun PasswordVisibilityToggle(
    visible: Boolean,
    onToggle: () -> Unit,
    size: AppTextFieldSize
) {
    IconButton(
        onClick = onToggle,
        modifier = Modifier.size(getTextFieldIconSize(size) + 8.dp)
    ) {
        Icon(
            imageVector = if (visible) {
                androidx.compose.material.icons.Icons.Default.Visibility
            } else {
                androidx.compose.material.icons.Icons.Default.VisibilityOff
            },
            contentDescription = if (visible) "Hide password" else "Show password",
            modifier = Modifier.size(getTextFieldIconSize(size)),
            tint = AppTheme.colors.onSurfaceVariant
        )
    }
}

/**
 * 获取 Filled 文本框颜色
 */
@Composable
private fun getFilledTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedTextColor = AppTheme.colors.onSurface,
        unfocusedTextColor = AppTheme.colors.onSurface,
        disabledTextColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorTextColor = AppTheme.colors.error,

        focusedContainerColor = AppTheme.colors.surfaceVariant,
        unfocusedContainerColor = AppTheme.colors.surfaceVariant,
        disabledContainerColor = AppTheme.colors.onSurface.copy(alpha = 0.04f),
        errorContainerColor = AppTheme.colors.errorContainer,

        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,

        focusedLabelColor = AppTheme.colors.primary,
        unfocusedLabelColor = AppTheme.colors.onSurfaceVariant,
        disabledLabelColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorLabelColor = AppTheme.colors.error,

        focusedPlaceholderColor = AppTheme.colors.onSurfaceVariant,
        unfocusedPlaceholderColor = AppTheme.colors.onSurfaceVariant,
        disabledPlaceholderColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorPlaceholderColor = AppTheme.colors.error,

        focusedLeadingIconColor = AppTheme.colors.onSurfaceVariant,
        unfocusedLeadingIconColor = AppTheme.colors.onSurfaceVariant,
        disabledLeadingIconColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorLeadingIconColor = AppTheme.colors.error,

        focusedTrailingIconColor = AppTheme.colors.onSurfaceVariant,
        unfocusedTrailingIconColor = AppTheme.colors.onSurfaceVariant,
        disabledTrailingIconColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorTrailingIconColor = AppTheme.colors.error
    )
}

/**
 * 获取 Outlined 文本框颜色
 */
@Composable
private fun getOutlinedTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = AppTheme.colors.onSurface,
        unfocusedTextColor = AppTheme.colors.onSurface,
        disabledTextColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorTextColor = AppTheme.colors.error,

        focusedBorderColor = AppTheme.colors.primary,
        unfocusedBorderColor = AppTheme.colors.outline,
        disabledBorderColor = AppTheme.colors.onSurface.copy(alpha = 0.12f),
        errorBorderColor = AppTheme.colors.error,

        focusedLabelColor = AppTheme.colors.primary,
        unfocusedLabelColor = AppTheme.colors.onSurfaceVariant,
        disabledLabelColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorLabelColor = AppTheme.colors.error,

        focusedPlaceholderColor = AppTheme.colors.onSurfaceVariant,
        unfocusedPlaceholderColor = AppTheme.colors.onSurfaceVariant,
        disabledPlaceholderColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorPlaceholderColor = AppTheme.colors.error,

        focusedLeadingIconColor = AppTheme.colors.onSurfaceVariant,
        unfocusedLeadingIconColor = AppTheme.colors.onSurfaceVariant,
        disabledLeadingIconColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorLeadingIconColor = AppTheme.colors.error,

        focusedTrailingIconColor = AppTheme.colors.onSurfaceVariant,
        unfocusedTrailingIconColor = AppTheme.colors.onSurfaceVariant,
        disabledTrailingIconColor = AppTheme.colors.onSurface.copy(alpha = 0.38f),
        errorTrailingIconColor = AppTheme.colors.error
    )
}

/**
 * 获取图标尺寸
 */
private fun getTextFieldIconSize(size: AppTextFieldSize): androidx.compose.ui.unit.Dp {
    return when (size) {
        AppTextFieldSize.SMALL -> 20.dp
        AppTextFieldSize.MEDIUM -> 24.dp
        AppTextFieldSize.LARGE -> 28.dp
    }
}

/**
 * 搜索输入框便捷组件
 */
@Composable
fun AppSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onSearch: () -> Unit = {},
    enabled: Boolean = true
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = AppTextFieldVariant.OUTLINED,
        size = AppTextFieldSize.MEDIUM,
        placeholder = placeholder,
        leadingIcon = androidx.compose.material.icons.Icons.Default.Search,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        enabled = enabled
    )
}

/**
 * 邮箱输入框便捷组件
 */
@Composable
fun AppEmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Email",
    errorMessage: String? = null,
    enabled: Boolean = true
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = AppTextFieldVariant.OUTLINED,
        label = label,
        errorMessage = errorMessage,
        leadingIcon = androidx.compose.material.icons.Icons.Default.Email,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        enabled = enabled
    )
}

/**
 * 密码输入框便捷组件
 */
@Composable
fun AppPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    errorMessage: String? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        variant = AppTextFieldVariant.OUTLINED,
        label = label,
        errorMessage = errorMessage,
        leadingIcon = androidx.compose.material.icons.Icons.Default.Lock,
        isPassword = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        enabled = enabled
    )
}
