package com.dqc.kit.data.network.datasource

import com.dqc.kit.data.network.dto.LoginRequest
import com.dqc.kit.data.network.dto.LoginResponse
import com.dqc.kit.data.network.dto.RegisterRequest
import com.dqc.kit.data.network.dto.UpdateUserRequest
import com.dqc.kit.data.network.dto.UserResponse
import com.dqc.kit.data.util.executeAndMap
import com.dqc.kit.data.util.executeAndMapToDomain
import com.dqc.kit.data.util.executeBaseUnit
import com.dqc.kit.domain.entity.UserEntity
import com.dqc.kit.domain.entity.UserSessionEntity
import com.dqc.kit.domain.result.DomainResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * 用户远程数据源
 * 负责用户相关的网络请求
 *
 * @property httpClient Ktor HTTP 客户端
 * @property basePath API 基础路径（默认为 /api/v1）
 */
internal class UserRemoteDataSource(
    private val httpClient: HttpClient,
    private val basePath: String = "/api/v1"
) {

    companion object {
        // API 端点路径
        private const val PATH_LOGIN = "/auth/login"
        private const val PATH_REGISTER = "/auth/register"
        private const val PATH_LOGOUT = "/auth/logout"
        private const val PATH_REFRESH_TOKEN = "/auth/refresh"
        private const val PATH_USERS = "/users"
        private const val PATH_ME = "/users/me"
    }

    /**
     * 用户登录
     */
    suspend fun login(
        username: String,
        password: String
    ): DomainResult<LoginResponse> {
        return httpClient.executeAndMap {
            post("$basePath$PATH_LOGIN") {
                setBody(LoginRequest(username, password))
            }
        }
    }

    /**
     * 用户注册
     */
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): DomainResult<UserResponse> {
        return httpClient.executeAndMap {
            post("$basePath$PATH_REGISTER") {
                setBody(RegisterRequest(username, email, password))
            }
        }
    }

    /**
     * 获取当前登录用户信息
     */
    suspend fun getCurrentUser(): DomainResult<UserResponse> {
        return httpClient.executeAndMap {
            get("$basePath$PATH_ME")
        }
    }

    /**
     * 获取用户详情
     */
    suspend fun getUserById(userId: String): DomainResult<UserResponse> {
        return httpClient.executeAndMap {
            get("$basePath$PATH_USERS/$userId")
        }
    }

    /**
     * 更新用户信息
     */
    suspend fun updateUser(
        userId: String,
        name: String? = null,
        avatarUrl: String? = null
    ): DomainResult<UserResponse> {
        return httpClient.executeAndMap {
            patch("$basePath$PATH_USERS/$userId") {
                setBody(UpdateUserRequest(name, avatarUrl))
            }
        }
    }

    /**
     * 刷新访问令牌
     */
    suspend fun refreshToken(refreshToken: String): DomainResult<String> {
        return httpClient.executeAndMap {
            post("$basePath$PATH_REFRESH_TOKEN") {
                setBody(mapOf("refresh_token" to refreshToken))
            }
        }
    }

    /**
     * 用户登出
     */
    suspend fun logout(): DomainResult<Unit> {
        return httpClient.executeBaseUnit {
            post("$basePath$PATH_LOGOUT")
        }
    }
}
