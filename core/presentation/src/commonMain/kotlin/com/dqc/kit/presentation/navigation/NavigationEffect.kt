package com.dqc.kit.presentation.navigation

/**
 * 导航效果（副作用）
 * 用于表示导航意图，由各平台具体实现导航逻辑
 *
 * 设计原则：
 * 1. ViewModel 只发出"要去哪里"的指令
 * 2. 具体如何跳转由各平台的 UI 层处理
 * 3. 支持参数传递和返回结果处理
 *
 * 使用示例：
 * ```kotlin
 * // ViewModel 中发出导航指令
 * sendEffect(NavigationEffect.ToRoute("/home"))
 * sendEffect(NavigationEffect.ToRoute("/detail/{id}", mapOf("id" to "123")))
 * sendEffect(NavigationEffect.PopBackStack)
 *
 * // Android (Compose Navigation) 处理
 * @Composable
 * fun HandleNavigationEffect(
 *     effect: Flow<NavigationEffect>,
 *     navController: NavController
 * ) {
 *     LaunchedEffect(Unit) {
 *         effect.collect { navigationEffect ->
 *             when (navigationEffect) {
 *                 is NavigationEffect.ToRoute -> {
 *                     val route = navigationEffect.buildRoute()
 *                     navController.navigate(route)
 *                 }
 *                 is NavigationEffect.PopBackStack -> {
 *                     navController.popBackStack()
 *                 }
 *                 // ...
 *             }
 *         }
 *     }
 * }
 *
 * // iOS (SwiftUI Navigation) 处理
 * class ViewModelWrapper: ObservableObject {
 *     func handleNavigationEffect(_ effect: NavigationEffect) {
 *         switch effect {
 *         case .toRoute(let route, let params):
 *             path.append(route.buildPath(with: params))
 *         case .popBackStack:
 *             path.removeLast()
 *         // ...
 *         }
 *     }
 * }
 * ```
 */
sealed class NavigationEffect {

    /**
     * 导航到指定路由
     *
     * @param route 路由路径（如 "/home", "/detail/{id}"）
     * @param params 路由参数
     * @param popUpTo 导航前需要弹出的路由（用于清空栈）
 * @param inclusive 是否包含 popUpTo 指定的路由
     * @param singleTop 是否使用 singleTop 模式
     */
    data class ToRoute(
        val route: String,
        val params: Map<String, Any?> = emptyMap(),
        val popUpTo: String? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEffect() {
        /**
         * 构建完整的路由字符串
         */
        fun buildRoute(): String {
            if (params.isEmpty()) return route

            var result = route
            params.forEach { (key, value) ->
                result = result.replace("{$key}", value.toString())
            }
            return result
        }
    }

    /**
     * 返回上一页
     *
     * @param result 返回结果（可选）
     */
    data class PopBackStack(
        val result: Map<String, Any?>? = null
    ) : NavigationEffect()

    /**
     * 返回到指定路由
     *
     * @param route 目标路由
     * @param inclusive 是否弹出目标路由
     */
    data class PopUpTo(
        val route: String,
        val inclusive: Boolean = false
    ) : NavigationEffect()

    /**
     * 清空导航栈并导航到指定路由
     *
     * @param route 目标路由
     */
    data class ClearStackAndTo(
        val route: String
    ) : NavigationEffect()

    /**
     * 显示底部弹窗
     *
     * @param route 弹窗路由
     * @param params 参数
     */
    data class ShowBottomSheet(
        val route: String,
        val params: Map<String, Any?> = emptyMap()
    ) : NavigationEffect()

    /**
     * 显示对话框
     *
     * @param route 对话框路由
     * @param params 参数
     */
    data class ShowDialog(
        val route: String,
        val params: Map<String, Any?> = emptyMap()
    ) : NavigationEffect()

    /**
     * 关闭当前弹窗/对话框
     */
    data object Dismiss : NavigationEffect()

    /**
     * 打开外部链接
     *
     * @param url 链接地址
     */
    data class OpenUrl(val url: String) : NavigationEffect()

    /**
     * 打开系统设置
     *
     * @param setting 设置项（各平台具体定义）
     */
    data class OpenSettings(val setting: String? = null) : NavigationEffect()

    /**
     * 分享内容
     *
     * @param content 分享内容
     */
    data class Share(val content: ShareContent) : NavigationEffect() {
        data class ShareContent(
            val title: String? = null,
            val text: String? = null,
            val url: String? = null,
            val imageUri: String? = null
        )
    }

    companion object {
        /**
         * 便捷的导航到路由方法
         */
        fun to(
            route: String,
            vararg params: Pair<String, Any?>
        ): NavigationEffect = ToRoute(route, params.toMap())

        /**
         * 便捷的返回方法
         */
        fun back(result: Map<String, Any?>? = null): NavigationEffect =
            PopBackStack(result)

        /**
         * 便捷的清空栈并导航方法
         */
        fun clearAndTo(route: String): NavigationEffect = ClearStackAndTo(route)
    }
}

/**
 * 路由定义
 * 集中管理应用中的所有路由
 */
object Routes {
    // 首页
    const val HOME = "/home"

