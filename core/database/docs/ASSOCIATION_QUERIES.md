# SQLDelight 关联查询指南

## 概述

本模块实现了 SQLDelight 的关联查询支持，包括一对多、多对多关系的管理。

## 架构设计

### 1. 关系模型

```
User (1) ----< (N) Project (拥有者)
  |                    |
  |                    |
  (N) --[UserProject]-- (N) 多对多关系
```

### 2. 核心组件

| 组件 | 说明 |
|------|------|
| `ProjectEntity` | 项目表，包含外键 ownerId |
| `UserProjectCrossRef` | 关联表，实现多对多关系 |
| `ProjectMapper` | 处理关系映射和枚举转换 |
| `ProjectDataSource` | 提供关联查询接口 |

## 关联查询类型

### 一对多查询

**场景**: 获取项目及其所有成员

```kotlin
// SQL
selectProjectsWithMembers:
SELECT 
    p.*,
    u.id AS user_id,
    u.username AS user_username,
    -- ... 其他字段
FROM ProjectEntity p
LEFT JOIN UserProjectCrossRef up ON p.id = up.projectId
LEFT JOIN UserEntity u ON up.userId = u.id
WHERE p.id = ?;

// Kotlin 实现
override fun getProjectWithMembers(projectId: String): Flow<ProjectWithMembers?> {
    return queries
        .selectProjectsWithMembers(projectId)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { rows ->
            // 处理一对多关系，将多行合并为对象
            ProjectMapper.toProjectWithMembers(rows)
        }
}
```

### 多对多查询

**场景**: 获取用户参与的所有项目

```kotlin
// SQL
selectProjectsByUser:
SELECT 
    p.*,
    up.role AS member_role,
    up.joinedAt AS member_joinedAt
FROM ProjectEntity p
INNER JOIN UserProjectCrossRef up ON p.id = up.projectId
WHERE up.userId = ?;

// Kotlin 实现
override fun getUserProjects(userId: String): Flow<List<UserProject>> {
    return queries
        .selectProjectsByUser(userId)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { entities ->
            entities.map { ProjectMapper.toUserProject(it) }
        }
}
```

## 最佳实践

### 1. 外键约束

使用外键确保数据完整性：

```sql
CREATE TABLE ProjectEntity (
    id TEXT PRIMARY KEY NOT NULL,
    ownerId TEXT NOT NULL,
    FOREIGN KEY (ownerId) REFERENCES UserEntity(id) ON DELETE CASCADE
);
```

### 2. 级联操作

设置级联删除自动清理关联数据：

```sql
-- 删除项目时自动删除关联记录
FOREIGN KEY (projectId) REFERENCES ProjectEntity(id) ON DELETE CASCADE
```

### 3. 枚举映射

在 Mapper 中处理枚举与字符串的转换：

```kotlin
object ProjectMapper {
    fun toStatus(status: String): ProjectStatus {
        return when (status.lowercase()) {
            "active" -> ProjectStatus.ACTIVE
            "archived" -> ProjectStatus.ARCHIVED
            else -> ProjectStatus.ACTIVE
        }
    }
    
    fun fromStatus(status: ProjectStatus): String {
        return status.name.lowercase()
    }
}
```

### 4. 一对多结果合并

将多行查询结果合并为嵌套对象：

```kotlin
fun toProjectWithMembers(rows: List<SelectProjectsWithMembers>): ProjectWithMembers? {
    if (rows.isEmpty()) return null

    // 第一行包含项目信息
    val project = Project(/* ... */)

    // 后续行包含成员信息
    val members = rows.mapNotNull { row ->
        row.user_id?.let { /* 构建 ProjectMember */ }
    }

    return ProjectWithMembers(project = project, members = members)
}
```

### 5. Flow 组合查询

使用 Flow 操作符组合多个数据源：

```kotlin
fun observeUserProjectsWithOwners(userId: String): Flow<List<ProjectWithOwner>> {
    return projectDataSource.getUserProjects(userId)
        .map { userProjects ->
            userProjects.mapNotNull { userProject ->
                // 查询关联的拥有者信息
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
```

## 性能优化

### 1. 索引

为外键和常用查询字段添加索引：

```sql
CREATE INDEX index_project_owner ON ProjectEntity(ownerId);
CREATE INDEX index_crossref_user ON UserProjectCrossRef(userId);
CREATE INDEX index_crossref_project ON UserProjectCrossRef(projectId);
```

### 2. 按需查询

避免加载不必要的数据：

```kotlin
// 仅查询需要的字段
selectProjectMembers:
SELECT 
    u.id,
    u.username,
    u.avatarUrl,
    up.role
FROM UserEntity u
INNER JOIN UserProjectCrossRef up ON u.id = up.userId
WHERE up.projectId = ?;
```

### 3. 分页查询

对于大数据量使用 LIMIT/OFFSET：

```sql
selectUserProjectsPaginated:
SELECT p.*, up.role
FROM ProjectEntity p
INNER JOIN UserProjectCrossRef up ON p.id = up.projectId
WHERE up.userId = ?
ORDER BY p.updatedAt DESC
LIMIT ? OFFSET ?;
```

## 事务管理

对于需要原子性的多表操作，使用事务：

```kotlin
suspend fun transferOwnership(projectId: String, newOwnerId: String) {
    database.transaction {
        // 1. 更新项目拥有者
        queries.updateProjectOwner(newOwnerId, projectId)
        
        // 2. 更新角色
        queries.updateUserRole("admin", oldOwnerId, projectId)
        queries.insertUserToProject(newOwnerId, projectId, "owner", now)
    }
}
```

## 常见问题

### Q: 如何处理循环依赖？

A: 使用延迟加载或将关联对象拆分为独立查询。

### Q: 大量关联数据如何处理？

A: 使用分页查询，避免一次性加载所有关联数据。

### Q: 枚举类型如何存储？

A: 在数据库中存储为字符串，在 Mapper 中进行转换。

## 总结

通过合理设计表结构、使用外键约束、创建适当的索引，以及利用 Flow 的响应式特性，可以高效地处理 SQLDelight 中的关联查询。
