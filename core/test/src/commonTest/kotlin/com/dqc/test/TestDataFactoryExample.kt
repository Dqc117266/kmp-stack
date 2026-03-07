package com.dqc.test

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 测试数据工厂使用示例
 */
class TestDataFactoryExample {

    @Test
    fun testCreateUser() {
        // 简单创建
        val user1 = createUser()
        assertNotNull(user1.id)
        assertEquals("Test User", user1.name)

        // 指定参数
        val user2 = createUser(
            id = "user-123",
            name = "张三",
            email = "zhangsan@example.com"
        )
        assertEquals("user-123", user2.id)
        assertEquals("张三", user2.name)

        // DSL 风格
        val user3 = createUser {
            id = "user-456"
            name = "李四"
            email = "lisi@example.com"
        }
        assertEquals("李四", user3.name)
    }

    @Test
    fun testCreateOrder() {
        val order = createOrder {
            items = listOf(
                createItem {
                    title = "iPhone"
                    price = 9999.0
                },
                createItem {
                    title = "iPad"
                    price = 5999.0
                }
            )
            status = OrderStatus.CONFIRMED
        }

        assertEquals(2, order.items.size)
        assertEquals(OrderStatus.CONFIRMED, order.status)
        assertEquals(15998.0, order.totalPrice)
    }

    @Test
    fun testCreateList() {
        val users = createList(5) { index ->
            createUser {
                name = "User $index"
            }
        }

        assertEquals(5, users.size)
        assertEquals("User 0", users[0].name)
        assertEquals("User 4", users[4].name)
    }
}
