package com.timelinesolutions.kmpstack.core.database

import app.cash.sqldelight.Transacter

/**
 * 事务帮助类
 * 提供高级事务操作封装
 */
class TransactionHelper(private val database: Transacter) {

    /**
     * 执行批量插入操作
     *
     * @param items 要插入的数据列表
     * @param insert 单个插入操作，接收 Transacter 作为接收者
     */
    fun <T> batchInsert(
        items: List<T>,
        insert: Transacter.(T) -> Unit
    ) {
        if (items.isEmpty()) return

        database.transaction {
            items.forEach { item ->
                database.insert(item)
            }
        }
    }

    /**
     * 执行批量更新操作
     *
     * @param items 要更新的数据列表
     * @param update 单个更新操作，接收 Transacter 作为接收者
     */
    fun <T> batchUpdate(
        items: List<T>,
        update: Transacter.(T) -> Unit
    ) {
        if (items.isEmpty()) return

        database.transaction {
            items.forEach { item ->
                database.update(item)
            }
        }
    }

    /**
     * 执行批量删除操作
     *
     * @param ids 要删除的 ID 列表
     * @param delete 单个删除操作，接收 Transacter 作为接收者
     */
    fun batchDelete(
        ids: List<String>,
        delete: Transacter.(String) -> Unit
    ) {
        if (ids.isEmpty()) return

        database.transaction {
            ids.forEach { id ->
                database.delete(id)
            }
        }
    }

    /**
     * 在事务中执行多个操作
     *
     * @param operations 操作列表，每个操作接收 Transacter 作为接收者
     */
    fun executeInTransaction(
        vararg operations: Transacter.() -> Unit
    ) {
        database.transaction {
            operations.forEach { operation ->
                database.operation()
            }
        }
    }

    /**
     * 在事务中执行带返回值的操作
     *
     * @param block 事务操作块，接收 Transacter 作为接收者
     * @return 操作结果
     */
    fun <T> executeWithResult(
        block: Transacter.() -> T
    ): T {
        return database.transactionWithResult {
            database.block()
        }
    }
}

/**
 * 分页查询参数
 */
data class PaginationParams(
    val page: Int = 1,
    val pageSize: Int = 20,
    val offset: Int = (page - 1) * pageSize
) {
    companion object {
        fun default() = PaginationParams()
        fun of(page: Int, pageSize: Int = 20) = PaginationParams(page, pageSize)
    }
}

/**
 * 分页结果包装
 */
data class PagedResult<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Long,
    val hasMore: Boolean
) {
    companion object {
        fun <T> empty() = PagedResult<T>(
            data = emptyList(),
            page = 1,
            pageSize = 20,
            totalCount = 0,
            hasMore = false
        )
    }
}

/**
 * 排序方向
 */
enum class SortOrder {
    ASC,
    DESC
}
