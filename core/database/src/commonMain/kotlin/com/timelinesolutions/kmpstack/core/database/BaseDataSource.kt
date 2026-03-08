package com.timelinesolutions.kmpstack.core.database

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * 数据源基类
 * 提供通用的数据库操作封装，简化子类的实现
 *
 * @param T 数据库 Transacter 类型（由 SQLDelight 生成）
 */
abstract class BaseDataSource<T : Transacter>(
    protected val database: T
) {

    /**
     * 数据库操作的 CoroutineContext，默认为 Dispatchers.IO
     */
    protected open val dispatcher: CoroutineContext = Dispatchers.IO

    /**
     * 将查询结果转换为 Flow<List>
     *
     * @param query SQLDelight 查询对象
     * @param mapper 将数据库模型转换为领域模型的映射函数
     */
    protected fun <D : Any, R> flowList(
        query: app.cash.sqldelight.Query<D>,
        mapper: (D) -> R
    ): Flow<List<R>> {
        return query
            .asFlow()
            .mapToList(dispatcher)
            .map { list -> list.map(mapper) }
    }

    /**
     * 将查询结果转换为 Flow（单个对象或 null）
     *
     * @param query SQLDelight 查询对象
     * @param mapper 将数据库模型转换为领域模型的映射函数
     */
    protected fun <D : Any, R> flowOneOrNull(
        query: app.cash.sqldelight.Query<D>,
        mapper: (D) -> R
    ): Flow<R?> {
        return query
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .map { it?.let(mapper) }
    }

    /**
     * 在 IO 上下文中执行挂起操作
     *
     * @param block 要执行的操作
     */
    protected suspend fun <R> withTransaction(block: suspend () -> R): R {
        return withContext(dispatcher) {
            block()
        }
    }

    /**
     * 执行数据库事务
     *
     * @param block 事务中执行的操作
     */
    protected fun <R> transaction(block: Transacter.() -> R): R {
        return database.transactionWithResult { block(database) }
    }

    /**
     * 执行无返回值的事务
     *
     * @param block 事务中执行的操作
     */
    protected fun transactionNoResult(block: Transacter.() -> Unit) {
        database.transaction { block(database) }
    }
}

/**
 * Flow 扩展：在指定调度器上执行
 */
fun <T> Flow<T>.flowOnIO(): Flow<T> = this.flowOn(Dispatchers.IO)
