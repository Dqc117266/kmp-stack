package com.timelinesolutions.kmpstack.core.database.datasource

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.timelinesolutions.kmpstack.core.database.AppDatabase
import com.timelinesolutions.kmpstack.core.database.entity.MemberRole
import com.timelinesolutions.kmpstack.core.database.entity.Project
import com.timelinesolutions.kmpstack.core.database.entity.ProjectMember
import com.timelinesolutions.kmpstack.core.database.entity.ProjectStatus
import com.timelinesolutions.kmpstack.core.database.entity.ProjectWithMembers
import com.timelinesolutions.kmpstack.core.database.entity.UserProject
import com.timelinesolutions.kmpstack.core.database.mapper.ProjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * 项目数据源实现
 * 支持关联查询和事务操作
 */
class ProjectDataSourceImpl(
    database: AppDatabase
) : ProjectDataSource {

    private val queries = database.projectEntityQueries

    // ========== 基本 CRUD ==========

    override fun getAllProjects(): Flow<List<Project>> {
        return queries
            .selectAllProjects()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { ProjectMapper.toDomain(it) }
            }
    }

    override fun getProjectById(id: String): Flow<Project?> {
        return queries
            .selectProjectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.let { ProjectMapper.toDomain(it) } }
    }

    override fun getProjectsByOwner(ownerId: String): Flow<List<Project>> {
        return queries
            .selectProjectsByOwner(ownerId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { ProjectMapper.toDomain(it) }
            }
    }

    override fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> {
        return queries
            .selectProjectsByStatus(ProjectMapper.fromStatus(status))
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { ProjectMapper.toDomain(it) }
            }
    }

    override suspend fun insertProject(project: Project) {
        withContext(Dispatchers.IO) {
            queries.insertProject(
                id = project.id,
                name = project.name,
                description = project.description,
                ownerId = project.ownerId,
                status = ProjectMapper.fromStatus(project.status),
                createdAt = project.createdAt.toEpochMilliseconds(),
                updatedAt = project.updatedAt.toEpochMilliseconds()
            )
        }
    }

    override suspend fun updateProject(project: Project) {
        withContext(Dispatchers.IO) {
            queries.updateProject(
                name = project.name,
                description = project.description,
                ownerId = project.ownerId,
                status = ProjectMapper.fromStatus(project.status),
                updatedAt = project.updatedAt.toEpochMilliseconds(),
                id = project.id
            )
        }
    }

    override suspend fun deleteProject(id: String) {
        withContext(Dispatchers.IO) {
            queries.deleteProject(id)
        }
    }

    // ========== 关联查询 ==========

    override fun getProjectWithMembers(projectId: String): Flow<ProjectWithMembers?> {
        return queries
            .selectProjectsWithMembers(projectId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                // 处理一对多关系，将多行结果合并为 ProjectWithMembers
                ProjectMapper.toProjectWithMembers(rows)
            }
    }

    override fun getProjectMembers(projectId: String): Flow<List<ProjectMember>> {
        return queries
            .selectProjectMembers(projectId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { ProjectMapper.toProjectMemberLight(it) }
            }
    }

    override fun getUserProjects(userId: String): Flow<List<UserProject>> {
        return queries
            .selectProjectsByUser(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { ProjectMapper.toUserProject(it) }
            }
    }

    // ========== 关系管理 ==========

    override suspend fun addUserToProject(
        userId: String,
        projectId: String,
        role: MemberRole
    ) {
        withContext(Dispatchers.IO) {
            queries.insertUserToProject(
                userId = userId,
                projectId = projectId,
                role = ProjectMapper.fromRole(role),
                joinedAt = Clock.System.now().toEpochMilliseconds()
            )
        }
    }

    override suspend fun removeUserFromProject(userId: String, projectId: String) {
        withContext(Dispatchers.IO) {
            queries.removeUserFromProject(userId, projectId)
        }
    }

    override suspend fun updateUserRole(
        userId: String,
        projectId: String,
        role: MemberRole
    ) {
        withContext(Dispatchers.IO) {
            queries.updateUserRole(
                role = ProjectMapper.fromRole(role),
                userId = userId,
                projectId = projectId
            )
        }
    }
}
