package com.dqc.kit.data.repository

import com.dqc.kit.data.local.database.datasource.UserLocalDataSource
import com.dqc.kit.data.network.datasource.UserRemoteDataSource
import com.dqc.kit.data.network.mapper.toDomain
import com.dqc.kit.domain.entity.UserEntity
import com.dqc.kit.domain.entity.UserSessionEntity
import com.dqc.kit.domain.repository.UserRepository
import com.dqc.kit.domain.result.DomainResult
import com.dqc.kit.logging.Log
import com.dqc.kit.network.auth.AuthManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 用户仓库实现
 *
 * 职责：
 * 1. 协调远程和本地数据源
 * 2. 处理 DTO 到领域实体的转换
 * 3. 管理数据缓存策略
 * 4. 统一错误处理
 *
 * @property remoteDataSource 远程数据源（网络）
 * @property localDataSource 本地数据源（数据库）
 * @property authManager 认证管理器
 */
internal class UserRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val authManager: AuthManager
) : UserRepository {

    companion object {
        private const val TAG = "UserRepository"
    }

    /**
     * 用户登录
     */
    override suspend fun login(
        username: String,
        password: String
    ): DomainResult<UserSessionEntity> {
        Log.tag(TAG).d("Login attempt for user: $username")

        return when (val result = remoteDataSource.login(username, password)) {
            is DomainResult.Success -> {
                val session = result.data.toDomain()

                // 保存 Token
                if (session.refreshToken != null) {
                    authManager.loginWithTokens(
                        session.accessToken,
                        session.refreshToken,
                        session.expiresAt?.let {
                            (it.toEpochMilliseconds() - kotlinx.datetime.Clock.System.now().toEpochMilliseconds()) / 1000
                        } ?: 3600
                    )
                } else {
                    authManager.loginWithTokens(session.accessToken, null)
                }

                // 缓存用户信息
                localDataSource.saveUser(session.user)

                Log.tag(TAG).d(TAG) { "Login successful for user: ${session.user.id}" }
                DomainResult.Success(session)
            }

            is DomainResult.Error -> {
                Log.tag(TAG).w(TAG) { "Login failed: ${result.error.message}" }
                result
            }
        }
    }

    /**
     * 用户注册
     */
    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): DomainResult<UserEntity> {
        Log.tag(TAG).d("Register attempt for user: $username")

        return when (val result = remoteDataSource.register(username, email, password)) {
            is DomainResult.Success -> {
                val user = result.data.toDomain()
                localDataSource.saveUser(user)
                Log.tag(TAG).i(TAG) { "Registration successful for user: ${user.id}" }
                DomainResult.Success(user)
            }

            is DomainResult.Error -> {
                Log.tag(TAG).w(TAG) { "Registration failed: ${result.error.message}" }
                result
            }
        }
    }

    /**
     * 获取当前登录用户
     * 优先从本地获取，本地没有则请求网络
     */
    override suspend fun getCurrentUser(): DomainResult<UserEntity> {
        // 1. 尝试从本地获取
        localDataSource.getCurrentUser()?.let {
            Log.tag(TAG).d(TAG) { "Returning cached current user: ${it.id}" }
            return DomainResult.Success(it)
        }

        // 2. 检查是否已登录
        if (!authManager.isAuthenticated()) {
            Log.tag(TAG).w(TAG) { "No authenticated user found" }
            return DomainResult.Error(
                com.dqc.kit.domain.result.DomainError.Unauthorized("Not logged in")
            )
        }

        // 3. 从网络获取并缓存
        Log.tag(TAG).d(TAG) { "Fetching current user from network" }
        return when (val result = remoteDataSource.getCurrentUser()) {
            is DomainResult.Success -> {
                val user = result.data.toDomain()
                localDataSource.saveUser(user)
                DomainResult.Success(user)
            }

            is DomainResult.Error -> result
        }
    }

    /**
     * 观察当前用户变化
     */
    override fun observeCurrentUser(): Flow<UserEntity> {
        return localDataSource.observeCurrentUser()
            .map { it ?: UserEntity.empty() }
    }

    /**
     * 更新用户信息
     */
    override suspend fun updateUser(user: UserEntity): DomainResult<UserEntity> {
        Log.tag(TAG).d(TAG) { "Updating user: ${user.id}" }

        return when (val result = remoteDataSource.updateUser(
            userId = user.id,
            name = user.name,
            avatarUrl = user.avatarUrl
        )) {
            is DomainResult.Success -> {
                val updatedUser = result.data.toDomain()
                localDataSource.saveUser(updatedUser)
                Log.tag(TAG).i(TAG) { "User updated successfully: ${updatedUser.id}" }
                DomainResult.Success(updatedUser)
            }

            is DomainResult.Error -> {
                Log.tag(TAG).w(TAG) { "User update failed: ${result.error.message}" }
                result
            }
        }
    }

    /**
     * 刷新访问令牌
     */
    override suspend fun refreshToken(): DomainResult<String> {
        Log.tag(TAG).d(TAG) { "Refreshing access token" }

        return try {
            val newToken = authManager.refreshToken()
            if (newToken != null) {
                Log.tag(TAG).i(TAG) { "Token refreshed successfully" }
                DomainResult.Success(newToken)
            } else {
                Log.tag(TAG).w(TAG) { "Token refresh failed" }
                DomainResult.Error(
                    com.dqc.kit.domain.result.DomainError.Unauthorized("Failed to refresh token")
                )
            }
        } catch (e: Exception) {
            Log.tag(TAG).e("Token refresh error", e)
            DomainResult.Error(
                com.dqc.kit.domain.result.DomainError.Unauthorized(e.message ?: "Token refresh failed")
            )
        }
    }

    /**
     * 用户登出
     */
    override suspend fun logout(): DomainResult<Unit> {
        Log.tag(TAG).d(TAG) { "Logging out user" }

        // 1. 调用远程登出（忽略失败）
        remoteDataSource.logout()

        // 2. 清除本地 Token
        authManager.logout()

        // 3. 清除本地用户数据
        localDataSource.deleteAllUsers()

        Log.tag(TAG).i(TAG) { "User logged out successfully" }
        return DomainResult.Success(Unit)
    }

    /**
     * 检查用户是否已登录
     */
    override suspend fun isLoggedIn(): Boolean {
        return authManager.isAuthenticated()
    }

    /**
     * 获取用户详情
     */
    override suspend fun getUserById(userId: String): DomainResult<UserEntity> {
        Log.tag(TAG).d("Getting user by ID: $userId")

        // 1. 尝试从本地获取
        localDataSource.getUserById(userId)?.let {
            return DomainResult.Success(it)
        }

        // 2. 从网络获取并缓存
        return when (val result = remoteDataSource.getUserById(userId)) {
            is DomainResult.Success -> {
                val user = result.data.toDomain()
                localDataSource.saveUser(user)
                DomainResult.Success(user)
            }

            is DomainResult.Error -> result
        }
    }
}
