package com.dqc.kit.data.network.mapper

import com.dqc.kit.data.network.dto.LoginResponse
import com.dqc.kit.data.network.dto.RefreshTokenResponse
import com.dqc.kit.data.network.dto.UserResponse
import com.dqc.kit.domain.entity.UserEntity
import com.dqc.kit.domain.entity.UserSessionEntity
import kotlinx.datetime.Instant

/**
 * 用户数据映射器
 * 将网络 DTO 转换为领域实体
 *
 * ⚠️ 所有映射函数都是内部可见，不对外暴露
 */

/**
 * 将 UserResponse DTO 转换为 UserEntity
 */
internal fun UserResponse.toDomain(): = UserEntity(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    lastLoginAt = lastLoginAt?.let { Instant.fromEpochMilliseconds(it) } ?: Instant.DISTANT_PAST,
    isActive = isActive
)

/**
 * 将 LoginResponse DTO 转换为 UserSessionEntity
 */
internal fun LoginResponse.toDomain(): UserSessionEntity {
    return UserSessionEntity(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresAt = expiresIn?.let {
            Instant.fromEpochMilliseconds(
                kotlinx.datetime.Clock.System.now().toEpochMilliseconds() + (it * 1000)
            )
        },
        user = user.toDomain()
    )
}

/**
 * 将 RefreshTokenResponse 转换为 UserSessionEntity
 * 需要传入现有的用户信息
 */
internal fun RefreshTokenResponse.toDomain(
    existingUser: UserEntity
): UserSessionEntity {
    return UserSessionEntity(
        accessToken = accessToken,
        refreshToken = refreshToken,
        expiresAt = expiresIn?.let {
            Instant.fromEpochMilliseconds(
                kotlinx.datetime.Clock.System.now().toEpochMilliseconds() + (it * 1000)
            )
        },
        user = existingUser
    )
}

/**
 * 将 UserResponse 列表转换为 UserEntity 列表
 */
internal fun List<UserResponse>.toDomainList(): List<UserEntity> = map { it.toDomain() }
