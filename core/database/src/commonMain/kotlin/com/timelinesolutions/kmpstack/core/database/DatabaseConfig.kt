package com.timelinesolutions.kmpstack.core.database

/**
 * 数据库配置
 * 用于配置数据库的各种参数
 *
 * @param name 数据库文件名
 * @param version 数据库版本（用于迁移）
 * @param enableWal 是否启用 WAL 模式（Android 平台有效）
 * @param maxSize 数据库最大大小（字节）
 */
data class DatabaseConfig(
    val name: String = "app_database.db",
    val version: Int = 1,
    val enableWal: Boolean = true,
    val maxSize: Long = 50 * 1024 * 1024, // 50MB 默认限制
    val journalMode: JournalMode = JournalMode.WAL
) {

    /**
     * 日志模式
     */
    enum class JournalMode {
        DELETE,
        TRUNCATE,
        PERSIST,
        MEMORY,
        WAL,
        OFF
    }

    companion object {
        /**
         * 默认配置
         */
        val DEFAULT = DatabaseConfig()

        /**
         * 测试配置（内存数据库）
         */
        val IN_MEMORY = DatabaseConfig(
            name = ":memory:",
            enableWal = false
        )
    }
}
