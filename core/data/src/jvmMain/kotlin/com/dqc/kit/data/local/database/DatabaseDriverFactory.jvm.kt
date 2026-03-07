package com.dqc.kit.data.local.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

/**
 * JVM 平台数据库驱动工厂实现
 * 支持桌面应用
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        // 获取用户主目录下的应用数据目录
        val databaseDir = File(System.getProperty("user.home"), ".kmp-stack")
        if (!databaseDir.exists()) {
            databaseDir.mkdirs()
        }

        val databasePath = File(databaseDir, "app_database.db").absolutePath

        return JdbcSqliteDriver("jdbc:sqlite:$databasePath").also { driver ->
            // 确保数据库表已创建
            AppDatabase.Schema.create(driver)
        }
    }
}