    // 登录
    const val LOGIN = "/login"
    const val REGISTER = "/register"

    // 用户
    const val PROFILE = "/profile"
    const val PROFILE_DETAIL = "/profile/{userId}"
    const val SETTINGS = "/settings"

    // 内容
    const val DETAIL = "/detail/{id}"
    const val LIST = "/list/{type}"

    // 弹窗
    const val BOTTOM_SHEET_MENU = "/bottom_sheet/menu"
    const val DIALOG_CONFIRM = "/dialog/confirm"

    /**
     * 构建带参数的路由
     */
    fun profileDetail(userId: String): String = "/profile/$userId"
    fun detail(id: String): String = "/detail/$id"
    fun list(type: String): String = "/list/$type"
}

/**
 * 导航效果构建器
 * 提供 DSL 风格的导航效果构建
 */
class NavigationEffectBuilder {
    private var effect: NavigationEffect? = null

    fun to(route: String, block: ToRouteBuilder.() -> Unit = {}) {
        val builder = ToRouteBuilder(route)
        builder.block()
        effect = builder.build()
    }

    fun back(result: Map<String, Any?>? = null) {
        effect = NavigationEffect.PopBackStack(result)
    }

    fun popUpTo(route: String, inclusive: Boolean = false) {
        effect = NavigationEffect.PopUpTo(route, inclusive)
    }

    fun clearAndTo(route: String) {
        effect = NavigationEffect.ClearStackAndTo(route)
    }

    fun showBottomSheet(route: String, params: Map<String, Any?> = emptyMap()) {
        effect = NavigationEffect.ShowBottomSheet(route, params)
    }

    fun showDialog(route: String, params: Map<String, Any?> = emptyMap()) {
        effect = NavigationEffect.ShowDialog(route, params)
    }

    fun dismiss() {
        effect = NavigationEffect.Dismiss
    }

    fun openUrl(url: String) {
        effect = NavigationEffect.OpenUrl(url)
    }

    fun build(): NavigationEffect = effect
        ?: throw IllegalStateException("NavigationEffect not set")

    class ToRouteBuilder(private val route: String) {
        var params: MutableMap<String, Any?> = mutableMapOf()
        var popUpTo: String? = null
        var inclusive: Boolean = false
        var singleTop: Boolean = false

        fun param(key: String, value: Any?) {
            params[key] = value
        }

        fun build(): NavigationEffect.ToRoute = NavigationEffect.ToRoute(
            route = route,
            params = params,
            popUpTo = popUpTo,
            inclusive = inclusive,
            singleTop = singleTop
        )
    }
}

/**
 * DSL 风格的导航效果构建
 */
inline fun navigationEffect(block: NavigationEffectBuilder.() -> Unit): NavigationEffect {
    return NavigationEffectBuilder().apply(block).build()
}
