package com.dqc.test

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * DSL 风格的测试数据工厂
 *
 * 提供流畅的 API 用于生成测试用的实体对象
 *
 * 使用示例：
 * ```kotlin
 * val user = createUser(
 *     id = "user-1",
 *     name = "张三",
 *     email = "zhangsan@example.com"
 * )
 *
 * val userWithOverrides = createUser {
 *     id = "user-2"
 *     name = "李四"
 * }
 * ```
 */
object TestDataFactory {

    /**
     * 生成唯一 ID
     */
    private var idCounter = 0
    fun nextId(prefix: String = "test"): String =
        "$prefix-${++idCounter}-${Clock.System.now().toEpochMilliseconds()}"

    /**
     * 获取当前时间
     */
    fun now(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    /**
     * 创建测试用的 User 实体
     */
    inline fun createUser(
        id: String = nextId("user"),
        name: String = "Test User",
        email: String = "test@example.com",
        avatar: String? = null,
        createdAt: LocalDateTime = now(),
        block: UserBuilder.() -> Unit = {}
    ): TestUser {
        val builder = UserBuilder(id, name, email, avatar, createdAt)
        builder.apply(block)
        return builder.build()
    }

    /**
     * 创建测试用的 Item 实体
     */
    inline fun createItem(
        id: String = nextId("item"),
        title: String = "Test Item",
        description: String = "Test description",
        price: Double = 99.99,
        quantity: Int = 1,
        block: ItemBuilder.() -> Unit = {}
    ): TestItem {
        val builder = ItemBuilder(id, title, description, price, quantity)
        builder.apply(block)
        return builder.build()
    }

    /**
     * 创建测试用的 Order 实体
     */
    inline fun createOrder(
        id: String = nextId("order"),
        userId: String = nextId("user"),
        items: List<TestItem> = listOf(createItem()),
        status: OrderStatus = OrderStatus.PENDING,
        createdAt: LocalDateTime = now(),
        block: OrderBuilder.() -> Unit = {}
    ): TestOrder {
        val builder = OrderBuilder(id, userId, items, status, createdAt)
        builder.apply(block)
        return builder.build()
    }

    /**
     * 批量生成测试数据
     */
    inline fun <T> createList(
        count: Int = 3,
        crossinline factory: (index: Int) -> T
    ): List<T> = List(count) { index -> factory(index) }
}

// ==================== Builder 类 ====================

class UserBuilder(
    var id: String,
    var name: String,
    var email: String,
    var avatar: String?,
    var createdAt: LocalDateTime
) {
    fun build(): TestUser = TestUser(
        id = id,
        name = name,
        email = email,
        avatar = avatar,
        createdAt = createdAt
    )
}

class ItemBuilder(
    var id: String,
    var title: String,
    var description: String,
    var price: Double,
    var quantity: Int
) {
    var isAvailable: Boolean = true
    var tags: List<String> = emptyList()

    fun build(): TestItem = TestItem(
        id = id,
        title = title,
        description = description,
        price = price,
        quantity = quantity,
        isAvailable = isAvailable,
        tags = tags
    )
}

class OrderBuilder(
    var id: String,
    var userId: String,
    var items: List<TestItem>,
    var status: OrderStatus,
    var createdAt: LocalDateTime
) {
    var shippingAddress: String? = null
    var note: String? = null

    val totalPrice: Double
        get() = items.sumOf { it.price * it.quantity }

    fun build(): TestOrder = TestOrder(
        id = id,
        userId = userId,
        items = items,
        totalPrice = totalPrice,
        status = status,
        shippingAddress = shippingAddress,
        note = note,
        createdAt = createdAt
    )
}

// ==================== 测试实体 ====================

data class TestUser(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String?,
    val createdAt: LocalDateTime
)

data class TestItem(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val isAvailable: Boolean = true,
    val tags: List<String> = emptyList()
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

data class TestOrder(
    val id: String,
    val userId: String,
    val items: List<TestItem>,
    val totalPrice: Double,
    val status: OrderStatus,
    val shippingAddress: String?,
    val note: String?,
    val createdAt: LocalDateTime
)

// ==================== DSL 入口 ====================

inline fun createUser(
    id: String = TestDataFactory.nextId("user"),
    name: String = "Test User",
    email: String = "test@example.com",
    avatar: String? = null,
    createdAt: LocalDateTime = TestDataFactory.now(),
    block: UserBuilder.() -> Unit = {}
): TestUser = TestDataFactory.createUser(id, name, email, avatar, createdAt, block)

inline fun createItem(
    id: String = TestDataFactory.nextId("item"),
    title: String = "Test Item",
    description: String = "Test description",
    price: Double = 99.99,
    quantity: Int = 1,
    block: ItemBuilder.() -> Unit = {}
): TestItem = TestDataFactory.createItem(id, title, description, price, quantity, block)

inline fun createOrder(
    id: String = TestDataFactory.nextId("order"),
    userId: String = TestDataFactory.nextId("user"),
    items: List<TestItem> = listOf(createItem()),
    status: OrderStatus = OrderStatus.PENDING,
    createdAt: LocalDateTime = TestDataFactory.now(),
    block: OrderBuilder.() -> Unit = {}
): TestOrder = TestDataFactory.createOrder(id, userId, items, status, createdAt, block)

inline fun <T> createList(
    count: Int = 3,
    crossinline factory: (index: Int) -> T
): List<T> = TestDataFactory.createList(count, factory)
