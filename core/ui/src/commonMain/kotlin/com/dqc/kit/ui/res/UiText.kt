package com.dqc.kit.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * UI 文本包装类
 *
 * 解决字符串资源在普通代码中无法访问的问题。
 * 支持静态字符串和动态参数。
 *
 * 使用场景：
 * 1. ViewModel 或业务逻辑中需要返回显示文本时
 * 2. 需要延迟解析字符串资源（等 Composable 上下文）
 * 3. 需要支持格式化参数的文本
 *
 * 示例：
 * ```kotlin
 * // 在 ViewModel 中
 * val errorMessage: UiText = UiText.StringResource(Res.string.error_network)
 *
 * // 在 Composable 中
 * Text(text = errorMessage.asString())
 * ```
 */
sealed class UiText {

    /**
     * 动态文本值
     */
    data class DynamicString(val value: String) : UiText()

    /**
     * 字符串资源
     */
    class StringResource(
        val res: StringResource,
        vararg val args: Any
    ) : UiText()

    /**
     * 带格式化参数的字符串资源
     */
    class StringResourceArgs(
        val res: StringResource,
        val args: List<Any>
    ) : UiText()

    /**
     * 空文本
     */
    object Empty : UiText()

    /**
     * 转换为字符串（仅在 Composable 上下文中调用）
     */
    @Composable
    @ReadOnlyComposable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(res, *args)
            is StringResourceArgs -> stringResource(res, *args.toTypedArray())
            is Empty -> ""
        }
    }

    /**
     * 获取字符串或空
     */
    @Composable
    @ReadOnlyComposable
    fun asStringOrNull(): String? {
        return when (this) {
            is DynamicString -> value.takeIf { it.isNotEmpty() }
            is StringResource -> stringResource(res, *args)
            is StringResourceArgs -> stringResource(res, *args.toTypedArray())
            is Empty -> null
        }
    }

    /**
     * 如果为空则返回默认值
     */
    @Composable
    @ReadOnlyComposable
    fun asStringOrDefault(default: String): String {
        return asStringOrNull() ?: default
    }

    companion object {
        /**
         * 创建空文本
         */
        fun empty(): UiText = Empty

        /**
         * 创建动态文本
         */
        fun of(value: String): UiText = DynamicString(value)

        /**
         * 创建字符串资源
         */
        fun of(res: StringResource, vararg args: Any): UiText =
            StringResource(res, *args)

        /**
         * 创建带格式化参数的字符串资源
         */
        fun of(res: StringResource, args: List<Any>): UiText =
            StringResourceArgs(res, args)

        /**
         * 创建可选文本（如果为 null 则返回 Empty）
         */
        fun ofNullable(value: String?): UiText =
            value?.let { DynamicString(it) } ?: Empty

        /**
         * 连接多个 UiText
         */
        fun join(vararg texts: UiText, separator: String = ""): UiText {
            return DynamicString(
                texts.joinToString(separator) { text ->
                    when (text) {
                        is DynamicString -> text.value
                        else -> ""
                    }
                }
            )
        }
    }
}

/**
 * 字符串转换为 UiText
 */
fun String.toUiText(): UiText = UiText.of(this)

/**
 * 字符串资源转换为 UiText
 */
fun StringResource.toUiText(vararg args: Any): UiText = UiText.of(this, *args)

/**
 * UiText 集合连接为单个 UiText
 */
fun List<UiText>.joinToUiText(separator: String = ""): UiText {
    return UiText.DynamicString(
        joinToString(separator) { text ->
            when (text) {
                is UiText.DynamicString -> text.value
                else -> ""
            }
        }
    )
}

/**
 * UiText 条件显示
 */
fun UiText?.orEmpty(): UiText = this ?: UiText.Empty
