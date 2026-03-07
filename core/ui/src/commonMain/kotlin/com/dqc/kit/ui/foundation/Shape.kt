package com.dqc.kit.ui.foundation

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 应用形状系统
 *
 * 定义统一的圆角风格，确保组件视觉一致性。
 * 遵循 Material 3 形状分类：
 * - Small: 按钮、输入框、小卡片
 * - Medium: 卡片、对话框、底部Sheet
 * - Large: 大型卡片、展开面板
 * - ExtraLarge: 全屏对话框、大型容器
 *
 * @property none 无圆角（矩形）
 * @property extraSmall 超小圆角 (2dp)
 * @property small 小圆角 (4dp)
 * @property medium 中圆角 (8dp)
 * @property large 大圆角 (12dp)
 * @property extraLarge 超大圆角 (16dp)
 * @property full 全圆角 (50%)
 */
class AppShapes(
    val none: CornerBasedShape = RoundedCornerShape(0.dp),
    val extraSmall: CornerBasedShape = RoundedCornerShape(2.dp),
    val small: CornerBasedShape = RoundedCornerShape(4.dp),
    val medium: CornerBasedShape = RoundedCornerShape(8.dp),
    val large: CornerBasedShape = RoundedCornerShape(12.dp),
    val extraLarge: CornerBasedShape = RoundedCornerShape(16.dp),
    val full: CornerBasedShape = RoundedCornerShape(percent = 50)
) {
    /**
     * 转换为 Material 3 Shapes
     */
    fun toMaterialShapes(): Shapes = Shapes(
        small = small,
        medium = medium,
        large = extraLarge
    )

    /**
     * 按钮默认形状
     */
    val button: CornerBasedShape get() = medium

    /**
     * 卡片默认形状
     */
    val card: CornerBasedShape get() = large

    /**
     * 输入框默认形状
     */
    val textField: CornerBasedShape get() = small

    /**
     * 对话框默认形状
     */
    val dialog: CornerBasedShape get() = extraLarge

    /**
     * 底部 Sheet 默认形状
     */
    val bottomSheet: CornerBasedShape get() = large

    /**
     * 创建自定义圆角形状
     */
    fun rounded(corner: androidx.compose.ui.unit.Dp): CornerBasedShape =
        RoundedCornerShape(corner)

    /**
     * 创建不对称圆角形状
     */
    fun rounded(
        topStart: androidx.compose.ui.unit.Dp = 0.dp,
        topEnd: androidx.compose.ui.unit.Dp = 0.dp,
        bottomEnd: androidx.compose.ui.unit.Dp = 0.dp,
        bottomStart: androidx.compose.ui.unit.Dp = 0.dp
    ): CornerBasedShape = RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart
    )

    companion object {
        /**
         * 默认形状实例
         */
        val Default = AppShapes()

        /**
         * 锐利风格（小圆角）
         */
        val Sharp = AppShapes(
            extraSmall = RoundedCornerShape(0.dp),
            small = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(8.dp),
            extraLarge = RoundedCornerShape(12.dp)
        )

        /**
         * 柔和风格（大圆角）
         */
        val Soft = AppShapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
            extraLarge = RoundedCornerShape(24.dp)
        )
    }
}
