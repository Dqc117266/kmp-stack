package com.dqc.kit.data.local.database

import app.cash.sqldelight.db.SqlDriver

/**
 * 数据库驱动工厂
 * 使用 expect/actual 模式为不同平台提供具体实现
 *
 * 平台实现：
 * - Android: AndroidSqliteDriver
 * - iOS: NativeSqliteDriver
 * - JVM: JdbcSqliteDriver
 */
expect class DatabaseDriverFactory {
    /**
     * 创建 SQLDelight 数据库驱动
     * @return 平台特定的 SqlDriver 实例
     */
    fun createDriver(): SqlDriver
}
