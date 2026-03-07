package com.dqc.kit.domain.repository

import com.dqc.kit.domain.entity.UserEntity
import com.dqc.kit.domain.entity.UserSessionEntity
import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据仓库接口
 * 定义用户相关的数据操作契约
 *
 * 实现说明：
 * - 由 data 层（core:network、core:datastore）具体实现
 * - 返回 DomainResult，将网络错误转换为领域错误
 */
interface UserRepository {

    /**
     * 用户登录
     * @param username 用户名/邮箱
     * @param password 密码
     * @return 登录结果，包含会话信息
     */
    suspend fun login(username: String, password: String): DomainResult<UserSessionEntity>

    /**
     * 用户注册
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @return 注册结果，包含新创建的用户信息
     */
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): DomainResult<UserEntity>

    /**
     * 获取当前登录用户
     * @return 当前用户或错误
     */
    suspend fun getCurrentUser(): DomainResult<UserEntity>

    /**
     * 观察当前用户变化
     * @return 用户数据流
     */
    fun observeCurrentUser(): Flow<UserEntity>

    /**
     * 更新用户信息
     * @param user 更新后的用户实体
     * @return 更新结果
     */
    suspend fun updateUser(user: UserEntity): DomainResult<UserEntity>

    /**
     * 刷新访问令牌
     * @return 新令牌或错误
     */
    suspend fun refreshToken(): DomainResult<String>

    /**
     * 用户登出
     * 清除本地存储的凭证
     */
    suspend fun logout(): DomainResult<Unit>

    /**
     * 检查用户是否已登录
     * @return true 如果存在有效会话
     */
    suspend fun isLoggedIn(): Boolean

    /**
     * 获取用户详情
     * @param userId 用户 ID
     * @return 用户详情
     */
    suspend fun getUserById(userId: String): DomainResult<UserEntity>
}
