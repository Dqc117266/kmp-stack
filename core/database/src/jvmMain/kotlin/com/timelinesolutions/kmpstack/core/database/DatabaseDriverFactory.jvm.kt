package com.timelinesolutions.kmpstack.core.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

/**
 * JVM 平台数据库驱动工厂实现
 *
 * 支持文件数据库和内存数据库
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory {
    /**
     * 创建数据库驱动
     *
     * @param schema 数据库 Schema（由 SQLDelight 生成）
     * @param name 数据库文件名，使用 ":memory:" 创建内存数据库
     * @return SqlDriver 实例
     */
    actual fun createDriver(schema: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver {
        val driver = JdbcSqliteDriver(
            url = if (name == ":memory:") {
                JdbcSqliteDriver.IN_MEMORY
            } else {
                "jdbc:sqlite:$name"
            }
        )
        // 执行 schema 创建
        schema.create(driver)
        return driver
    }
}
