package com.dqc.common.extension

/**
 * 字符串扩展函数集
 */

/**
 * 如果字符串为 null 或空白字符，则返回默认值
 *
 * @param default 默认值
 * @return 原字符串或默认值
 */
fun String?.orDefault(default: String): String = if (this.isNullOrBlank()) default else this

/**
 * 如果字符串为 null 或空白字符，则返回 null
 *
 * @return 原字符串或 null
 */
fun String?.nullIfBlank(): String? = if (this.isNullOrBlank()) null else this

/**
 * 截断字符串，超过指定长度时添加省略号
 *
 * @param maxLength 最大长度
 * @param suffix 省略号，默认为 "..."
 * @return 截断后的字符串
 */
fun String.truncate(maxLength: Int, suffix: String = "..."): String {
    if (length <= maxLength) return this
    return substring(0, maxLength - suffix.length) + suffix
}

/**
 * 检查字符串是否为有效的邮箱格式
 */
fun String.isEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return matches(emailRegex)
}

/**
 * 检查字符串是否为有效的手机号（中国大陆）
 */
fun String.isChinesePhone(): Boolean {
    val phoneRegex = "^1[3-9]\\d{9}$".toRegex()
    return matches(phoneRegex)
}

/**
 * 检查字符串是否只包含数字
 */
fun String.isNumeric(): Boolean = all { it.isDigit() }

/**
 * 检查字符串是否只包含字母
 */
fun String.isAlpha(): Boolean = all { it.isLetter() }

/**
 * 检查字符串是否只包含字母和数字
 */
fun String.isAlphanumeric(): Boolean = all { it.isLetterOrDigit() }

/**
 * 移除字符串中的所有空白字符
 */
fun String.removeWhitespace(): String = replace(Regex("\\s+"), "")

/**
 * 将字符串首字母大写
 */
fun String.capitalize(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

/**
 * 将字符串首字母小写
 */
fun String.decapitalize(): String = replaceFirstChar { it.lowercase() }

/**
 * 转换为 snake_case
 */
fun String.toSnakeCase(): String {
    return replace(Regex("([a-z])([A-Z]+)"), "$1_$2")
        .lowercase()
}

/**
 * 转换为 kebab-case
 */
fun String.toKebabCase(): String {
    return replace(Regex("([a-z])([A-Z]+)"), "$1-$2")
        .lowercase()
}

/**
 * 转换为 camelCase
 */
fun String.toCamelCase(): String {
    return split(Regex("[_-]"))
        .mapIndexed { index, s ->
            if (index == 0) s.lowercase() else s.capitalize()
        }
        .joinToString("")
}

/**
 * 安全地截取子字符串，避免 IndexOutOfBoundsException
 *
 * @param startIndex 起始索引
 * @param endIndex 结束索引（不包含）
 * @return 子字符串或空字符串
 */
fun String.safeSubstring(startIndex: Int, endIndex: Int): String {
    val safeStart = startIndex.coerceIn(0, length)
    val safeEnd = endIndex.coerceIn(safeStart, length)
    return substring(safeStart, safeEnd)
}

/**
 * 安全地截取从开头到指定长度的子字符串
 *
 * @param length 最大长度
 * @return 子字符串
 */
fun String.takeSafe(length: Int): String = take(length.coerceAtLeast(0))

/**
 * 添加前缀（如果不存在）
 */
fun String.ensurePrefix(prefix: String): String = if (startsWith(prefix)) this else prefix + this

/**
 * 添加后缀（如果不存在）
 */
fun String.ensureSuffix(suffix: String): String = if (endsWith(suffix)) this else this + suffix

/**
 * 如果字符串为空或为 null，执行操作并返回结果
 */
inline fun <R> String?.ifBlank(block: () -> R): R? = if (isNullOrBlank()) block() else null

/**
 * 如果字符串不为空，执行操作
 */
inline fun String?.ifNotBlank(block: (String) -> Unit) {
    if (!isNullOrBlank()) block(this)
}
