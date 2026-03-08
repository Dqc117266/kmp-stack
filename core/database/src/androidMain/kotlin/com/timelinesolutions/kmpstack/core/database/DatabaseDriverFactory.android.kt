package com.timelinesolutions.kmpstack.core.database

import android.content.Context
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/**
 * Android 平台数据库驱动工厂实现
 *
 * @param context Android Context
 */
actual class DatabaseDriverFactory(private val context: Context) {
    /**
     * 创建数据库驱动
     *
     * @param schema 数据库 Schema（由 SQLDelight 生成）
     * @param name 数据库文件名
     * @return SqlDriver 实例
     */
    actual fun createDriver(schema: SqlSchema<QueryResult.Value<Unit>>, name: String): SqlDriver {
        return AndroidSqliteDriver(
            schema = schema,
            context = context,
            name = name
        )
    }
}
