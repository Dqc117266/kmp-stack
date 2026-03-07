package com.dqc.kit.domain.entity

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * 用户业务实体
 * 纯粹的业务模型，与 DTO 和网络层完全解耦
 *
 * @property id 用户唯一标识
 * @property name 用户名
 * @property email 邮箱地址
 * @property avatarUrl 头像 URL（可选）
 * @property lastLoginAt 最后登录时间
 * @property isActive 账户是否激活
 */
data class UserEntity(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val lastLoginAt: Instant,
    val isActive: Boolean = true
) {
    /**
     * 用户邮箱的显示名（@ 之前的部分）
     */
    val emailDisplayName: String
        get() = email.substringBefore("@")

    /**
     * 用户是否为有效用户
     */
    val isValid: Boolean
        get() = id.isNotBlank() && name.isNotBlank() && email.isNotBlank()

    companion object {
        /**
         * 创建空用户（用于占位）
         */
        fun empty(): UserEntity = UserEntity(
            id = "",
            name = "",
            email = "",
            lastLoginAt = Instant.fromEpochMilliseconds(0)
        )
    }
}

/**
 * 用户会话实体
 * 包含登录凭证和会话信息
 *
 * @property accessToken 访问令牌
 * @property refreshToken 刷新令牌（可选）
 * @property expiresAt 令牌过期时间（可选）
 * @property user 用户信息
 */
data class UserSessionEntity(
    val accessToken: String,
    val refreshToken: String? = null,
    val expiresAt: Instant? = null,
    val user: UserEntity
) {
    /**
     * 令牌是否已过期
     */
    fun isTokenExpired(): Boolean {
        return expiresAt?.let { Clock.System.now() >= it } ?: false
    }

    /**
     * 是否有效会话
     */
    val isValid: Boolean
        get() = accessToken.isNotBlank() && user.isValid
}
