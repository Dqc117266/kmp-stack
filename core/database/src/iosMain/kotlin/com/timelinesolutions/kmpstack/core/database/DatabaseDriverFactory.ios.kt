package com.timelinesolutions.kmpstack.core.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/**
 * iOS 平台数据库驱动工厂实现
 */
actual class DatabaseDriverFactory {
    /**
     * 创建数据库驱动
     *
     * @param schema 数据库 Schema（由 SQLDelight 生成）
     * @param name 数据库文件名
     * @return SqlDriver 实例
     */
    actual fun createDriver(schema: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver {
        return NativeSqliteDriver(
            schema = schema,
            name = name
        )
    }
}
