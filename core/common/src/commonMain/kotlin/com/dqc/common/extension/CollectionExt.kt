package com.dqc.common.extension

/**
 * 集合扩展函数集
 */

// ==================== List 扩展 ====================

/**
 * 如果列表为空，则返回默认值
 *
 * @param default 默认值提供者
 * @return 原列表或默认值
 */
fun <T> List<T>.orEmpty(default: () -> List<T>): List<T> = ifEmpty { default() }

/**
 * 安全地获取元素，索引越界时返回 null
 *
 * @param index 索引
 * @return 元素或 null
 */
fun <T> List<T>.getOrNull(index: Int): T? = if (index in indices) this[index] else null

/**
 * 安全地获取元素，索引越界时返回默认值
 *
 * @param index 索引
 * @param default 默认值提供者
 * @return 元素或默认值
 */
fun <T> List<T>.getOrDefault(index: Int, default: () -> T): T =
    if (index in indices) this[index] else default()

/**
 * 将列表分割成指定大小的子列表
 *
 * @param size 每个子列表的大小
 * @return 子列表集合
 */
fun <T> List<T>.chunkedSafe(size: Int): List<List<T>> {
    if (size <= 0) return listOf(this)
    return chunked(size)
}

/**
 * 去重并保持原有顺序（基于指定键）
 *
 * @param keySelector 键选择器
 * @return 去重后的列表
 */
fun <T, K> List<T>.distinctByOrdered(keySelector: (T) -> K): List<T> {
    val seen = mutableSetOf<K>()
    return filter { seen.add(keySelector(it)) }
}

/**
 * 根据条件去重，保留第一个匹配项
 *
 * @param predicate 条件
 * @return 去重后的列表
 */
fun <T> List<T>.distinctByCondition(predicate: (T, T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (result.none { predicate(item, it) }) {
            result.add(item)
        }
    }
    return result
}

/**
 * 查找并替换第一个匹配项
 *
 * @param predicate 匹配条件
 * @param transform 转换函数
 * @return 新列表
 */
fun <T> List<T>.replaceFirst(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    val index = indexOfFirst(predicate)
    if (index == -1) return this
    return toMutableList().apply { set(index, transform(this[index])) }
}

/**
 * 查找并替换所有匹配项
 *
 * @param predicate 匹配条件
 * @param transform 转换函数
 * @return 新列表
 */
fun <T> List<T>.replaceAll(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    return map { if (predicate(it)) transform(it) else it }
}

/**
 * 如果元素不存在则添加
 *
 * @param element 元素
 * @return 是否添加成功
 */
fun <T> MutableList<T>.addIfAbsent(element: T): Boolean {
    return if (!contains(element)) {
        add(element)
        true
    } else false
}

/**
 * 添加到开头
 *
 * @param element 元素
 */
fun <T> MutableList<T>.addFirst(element: T) {
    add(0, element)
}

/**
 * 安全地移除元素，如果不存在则返回 false
 *
 * @param element 元素
 * @return 是否移除成功
 */
fun <T> MutableList<T>.removeIfPresent(element: T): Boolean {
    return remove(element)
}

/**
 * 批量添加元素（仅添加不存在的）
 *
 * @param elements 元素集合
 * @return 添加的数量
 */
fun <T> MutableList<T>.addAllAbsent(elements: Collection<T>): Int {
    var count = 0
    for (element in elements) {
        if (addIfAbsent(element)) count++
    }
    return count
}

// ==================== Map 扩展 ====================

/**
 * 如果 Map 为空，则返回默认值
 *
 * @param default 默认值提供者
 * @return 原 Map 或默认值
 */
fun <K, V> Map<K, V>.orEmpty(default: () -> Map<K, V>): Map<K, V> = if (isEmpty()) default() else this

/**
 * 安全地获取值，键不存在时返回 null
 *
 * @param key 键
 * @return 值或 null
 */
fun <K, V> Map<K, V>.getOrNull(key: K): V? = get(key)

