package com.timelinesolutions.kmpstack.core.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

/**
 * 数据库驱动工厂 - expect 声明
 * 平台特定实现将提供具体的驱动实例
 *
 * 使用示例：
 * ```kotlin
 * // 在其他模块中创建自己的数据库
 * val driverFactory = DatabaseDriverFactory(context)
 * val driver = driverFactory.createDriver(MyDatabase.Schema, "my_database.db")
 * val database = MyDatabase(driver)
 * ```
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseDriverFactory {
    /**
     * 创建数据库驱动
     *
     * @param schema 数据库 Schema（由 SQLDelight 生成）
     * @param name 数据库文件名
     * @return SqlDriver 实例
     */
    fun createDriver(schema: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver
}
