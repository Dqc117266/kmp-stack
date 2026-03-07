package com.dqc.kit.data.network.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 用户响应 DTO
 * 后端返回的原始用户数据结构
 *
 * ⚠️ 此 DTO 仅在模块内部可见，不对外暴露
 */
@Serializable
internal data class UserResponse(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("last_login_at")
    val lastLoginAt: Long? = null,

    @SerialName("is_active")
    val isActive: Boolean = true
)

/**
 * 用户登录响应 DTO
 */
@Serializable
internal data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("expires_in")
    val expiresIn: Long? = null,

    @SerialName("user")
    val user: UserResponse
)

/**
 * 用户注册请求 DTO
 */
@Serializable
internal data class RegisterRequest(
    @SerialName("username")
    val username: String,

    @SerialName("email")
    val email: String,

    @SerialName("password")
    val password: String
)

/**
 * 用户登录请求 DTO
 */
@Serializable
internal data class LoginRequest(
    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String
)

/**
 * 更新用户请求 DTO
 */
@Serializable
internal data class UpdateUserRequest(
    @SerialName("name")
    val name: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

/**
 * 令牌刷新响应 DTO
 */
@Serializable
internal data class RefreshTokenResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String? = null,

    @SerialName("expires_in")
    val expiresIn: Long? = null
)
