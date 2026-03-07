package com.timelinesolutions.kmpstack.core.database.entity

import kotlinx.datetime.Instant

/**
 * 项目状态枚举
 */
enum class ProjectStatus {
    ACTIVE,
    ARCHIVED,
    DELETED
}

/**
 * 成员角色枚举
 */
enum class MemberRole {
    OWNER,
    ADMIN,
    MEMBER,
    VIEWER
}

/**
 * 项目领域实体
 */
data class Project(
    val id: String,
    val name: String,
    val description: String?,
    val ownerId: String,
    val status: ProjectStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)

/**
 * 项目成员关联
 * 表示用户与项目的关系
 */
data class ProjectMember(
    val user: User,
    val role: MemberRole,
    val joinedAt: Instant
)

/**
 * 用户参与的项目
 * 表示项目与用户的关系
 */
data class UserProject(
    val project: Project,
    val role: MemberRole,
    val joinedAt: Instant
)

/**
 * 项目详情（包含成员列表）
 */
data class ProjectWithMembers(
    val project: Project,
    val members: List<ProjectMember>
)

/**
 * 用户详情（包含参与的项目列表）
 */
data class UserWithProjects(
    val user: User,
    val projects: List<UserProject>
)
