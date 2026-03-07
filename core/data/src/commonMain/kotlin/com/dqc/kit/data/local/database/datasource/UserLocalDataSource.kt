package com.dqc.kit.data.local.database.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.dqc.kit.data.local.database.AppDatabase
import com.dqc.kit.domain.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * 用户本地数据源
 * 负责用户数据的本地缓存操作（SQLDelight）
 */
internal class UserLocalDataSource(
    private val database: AppDatabase
) {
    private val queries = database.userQueries

    /**
     * 获取所有用户（Flow）
     */
    fun getAllUsers(): Flow<List<UserEntity>> =
        queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { list -> list.map { it.toEntity() } }

    /**
     * 获取用户详情
     */
    suspend fun getUserById(id: String): UserEntity? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toEntity()
    }

    /**
     * 获取当前登录用户（最近登录的）
     */
    suspend fun getCurrentUser(): UserEntity? = withContext(Dispatchers.Default) {
        queries.selectCurrentUser().executeAsOneOrNull()?.toEntity()
    }

    /**
     * 观察当前用户变化
     */
    fun observeCurrentUser(): Flow<UserEntity?> =
        queries.selectCurrentUser()
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toEntity() }

    /**
     * 保存或更新用户
     */
    suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.Default) {
        queries.insertOrReplace(
            id = user.id,
            name = user.name,
            email = user.email,
            avatar_url = user.avatarUrl,
            last_login_at = user.lastLoginAt.toEpochMilliseconds(),
            is_active = user.isActive,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }

    /**
     * 批量保存用户
     */
    suspend fun saveUsers(users: List<UserEntity>) = withContext(Dispatchers.Default) {
        database.transaction {
            users.forEach { user ->
                queries.insertOrReplace(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    avatar_url = user.avatarUrl,
                    last_login_at = user.lastLoginAt.toEpochMilliseconds(),
                    is_active = user.isActive,
                    updated_at = Clock.System.now().toEpochMilliseconds()
                )
            }
        }
    }

    /**
     * 删除用户
     */
    suspend fun deleteUser(id: String) = withContext(Dispatchers.Default) {
        queries.deleteById(id)
    }

    /**
     * 删除所有用户
     */
    suspend fun deleteAllUsers() = withContext(Dispatchers.Default) {
        queries.deleteAll()
    }

    /**
     * 获取用户数量
     */
    suspend fun getUserCount(): Long = withContext(Dispatchers.Default) {
        queries.count().executeAsOne()
    }

    /**
     * 将数据库模型转换为领域实体
     */
    private fun User.toEntity(): UserEntity = UserEntity(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatar_url,
        lastLoginAt = Instant.fromEpochMilliseconds(last_login_at),
        isActive = is_active
    )
}
