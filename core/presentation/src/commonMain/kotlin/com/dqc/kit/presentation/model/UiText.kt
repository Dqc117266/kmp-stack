package com.dqc.kit.presentation.model

/**
 * UI 文本抽象类
 * 用于在 commonMain 中定义文本，支持多种来源：
 * 1. 纯文本字符串
 * 2. 字符串资源（通过资源标识符）
 * 3. 动态构建的字符串
 *
 * 设计原则：
 * 1. 支持多平台（Android 和 iOS）
 * 2. 不依赖特定平台的 Context 或资源系统
 * 3. 支持参数化字符串
 *
 * 使用示例：
 * ```kotlin
 * // 纯文本
 * val title = UiText.Plain("登录")
 *
 * // 带参数的文本
 * val message = UiText.Plain("欢迎，$username")
 *
 * // 资源引用（Android 使用 R.string.xxx，iOS 使用 Localizable.xxx）
 * val error = UiText.Resource("error_network", "网络错误")
 *
 * // 带参数的资源
 * val count = UiText.Resource("item_count", "共 %d 项", 42)
 *
 * // 在 ViewModel 中使用
 * updateState { copy(title = UiText.Resource("login_title")) }
 *
 * // 在各平台解析
 * // Android: text.asString(context)
 * // iOS: text.asString()
 * ```
 */
sealed class UiText {

    /**
     * 纯文本
     * @param value 文本内容
     */
    data class Plain(val value: String) : UiText()

    /**
     * 字符串资源引用
     *
     * @param key 资源键名（Android: R.string.xxx 的名称，iOS: Localizable.xxx 的键）
     * @param defaultValue 默认值（当资源未找到时使用）
     * @param args 格式化参数
     */
    data class Resource(
        val key: String,
        val defaultValue: String,
        val args: List<Any> = emptyList()
    ) : UiText() {
        constructor(key: String, defaultValue: String, vararg args: Any) :
                this(key, defaultValue, args.toList())
    }

    /**
     * 动态构建的文本
     * @param builder 文本构建函数
     */
    data class Dynamic(val builder: () -> String) : UiText()

    companion object {
        /**
         * 创建纯文本
         */
        fun plain(value: String): UiText = Plain(value)

        /**
         * 创建资源引用
         */
        fun resource(
            key: String,
            defaultValue: String,
            vararg args: Any
        ): UiText = Resource(key, defaultValue, *args)

        /**
         * 创建动态文本
         */
        fun dynamic(builder: () -> String): UiText = Dynamic(builder)

        /**
         * 空文本
         */
        val Empty: UiText = Plain("")
    }
}

/**
 * 将 UiText 转换为字符串（commonMain 中的默认实现）
 * 注意：此方法返回默认值或纯文本值
 * 各平台应该提供自己的解析方法以支持资源查找
 */
fun UiText.asString(): String = when (this) {
    is UiText.Plain -> value
    is UiText.Resource -> {
        if (args.isEmpty()) {
            defaultValue
        } else {
            defaultValue.format(*args.toTypedArray())
        }
    }
    is UiText.Dynamic -> builder()
}

/**
 * 带类型的 UI 值
 * 用于在 UI 层传递带类型信息的值
 */
sealed class UiValue<out T> {
    /**
     * 实际值
     */
    data class Value<out T>(val value: T) : UiValue<T>()

    /**
     * 加载中
     */
    data object Loading : UiValue<Nothing>()

    /**
     * 错误
     * @param message 错误信息
     */
    data class Error(val message: UiText) : UiValue<Nothing>()

    /**
     * 空值
     */
    data object Empty : UiValue<Nothing>()

    companion object {
        fun <T> of(value: T): UiValue<T> = Value(value)
        fun <T> loading(): UiValue<T> = Loading
        fun <T> error(message: UiText): UiValue<T> = Error(message)
        fun <T> error(message: String): UiValue<T> = Error(UiText.plain(message))
        fun <T> empty(): UiValue<T> = Empty
    }
}

/**
 * UI 列表数据包装类
 * 支持加载更多、刷新等状态
 */
data class UiList<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: UiText? = null
) {
    /**
     * 是否为空列表
     */
    val isEmpty: Boolean get() = items.isEmpty() && !isLoading

    /**
     * 是否可以加载更多
     */
    val canLoadMore: Boolean get() = hasMore && !isLoadingMore && !isLoading

    companion object {
        fun <T> loading(): UiList<T> = UiList(isLoading = true)
        fun <T> refreshing(items: List<T>): UiList<T> =
            UiList(items = items, isRefreshing = true)
        fun <T> loadingMore(items: List<T>): UiList<T> =
            UiList(items = items, isLoadingMore = true)
        fun <T> error(message: UiText): UiList<T> =
            UiList(error = message)
        fun <T> success(items: List<T>, hasMore: Boolean = true): UiList<T> =
            UiList(items = items, hasMore = hasMore)
    }
}
