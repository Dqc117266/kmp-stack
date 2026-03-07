package com.timelinesolutions.kmpstack.core.database.datasource

import com.timelinesolutions.kmpstack.core.database.entity.MemberRole
import com.timelinesolutions.kmpstack.core.database.entity.Project
import com.timelinesolutions.kmpstack.core.database.entity.ProjectMember
import com.timelinesolutions.kmpstack.core.database.entity.ProjectStatus
import com.timelinesolutions.kmpstack.core.database.entity.ProjectWithMembers
import com.timelinesolutions.kmpstack.core.database.entity.UserProject
import kotlinx.coroutines.flow.Flow

/**
 * 项目数据源接口
 * 包含关联查询和关系管理
 */
interface ProjectDataSource {

    // ========== 基本 CRUD ==========

    /**
     * 获取所有项目
     */
    fun getAllProjects(): Flow<List<Project>>

    /**
     * 根据 ID 获取项目
     */
    fun getProjectById(id: String): Flow<Project?>

    /**
     * 根据拥有者获取项目列表
     */
    fun getProjectsByOwner(ownerId: String): Flow<List<Project>>

    /**
     * 根据状态获取项目列表
     */
    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>>

    /**
     * 插入项目
     */
    suspend fun insertProject(project: Project)

    /**
     * 更新项目
     */
    suspend fun updateProject(project: Project)

    /**
     * 删除项目
     */
    suspend fun deleteProject(id: String)

    // ========== 关联查询 ==========

    /**
     * 获取项目及其成员列表（一对多）
     */
    fun getProjectWithMembers(projectId: String): Flow<ProjectWithMembers?>

    /**
     * 获取项目的所有成员
     */
    fun getProjectMembers(projectId: String): Flow<List<ProjectMember>>

    /**
     * 获取用户参与的所有项目（多对多）
     */
    fun getUserProjects(userId: String): Flow<List<UserProject>>

    // ========== 关系管理 ==========

    /**
     * 添加用户到项目
     */
    suspend fun addUserToProject(
        userId: String,
        projectId: String,
        role: MemberRole = MemberRole.MEMBER
    )

    /**
     * 从项目中移除用户
     */
    suspend fun removeUserFromProject(userId: String, projectId: String)

    /**
     * 更新用户在项目中的角色
     */
    suspend fun updateUserRole(
        userId: String,
        projectId: String,
        role: MemberRole
    )
}
