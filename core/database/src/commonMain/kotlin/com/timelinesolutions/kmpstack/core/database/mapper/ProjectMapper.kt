package com.timelinesolutions.kmpstack.core.database.mapper

import com.timelinesolutions.kmpstack.core.database.ProjectEntity
import com.timelinesolutions.kmpstack.core.database.SelectProjectMembers
import com.timelinesolutions.kmpstack.core.database.SelectProjectsByUser
import com.timelinesolutions.kmpstack.core.database.SelectProjectsWithMembers
import com.timelinesolutions.kmpstack.core.database.SelectUsersByProject
import com.timelinesolutions.kmpstack.core.database.entity.MemberRole
import com.timelinesolutions.kmpstack.core.database.entity.Project
import com.timelinesolutions.kmpstack.core.database.entity.ProjectMember
import com.timelinesolutions.kmpstack.core.database.entity.ProjectStatus
import com.timelinesolutions.kmpstack.core.database.entity.ProjectWithMembers
import com.timelinesolutions.kmpstack.core.database.entity.User
import com.timelinesolutions.kmpstack.core.database.entity.UserProject
import kotlinx.datetime.Instant

/**
 * 项目实体映射器
 */
object ProjectMapper {

    /**
     * 将数据库字符串转换为项目状态枚举
     */
    fun toStatus(status: String): ProjectStatus {
        return when (status.lowercase()) {
            "active" -> ProjectStatus.ACTIVE
            "archived" -> ProjectStatus.ARCHIVED
            "deleted" -> ProjectStatus.DELETED
            else -> ProjectStatus.ACTIVE
        }
    }

    /**
     * 将项目状态枚举转换为数据库字符串
     */
    fun fromStatus(status: ProjectStatus): String {
        return status.name.lowercase()
    }

    /**
     * 将数据库字符串转换为成员角色枚举
     */
    fun toRole(role: String): MemberRole {
        return when (role.lowercase()) {
            "owner" -> MemberRole.OWNER
            "admin" -> MemberRole.ADMIN
            "member" -> MemberRole.MEMBER
            "viewer" -> MemberRole.VIEWER
            else -> MemberRole.MEMBER
        }
    }

    /**
     * 将成员角色枚举转换为数据库字符串
     */
    fun fromRole(role: MemberRole): String {
        return role.name.lowercase()
    }

    /**
     * 将 ProjectEntity 转换为领域模型
     */
    fun toDomain(entity: ProjectEntity): Project {
        return Project(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            ownerId = entity.ownerId,
            status = toStatus(entity.status),
            createdAt = Instant.fromEpochMilliseconds(entity.createdAt),
            updatedAt = Instant.fromEpochMilliseconds(entity.updatedAt)
        )
    }

    /**
     * 从关联查询结果构建 ProjectWithMembers
     * 处理一对多关系的映射
     */
    fun toProjectWithMembers(rows: List<SelectProjectsWithMembers>): ProjectWithMembers? {
        if (rows.isEmpty()) return null

        // 第一行包含项目信息
        val firstRow = rows.first()
        val project = Project(
            id = firstRow.id,
            name = firstRow.name,
            description = firstRow.description,
            ownerId = firstRow.ownerId,
            status = toStatus(firstRow.status),
            createdAt = Instant.fromEpochMilliseconds(firstRow.createdAt),
            updatedAt = Instant.fromEpochMilliseconds(firstRow.updatedAt)
        )

        // 构建成员列表（过滤掉 null 用户）
        val members = rows.mapNotNull { row ->
            row.user_id?.let { userId ->
                val user = User(
                    id = userId,
                    username = row.user_username ?: "",
                    email = row.user_email ?: "",
                    avatarUrl = row.user_avatarUrl,
                    createdAt = Instant.fromEpochMilliseconds(row.user_createdAt ?: 0),
                    updatedAt = Instant.fromEpochMilliseconds(row.user_updatedAt ?: 0)
                )
                ProjectMember(
                    user = user,
                    role = toRole(row.member_role ?: "member"),
                    joinedAt = Instant.fromEpochMilliseconds(row.member_joinedAt ?: 0)
                )
            }
        }

        return ProjectWithMembers(project = project, members = members)
    }

    /**
     * 从 SelectUsersByProject 构建 ProjectMember
     */
    fun toProjectMember(entity: SelectUsersByProject): ProjectMember {
        val user = User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            avatarUrl = entity.avatarUrl,
            createdAt = Instant.fromEpochMilliseconds(entity.createdAt),
            updatedAt = Instant.fromEpochMilliseconds(entity.updatedAt)
        )
        return ProjectMember(
            user = user,
            role = toRole(entity.member_role),
            joinedAt = Instant.fromEpochMilliseconds(entity.member_joinedAt)
        )
    }

    /**
     * 从 SelectProjectsByUser 构建 UserProject
     */
    fun toUserProject(entity: SelectProjectsByUser): UserProject {
        val project = Project(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            ownerId = entity.ownerId,
            status = toStatus(entity.status),
            createdAt = Instant.fromEpochMilliseconds(entity.createdAt),
            updatedAt = Instant.fromEpochMilliseconds(entity.updatedAt)
        )
        return UserProject(
            project = project,
            role = toRole(entity.member_role),
            joinedAt = Instant.fromEpochMilliseconds(entity.member_joinedAt)
        )
    }

    /**
     * 从 SelectProjectMembers 构建轻量级成员信息
     */
    fun toProjectMemberLight(entity: SelectProjectMembers): ProjectMember {
        val user = User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            avatarUrl = entity.avatarUrl,
            createdAt = Instant.now(), // 轻量级查询不包含完整时间信息
            updatedAt = Instant.now()
        )
        return ProjectMember(
            user = user,
            role = toRole(entity.role),
            joinedAt = Instant.fromEpochMilliseconds(entity.joinedAt)
        )
    }
}
