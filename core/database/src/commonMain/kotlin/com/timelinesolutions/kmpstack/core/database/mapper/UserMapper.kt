package com.timelinesolutions.kmpstack.core.database.mapper

import com.timelinesolutions.kmpstack.core.database.UserEntity
import com.timelinesolutions.kmpstack.core.database.entity.User
import kotlinx.datetime.Instant

/**
 * 用户实体映射器
 * 负责数据库模型和领域模型之间的转换
 */
object UserMapper {

    /**
     * 将 SQLDelight 生成的 UserEntity 转换为领域模型 User
     */
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            avatarUrl = entity.avatarUrl,
            createdAt = Instant.fromEpochMilliseconds(entity.createdAt),
            updatedAt = Instant.fromEpochMilliseconds(entity.updatedAt)
        )
    }

    /**
     * 将领域模型 User 转换为 SQLDelight 需要的参数列表
     */
    fun toDatabaseParams(user: User): List<Any?> {
        return listOf(
            user.id,
            user.username,
            user.email,
            user.avatarUrl,
            user.createdAt.toEpochMilliseconds(),
            user.updatedAt.toEpochMilliseconds()
        )
    }
}