/**
 * 获取值，如果不存在则计算并放入
 *
 * @param key 键
 * @param defaultValue 默认值提供者
 * @return 值
 */
inline fun <K, V> MutableMap<K, V>.getOrPutSafe(key: K, defaultValue: () -> V): V {
    return getOrPut(key, defaultValue)
}

/**
 * 如果键不存在，则放入值
 *
 * @param key 键
 * @param value 值
 * @return 是否放入成功
 */
fun <K, V> MutableMap<K, V>.putIfAbsentSafe(key: K, value: V): Boolean {
    return if (!containsKey(key)) {
        put(key, value)
        true
    } else false
}

/**
 * 合并两个 Map，当前 Map 的值优先
 *
 * @param other 另一个 Map
 * @return 合并后的 Map
 */
fun <K, V> Map<K, V>.mergeWith(other: Map<K, V>): Map<K, V> {
    return other + this
}

/**
 * 过滤非空值
 *
 * @return 过滤后的 Map
 */
fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filterValues { it != null } as Map<K, V>
}

/**
 * 映射键（保持值不变）
 *
 * @param transform 键转换函数
 * @return 新 Map
 */
fun <K, V, R> Map<K, V>.mapKeys(transform: (Map.Entry<K, V>) -> R): Map<R, V> {
    return map { transform(it) to it.value }.toMap()
}

/**
 * 映射值（保持键不变）
 *
 * @param transform 值转换函数
 * @return 新 Map
 */
fun <K, V, R> Map<K, V>.mapValues(transform: (Map.Entry<K, V>) -> R): Map<K, R> {
    return map { it.key to transform(it) }.toMap()
}

// ==================== Set 扩展 ====================

/**
 * 如果 Set 为空，则返回默认值
 *
 * @param default 默认值提供者
 * @return 原 Set 或默认值
 */
fun <T> Set<T>.orEmpty(default: () -> Set<T>): Set<T> = if (isEmpty()) default() else this

/**
 * 如果元素不存在则添加，存在则移除（切换）
 *
 * @param element 元素
 * @return 操作后的 Set
 */
fun <T> Set<T>.toggle(element: T): Set<T> {
    return if (contains(element)) this - element else this + element
}

// ==================== 通用集合操作 ====================

/**
 * 检查集合是否不为空
 */
fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean = !isNullOrEmpty()

/**
 * 如果集合不为空，执行操作
 */
inline fun <T> Collection<T>?.ifNotNullOrEmpty(block: (Collection<T>) -> Unit) {
    if (!isNullOrEmpty()) block(this)
}

/**
 * 如果集合为空或为 null，执行操作
 */
inline fun <T, R> Collection<T>?.ifNullOrEmpty(block: () -> R): R? {
    return if (isNullOrEmpty()) block() else null
}

/**
 * 获取第 n 个元素（支持负数索引，从末尾计数）
 *
 * @param n 索引（负数表示从末尾）
 * @return 元素或 null
 */
fun <T> List<T>.nth(n: Int): T? {
    val index = if (n >= 0) n else size + n
    return getOrNull(index)
}

/**
 * 将集合分成两组：满足条件和不满足条件的
 *
 * @param predicate 条件
 * @return Pair<满足条件的, 不满足条件的>
 */
fun <T> Iterable<T>.partitionToLists(predicate: (T) -> Boolean): Pair<List<T>, List<T>> {
    val first = mutableListOf<T>()
    val second = mutableListOf<T>()
    for (element in this) {
        if (predicate(element)) {
            first.add(element)
        } else {
            second.add(element)
        }
    }
    return Pair(first, second)
}

/**
 * 计算元素出现次数
 *
 * @return 元素到计数的 Map
 */
fun <T> Iterable<T>.countOccurrences(): Map<T, Int> {
    return groupingBy { it }.eachCount()
}

/**
 * 查找最常见的元素
 *
 * @return 最常见的元素或 null
 */
fun <T> Iterable<T>.mostCommon(): T? {
    return countOccurrences().maxByOrNull { it.value }?.key
}
