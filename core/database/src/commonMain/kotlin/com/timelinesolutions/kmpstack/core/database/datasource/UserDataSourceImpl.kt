package com.timelinesolutions.kmpstack.core.database.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.timelinesolutions.kmpstack.core.database.AppDatabase
import com.timelinesolutions.kmpstack.core.database.entity.User
import com.timelinesolutions.kmpstack.core.database.mapper.UserMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * 用户数据源实现
 * 使用 SQLDelight 进行数据库操作
 */
class UserDataSourceImpl(
    database: AppDatabase
) : UserDataSource {

    private val queries = database.userEntityQueries

    override fun getAllUsers(): Flow<List<User>> {
        return queries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { UserMapper.toDomain(it) }
            }
    }

    override fun getUserById(id: String): Flow<User?> {
        return queries
            .selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.let { UserMapper.toDomain(it) } }
    }

    override fun getUserByEmail(email: String): Flow<User?> {
        return queries
            .selectByEmail(email)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.let { UserMapper.toDomain(it) } }
    }

    override suspend fun insertOrReplaceUser(user: User) {
        withContext(Dispatchers.IO) {
            queries.insertOrReplace(
                id = user.id,
                username = user.username,
                email = user.email,
                avatarUrl = user.avatarUrl,
                createdAt = user.createdAt.toEpochMilliseconds(),
                updatedAt = user.updatedAt.toEpochMilliseconds()
            )
        }
    }

    override suspend fun insertUser(user: User) {
        withContext(Dispatchers.IO) {
            queries.insert(
                id = user.id,
                username = user.username,
                email = user.email,
                avatarUrl = user.avatarUrl,
                createdAt = user.createdAt.toEpochMilliseconds(),
                updatedAt = user.updatedAt.toEpochMilliseconds()
            )
        }
    }

    override suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            queries.update(
                username = user.username,
                email = user.email,
                avatarUrl = user.avatarUrl,
                updatedAt = user.updatedAt.toEpochMilliseconds(),
                id = user.id
            )
        }
    }

    override suspend fun deleteUserById(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteById(id)
        }
    }

    override suspend fun deleteAllUsers() {
        withContext(Dispatchers.IO) {
            queries.deleteAll()
        }
    }

    override fun getUserCount(): Flow<Long> {
        return queries
            .count()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it ?: 0L }
    }
}
