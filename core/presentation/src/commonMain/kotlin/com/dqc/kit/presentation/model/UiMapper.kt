package com.dqc.kit.presentation.model

/**
 * UI 格式化器接口
 * 用于将 Domain 层的数据格式化为 UI 层可显示的数据
 *
 * 设计原则：
 * 1. Domain 层不应该关心 UI 如何显示数据
 * 2. 格式化逻辑应该在 Presentation 层统一处理
 * 3. 支持多平台的格式化需求
 *
 * 使用示例：
 * ```kotlin
 * // 时间戳格式化
 * class RelativeTimeFormatter : UiFormatter<Long, String> {
 *     override fun format(input: Long): String {
 *         val diff = Clock.System.now().toEpochMilliseconds() - input
 *         return when {
 *             diff < 60000 -> "刚刚"
 *             diff < 3600000 -> "${diff / 60000}分钟前"
 *             else -> "${diff / 3600000}小时前"
 *         }
 *     }
 * }
 *
 * // 使用
 * val formatter = RelativeTimeFormatter()
 * val displayTime = formatter.format(timestamp)
 * ```
 */
interface UiFormatter<in T, out R> {
    /**
     * 格式化输入数据
     * @param input 输入数据
     * @return 格式化后的结果
     */
    fun format(input: T): R
}

/**
 * 带上下文的格式化器接口
 * 用于需要额外上下文信息的格式化场景
 */
interface ContextualFormatter<in T, in C, out R> {
    /**
     * 格式化输入数据
     * @param input 输入数据
     * @param context 上下文信息
     * @return 格式化后的结果
     */
    fun format(input: T, context: C): R
}

/**
 * UI 映射器接口
 * 用于将 Domain 层的实体映射为 UI 层的模型
 *
 * 使用示例：
 * ```kotlin
 * // Domain 实体
 * data class UserEntity(
 *     val id: String,
 *     val name: String,
 *     val avatarUrl: String,
 *     val createdAt: Long
 * )
 *
 * // UI 模型
 * data class UserUiModel(
 *     val id: String,
 *     val displayName: String,
 *     val avatarUrl: String,
 *     val joinTime: String
 * )
 *
 * // 映射器
 * class UserUiMapper(
 *     private val timeFormatter: UiFormatter<Long, String>
 * ) : UiMapper<UserEntity, UserUiModel> {
 *     override fun map(input: UserEntity): UserUiModel {
 *         return UserUiModel(
 *             id = input.id,
 *             displayName = input.name.ifEmpty { "匿名用户" },
 *             avatarUrl = input.avatarUrl,
 *             joinTime = timeFormatter.format(input.createdAt)
 *         )
 *     }
 * }
 * ```
 */
interface UiMapper<in T, out R> {
    /**
     * 将输入数据映射为输出数据
     * @param input 输入数据
     * @return 映射后的结果
     */
    fun map(input: T): R

    /**
     * 批量映射
     * @param inputs 输入数据列表
     * @return 映射后的结果列表
     */
    fun mapList(inputs: List<T>): List<R> = inputs.map { map(it) }
}

/**
 * 通用格式化器集合
 */
object Formatters {

    /**
     * 数字格式化（千分位）
     */
    object NumberFormatter : UiFormatter<Number, String> {
        override fun format(input: Number): String {
            return input.toLong().let { num ->
                when {
                    num >= 100000000 -> "${num / 100000000}亿"
                    num >= 10000 -> "${num / 10000}万"
                    num >= 1000 -> "${num / 1000}k"
                    else -> num.toString()
                }
            }
        }
    }

    /**
     * 文件大小格式化
     */
    object FileSizeFormatter : UiFormatter<Long, String> {
        override fun format(input: Long): String {
            return when {
                input >= 1024 * 1024 * 1024 -> "%.2f GB".format(input / (1024.0 * 1024.0 * 1024.0))
                input >= 1024 * 1024 -> "%.2f MB".format(input / (1024.0 * 1024.0))
                input >= 1024 -> "%.2f KB".format(input / 1024.0)
                else -> "$input B"
            }
        }
    }

    /**
     * 持续时间格式化（秒 -> mm:ss）
     */
    object DurationFormatter : UiFormatter<Int, String> {
        override fun format(input: Int): String {
            val minutes = input / 60
            val seconds = input % 60
            return "%02d:%02d".format(minutes, seconds)
        }
    }

    /**
     * 百分比格式化
     */
    object PercentageFormatter : UiFormatter<Double, String> {
        override fun format(input: Double): String {
            return "${(input * 100).toInt()}%"
        }
    }

    /**
     * 价格格式化
     */
    object PriceFormatter : UiFormatter<Double, String> {
        override fun format(input: Double): String {
            return "¥%.2f".format(input)
        }
    }
}

/**
 * 映射器扩展函数
 * 提供更便捷的映射语法
 */
fun <T, R> T.toUi(mapper: UiMapper<T, R>): R = mapper.map(this)
fun <T, R> List<T>.toUi(mapper: UiMapper<T, R>): List<R> = mapper.mapList(this)
