package com.dqc.kit.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 窗口尺寸类别
 *
 * 根据 Material 3 窗口大小分类规范：
 * - COMPACT: 宽度 < 600dp（手机竖屏）
 * - MEDIUM: 宽度 600dp-840dp（手机横屏、平板竖屏）
 * - EXPANDED: 宽度 > 840dp（平板横屏、桌面）
 */
enum class WindowSizeClass {
    /**
     * 紧凑 - 手机竖屏
     * 宽度 < 600dp
     */
    COMPACT,

    /**
     * 中等 - 手机横屏、平板竖屏
     * 宽度 600dp-840dp
     */
    MEDIUM,

    /**
     * 扩展 - 平板横屏、桌面
     * 宽度 > 840dp
     */
    EXPANDED;

    /**
     * 是否为紧凑模式（手机）
     */
    val isCompact: Boolean get() = this == COMPACT

    /**
     * 是否为中等模式（小平板）
     */
    val isMedium: Boolean get() = this == MEDIUM

    /**
     * 是否为扩展模式（大平板/桌面）
     */
    val isExpanded: Boolean get() = this == EXPANDED

    /**
     * 是否至少为中等尺寸
     */
    val isAtLeastMedium: Boolean get() = this == MEDIUM || this == EXPANDED

    /**
     * 是否至少为扩展尺寸
     */
    val isAtLeastExpanded: Boolean get() = this == EXPANDED

    companion object {
        /**
         * 根据宽度计算窗口尺寸类别
         */
        fun fromWidth(widthDp: Dp): WindowSizeClass {
            return when {
                widthDp < 600.dp -> COMPACT
                widthDp < 840.dp -> MEDIUM
                else -> EXPANDED
            }
        }

        /**
         * 根据高度计算窗口尺寸类别
         */
        fun fromHeight(heightDp: Dp): WindowSizeClass {
            return when {
                heightDp < 480.dp -> COMPACT
                heightDp < 900.dp -> MEDIUM
                else -> EXPANDED
            }
        }
    }
}

/**
 * 窗口尺寸信息
 *
 * @property widthSizeClass 宽度尺寸类别
 * @property heightSizeClass 高度尺寸类别
 * @property widthDp 宽度（dp）
 * @property heightDp 高度（dp）
 */
data class WindowSizeInfo(
    val widthSizeClass: WindowSizeClass,
    val heightSizeClass: WindowSizeClass,
    val widthDp: Dp,
    val heightDp: Dp
) {
    /**
     * 是否为横屏模式
     */
    val isLandscape: Boolean get() = widthDp > heightDp

    /**
     * 是否为竖屏模式
     */
    val isPortrait: Boolean get() = heightDp > widthDp

    /**
     * 是否为平板尺寸（至少中等宽度）
     */
    val isTablet: Boolean get() = widthSizeClass.isAtLeastMedium

    /**
     * 是否为手机尺寸
     */
    val isPhone: Boolean get() = widthSizeClass.isCompact
}

/**
 * 记住当前窗口尺寸类别
 *
 * 在 Compose 中使用，自动监听窗口变化
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val windowInfo = LocalWindowInfo.current
    val containerSize = windowInfo.containerSize

    return remember(containerSize) {
        WindowSizeClass.fromWidth(containerSize.width.dp)
    }
}

/**
 * 记住当前窗口尺寸信息
 *
 * 在 Compose 中使用，包含宽度和高度信息
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun rememberWindowSizeInfo(): WindowSizeInfo {
    val windowInfo = LocalWindowInfo.current
    val containerSize = windowInfo.containerSize

    return remember(containerSize) {
        val widthDp = containerSize.width.dp
        val heightDp = containerSize.height.dp

        WindowSizeInfo(
            widthSizeClass = WindowSizeClass.fromWidth(widthDp),
            heightSizeClass = WindowSizeClass.fromHeight(heightDp),
            widthDp = widthDp,
            heightDp = heightDp
        )
    }
}

/**
 * 根据窗口尺寸条件执行不同逻辑
 *
 * @param compact 紧凑尺寸执行块
 * @param medium 中等尺寸执行块
 * @param expanded 扩展尺寸执行块
 */
@Composable
fun <T> rememberByWindowSize(
    compact: @Composable () -> T,
    medium: @Composable () -> T,
    expanded: @Composable () -> T
): T {
    val windowSizeClass = rememberWindowSizeClass()

    return when (windowSizeClass) {
        WindowSizeClass.COMPACT -> compact()
        WindowSizeClass.MEDIUM -> medium()
        WindowSizeClass.EXPANDED -> expanded()
    }
}

/**
 * 根据窗口尺寸条件选择值
 *
 * @param compact 紧凑尺寸值
 * @param medium 中等尺寸值
 * @param expanded 扩展尺寸值
 */
@Composable
fun <T> selectByWindowSize(
    compact: T,
    medium: T,
    expanded: T
): T {
    val windowSizeClass = rememberWindowSizeClass()

    return remember(windowSizeClass) {
        when (windowSizeClass) {
            WindowSizeClass.COMPACT -> compact
            WindowSizeClass.MEDIUM -> medium
            WindowSizeClass.EXPANDED -> expanded
        }
    }
}

/**
 * 响应式间距值
 *
 * 根据窗口尺寸返回不同的间距值
 */
@Composable
fun responsiveSpacing(
    compact: Dp,
    medium: Dp = compact,
    expanded: Dp = medium
): Dp = selectByWindowSize(compact, medium, expanded)

/**
 * 响应式尺寸值
 *
 * 根据窗口尺寸返回不同的尺寸值
 */
@Composable
fun <T> responsiveValue(
    compact: T,
    medium: T = compact,
    expanded: T = medium
): T = selectByWindowSize(compact, medium, expanded)
