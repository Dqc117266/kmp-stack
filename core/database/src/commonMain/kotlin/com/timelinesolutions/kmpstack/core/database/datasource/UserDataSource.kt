package com.timelinesolutions.kmpstack.core.database.datasource

import com.timelinesolutions.kmpstack.core.database.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据源接口
 * 定义了用户相关的所有数据库操作
 * 返回 Flow 支持响应式编程
 */
interface UserDataSource {
    /**
     * 获取所有用户
     */
    fun getAllUsers(): Flow<List<User>>

    /**
     * 根据 ID 获取用户
     */
    fun getUserById(id: String): Flow<User?>

    /**
     * 根据邮箱获取用户
     */
    fun getUserByEmail(email: String): Flow<User?>

    /**
     * 插入或替换用户
     */
    suspend fun insertOrReplaceUser(user: User)

    /**
     * 插入用户
     */
    suspend fun insertUser(user: User)

    /**
     * 更新用户
     */
    suspend fun updateUser(user: User)

    /**
     * 根据 ID 删除用户
     */
    suspend fun deleteUserById(id: String)

    /**
     * 删除所有用户
     */
    suspend fun deleteAllUsers()

    /**
     * 获取用户数量
     */
    fun getUserCount(): Flow<Long>
}
