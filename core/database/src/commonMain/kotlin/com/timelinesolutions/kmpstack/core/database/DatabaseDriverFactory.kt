package com.timelinesolutions.kmpstack.core.database

import app.cash.sqldelight.db.SqlDriver

/**
 * 数据库驱动工厂 - expect 声明
 * 平台特定实现将提供具体的驱动实例
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
