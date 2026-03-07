package com.dqc.kit.ui.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dqc.kit.ui.theme.AppTheme
import com.dqc.kit.ui.util.WindowSizeClass
import com.dqc.kit.ui.util.rememberWindowSizeClass

/**
 * 导航项数据类
 *
 * @param route 路由标识
 * @param label 标签文本
 * @param icon 未选中图标
 * @param selectedIcon 选中图标（可选，默认使用 icon）
 * @param badgeCount 徽章数字（可选）
 * @param badgeVisible 是否显示徽章（可选）
 */
data class AppNavigationItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector? = null,
    val badgeCount: Int? = null,
    val badgeVisible: Boolean = false
)

/**
 * 自适应导航栏
 *
 * 根据屏幕宽度自动切换：
 * - 手机：底部导航栏
 * - 平板/桌面：侧边导航栏 (NavigationRail)
 *
 * @param items 导航项列表
 * @param selectedRoute 当前选中的路由
 * @param onItemSelected 导航项选中回调
 * @param modifier 修饰符
 * @param windowSizeClass 窗口尺寸类别（可选，默认自动检测）
 */
@Composable
fun AppNavigationBar(
    items: List<AppNavigationItem>,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = rememberWindowSizeClass()
) {
    when (windowSizeClass) {
        WindowSizeClass.COMPACT -> {
            // 手机：底部导航栏
            AppBottomNavigationBar(
                items = items,
                selectedRoute = selectedRoute,
                onItemSelected = onItemSelected,
                modifier = modifier
            )
        }

        WindowSizeClass.MEDIUM,
        WindowSizeClass.EXPANDED -> {
            // 平板/桌面：侧边导航栏
            AppNavigationRail(
                items = items,
                selectedRoute = selectedRoute,
                onItemSelected = onItemSelected,
                modifier = modifier
            )
        }
    }
}

/**
 * 底部导航栏（手机专用）
 *
 * @param items 导航项列表
 * @param selectedRoute 当前选中的路由
 * @param onItemSelected 导航项选中回调
 * @param modifier 修饰符
 * @param tonalElevation 海拔高度
 */
@Composable
fun AppBottomNavigationBar(
    items: List<AppNavigationItem>,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    tonalElevation: Dp = 3.dp
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = AppTheme.colors.surface,
        contentColor = AppTheme.colors.onSurface,
        tonalElevation = tonalElevation
    ) {
        items.forEach { item ->
            val selected = item.route == selectedRoute

            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item.route) },
                icon = {
                    NavigationIcon(
                        item = item,
                        selected = selected
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppTheme.colors.primary,
                    selectedTextColor = AppTheme.colors.primary,
                    unselectedIconColor = AppTheme.colors.onSurfaceVariant,
                    unselectedTextColor = AppTheme.colors.onSurfaceVariant,
                    indicatorColor = AppTheme.colors.primaryContainer
                )
            )
        }
    }
}

/**
 * 侧边导航栏（平板/桌面专用）
 *
 * @param items 导航项列表
 * @param selectedRoute 当前选中的路由
 * @param onItemSelected 导航项选中回调
 * @param modifier 修饰符
 * @param header 顶部内容（可选）
 * @param footer 底部内容（可选）
 */
@Composable
fun AppNavigationRail(
    items: List<AppNavigationItem>,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null
) {
    NavigationRail(
        modifier = modifier,
        containerColor = AppTheme.colors.surface,
        contentColor = AppTheme.colors.onSurface,
        header = header
    ) {
        items.forEach { item ->
            val selected = item.route == selectedRoute

            NavigationRailItem(
                selected = selected,
                onClick = { onItemSelected(item.route) },
                icon = {
                    NavigationIcon(
                        item = item,
                        selected = selected
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = AppTheme.colors.primary,
                    selectedTextColor = AppTheme.colors.primary,
                    unselectedIconColor = AppTheme.colors.onSurfaceVariant,
                    unselectedTextColor = AppTheme.colors.onSurfaceVariant,
                    indicatorColor = AppTheme.colors.primaryContainer
                )
            )
        }

        // 底部内容
        footer?.let {
            Divider(
                color = AppTheme.colors.outlineVariant,
                modifier = Modifier.padding(vertical = AppTheme.spacing.s)
            )
            it()
        }
    }
}

/**
 * 自定义顶部导航栏
 *
 * 用于页面内的选项卡切换
 *
 * @param items 选项卡项列表
 * @param selectedIndex 当前选中索引
 * @param onItemSelected 选项卡选中回调
 * @param modifier 修饰符
 */
@Composable
fun AppTopNavigationBar(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup()
            .padding(horizontal = AppTheme.spacing.m),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.m)
    ) {
        items.forEachIndexed { index, label ->
            val selected = index == selectedIndex

            AppTabItem(
                label = label,
                selected = selected,
                onClick = { onItemSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 选项卡项
 */
@Composable
private fun AppTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab
            )
            .padding(vertical = AppTheme.spacing.s),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = if (selected) {
                AppTheme.typography.labelLarge
            } else {
                AppTheme.typography.labelMedium
            },
            color = if (selected) {
                AppTheme.colors.primary
            } else {
                AppTheme.colors.onSurfaceVariant
            }
        )

        // 选中指示器
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = AppTheme.spacing.xs)
                    .size(width = 24.dp, height = 2.dp)
                    .padding(top = AppTheme.spacing.xxs),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    color = AppTheme.colors.primary,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 导航图标（带徽章支持）
 */
@Composable
private fun NavigationIcon(
    item: AppNavigationItem,
    selected: Boolean
) {
    val icon = if (selected && item.selectedIcon != null) {
        item.selectedIcon
    } else {
        item.icon
    }

    if (item.badgeVisible || item.badgeCount != null) {
        BadgedBox(
            badge = {
                if (item.badgeCount != null) {
                    Badge {
                        Text(
                            text = item.badgeCount.toString(),
                            style = AppTheme.typography.labelSmall
                        )
                    }
                } else if (item.badgeVisible) {
                    Badge()
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = item.label
            )
        }
    } else {
        Icon(
            imageVector = icon,
            contentDescription = item.label
        )
    }
}

/**
 * 应用顶部应用栏
 *
 * 统一的顶部导航栏样式
 *
 * @param title 标题
 * @param modifier 修饰符
 * @param navigationIcon 导航图标（可选）
 * @param actions 操作按钮（可选）
 * @param onNavigationClick 导航图标点击回调（可选）
 */
@Composable
fun AppTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigationClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = AppTheme.spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 左侧：导航图标 + 标题
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s)
        ) {
            navigationIcon?.let { icon ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .selectable(
                            selected = false,
                            onClick = { onNavigationClick?.invoke() },
                            role = Role.Button
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Back",
                        tint = AppTheme.colors.onSurface
                    )
                }
            }

            Text(
                text = title,
                style = AppTheme.typography.titleLarge,
                color = AppTheme.colors.onSurface
            )
        }

        // 右侧：操作按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.xs)
        ) {
            actions()
        }
    }
}
