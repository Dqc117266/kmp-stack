package com.dqc.kit.ui.foundation

import androidx.compose.ui.unit.dp

/**
 * 应用间距系统
 *
 * 使用 4dp 基准网格系统，确保视觉一致性。
 * 命名采用 T-Shirt Size 方式，直观易懂。
 *
 * @property none 0dp - 无间距
 * @property xxxs 2dp - 极微间距
 * @property xxs 4dp - 超微间距（网格基准）
 * @property xs 8dp - 微间距
 * @property s 12dp - 小间距
 * @property m 16dp - 中间距（默认）
 * @property l 24dp - 大间距
 * @property xl 32dp - 超大间距
 * @property xxl 48dp - 超超大间距
 * @property xxxl 64dp - 极超大间距
 * @property xxxxl 96dp - 最大间距
 */
class AppSpacing(
    val none: androidx.compose.ui.unit.Dp = 0.dp,
    val xxxs: androidx.compose.ui.unit.Dp = 2.dp,
    val xxs: androidx.compose.ui.unit.Dp = 4.dp,
    val xs: androidx.compose.ui.unit.Dp = 8.dp,
    val s: androidx.compose.ui.unit.Dp = 12.dp,
    val m: androidx.compose.ui.unit.Dp = 16.dp,
    val l: androidx.compose.ui.unit.Dp = 24.dp,
    val xl: androidx.compose.ui.unit.Dp = 32.dp,
    val xxl: androidx.compose.ui.unit.Dp = 48.dp,
    val xxxl: androidx.compose.ui.unit.Dp = 64.dp,
    val xxxxl: androidx.compose.ui.unit.Dp = 96.dp
) {
    /**
     * 常用内边距值
     */
    val paddingSmall: androidx.compose.ui.unit.Dp get() = xs
    val paddingMedium: androidx.compose.ui.unit.Dp get() = m
    val paddingLarge: androidx.compose.ui.unit.Dp get() = l

    /**
     * 常用组件间距
     */
    val componentSpacingSmall: androidx.compose.ui.unit.Dp get() = xs
    val componentSpacingMedium: androidx.compose.ui.unit.Dp get() = s
    val componentSpacingLarge: androidx.compose.ui.unit.Dp get() = m

    companion object {
        /**
         * 默认间距实例
         */
        val Default = AppSpacing()
    }
}

/**
 * 紧凑间距（用于信息密度高的界面）
 */
fun compactSpacing(): AppSpacing = AppSpacing(
    none = 0.dp,
    xxxs = 2.dp,
    xxs = 4.dp,
    xs = 6.dp,
    s = 8.dp,
    m = 12.dp,
    l = 16.dp,
    xl = 24.dp,
    xxl = 32.dp,
    xxxl = 48.dp,
    xxxxl = 64.dp
)

/**
 * 宽松间距（用于大屏设备或强调呼吸感的界面）
 */
fun spaciousSpacing(): AppSpacing = AppSpacing(
    none = 0.dp,
    xxxs = 4.dp,
    xxs = 8.dp,
    xs = 12.dp,
    s = 16.dp,
    m = 24.dp,
    l = 32.dp,
    xl = 48.dp,
    xxl = 64.dp,
    xxxl = 96.dp,
    xxxxl = 128.dp
)
