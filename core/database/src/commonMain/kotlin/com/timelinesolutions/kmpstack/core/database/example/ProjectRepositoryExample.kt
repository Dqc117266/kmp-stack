package com.timelinesolutions.kmpstack.core.database.example

import com.timelinesolutions.kmpstack.core.database.datasource.ProjectDataSource
import com.timelinesolutions.kmpstack.core.database.datasource.UserDataSource
import com.timelinesolutions.kmpstack.core.database.entity.MemberRole
import com.timelinesolutions.kmpstack.core.database.entity.Project
import com.timelinesolutions.kmpstack.core.database.entity.ProjectStatus
import com.timelinesolutions.kmpstack.core.database.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 关联查询使用示例
 * 展示如何在 Repository 层使用 DataSource 进行关联操作
 */
class ProjectRepositoryExample(
    private val userDataSource: UserDataSource,
    private val projectDataSource: ProjectDataSource
) {

    /**
     * 示例 1: 创建项目并关联用户
     * 展示事务性的关联操作
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun createProjectWithOwner(
        name: String,
        description: String?,
        ownerId: String
    ): Result<Project> = runCatching {
        // 验证拥有者存在
        val owner = userDataSource.getUserById(ownerId).first()
            ?: throw IllegalArgumentException("Owner not found")

        // 创建项目
        val project = Project(
            id = Uuid.random().toString(),
            name = name,
            description = description,
            ownerId = ownerId,
            status = ProjectStatus.ACTIVE,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )

        // 插入项目
        projectDataSource.insertProject(project)

        // 将拥有者添加为项目成员（OWNER 角色）
        projectDataSource.addUserToProject(
            userId = ownerId,
            projectId = project.id,
            role = MemberRole.OWNER
        )

        project
    }

    /**
     * 示例 2: 获取项目及其成员
     * 展示一对多关系的查询
     */
    fun observeProjectWithMembers(projectId: String) =
        projectDataSource.getProjectWithMembers(projectId)

    /**
     * 示例 3: 获取用户的所有项目（包括角色信息）
     * 展示多对多关系的查询
     */
    fun observeUserProjects(userId: String) =
        projectDataSource.getUserProjects(userId)

    /**
     * 示例 4: 添加成员到项目
     */
    suspend fun addMemberToProject(
        projectId: String,
        userId: String,
        role: MemberRole
    ): Result<Unit> = runCatching {
        // 验证项目和用户都存在
        val project = projectDataSource.getProjectById(projectId).first()
            ?: throw IllegalArgumentException("Project not found")
        
        val user = userDataSource.getUserById(userId).first()
            ?: throw IllegalArgumentException("User not found")

        // 添加关联
        projectDataSource.addUserToProject(userId, projectId, role)
    }

    /**
     * 示例 5: 复杂查询 - 获取活跃项目及其成员数量
     * 展示组合多个 Flow 的高级用法
     */
    fun observeActiveProjectsWithMemberCount(): Flow<List<ProjectWithMemberCount>> {
        return projectDataSource.getProjectsByStatus(ProjectStatus.ACTIVE)
            .map { projects ->
                projects.map { project ->
                    // 为每个项目查询成员数量
                    val memberCount = projectDataSource
                        .getProjectMembers(project.id)
                        .map { it.size }
                    
                    ProjectWithMemberCount(
                        project = project,
                        memberCountFlow = memberCount
                    )
                }
            }
    }

    /**
     * 示例 6: 组合查询 - 获取用户参与的所有项目的拥有者信息
     * 展示跨表关联的组合查询
     */
    fun observeUserProjectsWithOwners(userId: String): Flow<List<ProjectWithOwner>> {
        return projectDataSource.getUserProjects(userId)
            .map { userProjects ->
                userProjects.mapNotNull { userProject ->
                    // 查询每个项目的拥有者信息
                    val owner = userDataSource
                        .getUserById(userProject.project.ownerId)
                        .first()
                    
                    owner?.let {
                        ProjectWithOwner(
                            project = userProject.project,
                            owner = it,
                            userRole = userProject.role
                        )
                    }
                }
            }
    }

    /**
     * 示例 7: 级联删除 - 删除项目及其所有关联
     * 由于设置了 ON DELETE CASCADE，删除项目会自动删除关联
     */
    suspend fun deleteProject(projectId: String): Result<Unit> = runCatching {
        projectDataSource.deleteProject(projectId)
        // 关联表中的记录会自动删除（外键级联）
    }

    /**
     * 示例 8: 事务性批量操作 - 转移项目拥有者
     */
    suspend fun transferProjectOwnership(
        projectId: String,
        newOwnerId: String
    ): Result<Unit> = runCatching {
        // 获取当前项目
        val project = projectDataSource.getProjectById(projectId).first()
            ?: throw IllegalArgumentException("Project not found")

        // 验证新拥有者存在
        val newOwner = userDataSource.getUserById(newOwnerId).first()
            ?: throw IllegalArgumentException("New owner not found")

        // 1. 更新项目拥有者
        val updatedProject = project.copy(
            ownerId = newOwnerId,
            updatedAt = Clock.System.now()
        )
        projectDataSource.updateProject(updatedProject)

        // 2. 将原拥有者降级为 ADMIN
        projectDataSource.updateUserRole(
            userId = project.ownerId,
            projectId = projectId,
            role = MemberRole.ADMIN
        )

        // 3. 确保新拥有者是项目成员并设置为 OWNER
        projectDataSource.addUserToProject(
            userId = newOwnerId,
            projectId = projectId,
            role = MemberRole.OWNER
        )
    }

    // ========== 辅助数据类 ==========

    data class ProjectWithMemberCount(
        val project: Project,
        val memberCountFlow: Flow<Int>
    )

    data class ProjectWithOwner(
        val project: Project,
        val owner: User,
        val userRole: MemberRole
    )
}
