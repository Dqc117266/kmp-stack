# Database 模块使用指南

## 目录

1. [快速开始](#快速开始)
2. [项目结构](#项目结构)
3. [创建数据库](#创建数据库)
4. [创建 DataSource](#创建-datasource)
5. [关联查询](#关联查询)
6. [事务处理](#事务处理)
7. [分页查询](#分页查询)
8. [最佳实践](#最佳实践)

## 快速开始

### 1. 在功能模块中添加依赖

```kotlin
// feature/my-feature/build.gradle.kts
plugins {
    id("com.dqc.kit.convention.kmp.library")
    alias(libs.plugins.sqldelight) // 添加 SQLDelight 插件
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:database"))

            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
        }
    }
}

sqldelight {
    databases {
        create("FeatureDatabase") {
            packageName.set("com.example.myfeature.data")
        }
    }
}
```

### 2. 定义表结构

```sql
-- src/commonMain/sqldelight/com/example/myfeature/data/TaskEntity.sq
CREATE TABLE TaskEntity (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    is_completed INTEGER NOT NULL DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

selectAll:
SELECT * FROM TaskEntity ORDER BY created_at DESC;

selectById:
SELECT * FROM TaskEntity WHERE id = ?;

selectCompleted:
SELECT * FROM TaskEntity WHERE is_completed = 1;

insert:
INSERT INTO TaskEntity (id, title, description, is_completed, created_at, updated_at)
VALUES (?, ?, ?, ?, ?, ?);

update:
UPDATE TaskEntity SET
    title = ?,
    description = ?,
    is_completed = ?,
    updated_at = ?
WHERE id = ?;

deleteById:
DELETE FROM TaskEntity WHERE id = ?;
```

### 3. 创建 Entity 和 Mapper

```kotlin
// entity/Task.kt
data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

// mapper/TaskMapper.kt
object TaskMapper : BaseMapper<TaskEntity, Task> {
    override fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            isCompleted = entity.is_completed != 0L,
            createdAt = entity.created_at.toInstant(),
            updatedAt = entity.updated_at.toInstant()
        )
    }

    override fun toInsertParams(domain: Task): List<Any?> {
        return listOf(
            domain.id,
            domain.title,
            domain.description,
            if (domain.isCompleted) 1L else 0L,
            domain.createdAt.toMillis(),
            domain.updatedAt.toMillis()
        )
    }

    override fun toUpdateParams(domain: Task): List<Any?> {
        return listOf(
            domain.title,
            domain.description,
            if (domain.isCompleted) 1L else 0L,
            domain.updatedAt.toMillis(),
            domain.id
        )
    }
}
```

### 4. 创建 DataSource

```kotlin
// datasource/TaskDataSource.kt
interface TaskDataSource {
    fun getAllTasks(): Flow<List<Task>>
    fun getTaskById(id: String): Flow<Task?>
    fun getCompletedTasks(): Flow<List<Task>>
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(id: String)
    suspend fun markAsCompleted(id: String)
}

// datasource/TaskDataSourceImpl.kt
class TaskDataSourceImpl(
    database: FeatureDatabase
) : BaseDataSource<FeatureDatabase>(database), TaskDataSource {

    private val queries = database.taskEntityQueries

    override fun getAllTasks(): Flow<List<Task>> = flowList(
        query = queries.selectAll(),
        mapper = TaskMapper::toDomain
    )

    override fun getTaskById(id: String): Flow<Task?> = flowOneOrNull(
        query = queries.selectById(id),
        mapper = TaskMapper::toDomain
    )

    override fun getCompletedTasks(): Flow<List<Task>> = flowList(
        query = queries.selectCompleted(),
        mapper = TaskMapper::toDomain
    )

    override suspend fun insertTask(task: Task) = withTransaction {
        val params = TaskMapper.toInsertParams(task)
        queries.insert(
            params[0] as String,
            params[1] as String,
            params[2] as String?,
            params[3] as Long,
            params[4] as Long,
            params[5] as Long
        )
    }

    override suspend fun updateTask(task: Task) = withTransaction {
        val params = TaskMapper.toUpdateParams(task)
        queries.update(
            params[0] as String,
            params[1] as String?,
            params[2] as Long,
            params[3] as Long,
            params[4] as String
        )
    }

    override suspend fun deleteTask(id: String) = withTransaction {
        queries.deleteById(id)
    }

    override suspend fun markAsCompleted(id: String) = withTransaction {
        val now = Clock.System.now().toMillis()
        queries.updateCompleted(1L, now, id)
    }
}
```

### 5. 配置依赖注入

```kotlin
// di/FeatureModule.kt
val featureModule = module {
    // 数据库
    single {
        val driverFactory = get<DatabaseDriverFactory>()
        val driver = driverFactory.createDriver(
            FeatureDatabase.Schema,
            "feature_database.db"
        )
        FeatureDatabase(driver)
    }

    // DataSource
    single<TaskDataSource> { TaskDataSourceImpl(get()) }

    // Repository
    single { TaskRepository(get()) }
}
```

## 项目结构

推荐的项目结构：

```
feature/my-feature/
├── build.gradle.kts              # SQLDelight 配置
├── src/
│   └── commonMain/
│       ├── kotlin/
│       │   └── com/example/myfeature/
│       │       ├── data/
│       │       │   ├── entity/           # 领域实体
│       │       │   │   └── Task.kt
│       │       │   ├── mapper/           # 实体映射器
│       │       │   │   └── TaskMapper.kt
│       │       │   ├── datasource/       # 数据源
│       │       │   │   ├── TaskDataSource.kt
│       │       │   │   └── TaskDataSourceImpl.kt
│       │       │   └── di/               # 依赖注入
│       │       │       └── FeatureModule.kt
│       │       └── domain/
│       │           └── repository/
│       │               └── TaskRepository.kt
│       └── sqldelight/
│           └── com/example/myfeature/data/
│               └── TaskEntity.sq         # SQLDelight 表定义
```

## 创建数据库

### 基础表定义

```sql
-- 用户表
CREATE TABLE UserEntity (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    avatar_url TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

-- 创建索引
CREATE INDEX index_user_email ON UserEntity(email);
```

### 关联表（多对多）

```sql
-- 文章表
CREATE TABLE PostEntity (
    id TEXT PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    author_id TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    FOREIGN KEY (author_id) REFERENCES UserEntity(id) ON DELETE CASCADE
);

-- 标签表
CREATE TABLE TagEntity (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL UNIQUE
);

-- 文章-标签关联表
CREATE TABLE PostTagCrossRef (
    post_id TEXT NOT NULL,
    tag_id TEXT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES PostEntity(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES TagEntity(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX index_crossref_post ON PostTagCrossRef(post_id);
CREATE INDEX index_crossref_tag ON PostTagCrossRef(tag_id);
```

## 创建 DataSource

### 基本 CRUD

```kotlin
interface UserDataSource {
    fun getAll(): Flow<List<User>>
    fun getById(id: String): Flow<User?>
    fun getByEmail(email: String): Flow<User?>
    suspend fun insert(user: User)
    suspend fun update(user: User)
    suspend fun delete(id: String)
}

class UserDataSourceImpl(database: MyDatabase)
    : BaseDataSource<MyDatabase>(database), UserDataSource {

    private val queries = database.userEntityQueries

    override fun getAll(): Flow<List<User>> =
        flowList(queries.selectAll(), UserMapper::toDomain)

    override fun getById(id: String): Flow<User?> =
        flowOneOrNull(queries.selectById(id), UserMapper::toDomain)

    override fun getByEmail(email: String): Flow<User?> =
        flowOneOrNull(queries.selectByEmail(email), UserMapper::toDomain)

    override suspend fun insert(user: User) = withTransaction {
        queries.insert(...)
    }

    override suspend fun update(user: User) = withTransaction {
        queries.update(...)
    }

    override suspend fun delete(id: String) = withTransaction {
        queries.deleteById(id)
    }
}
```

### 使用事务帮助类

```kotlin
class BatchDataSource(database: MyDatabase)
    : BaseDataSource<MyDatabase>(database) {

    private val queries = database.myEntityQueries
    private val transactionHelper = TransactionHelper(database)

    suspend fun batchInsert(items: List<MyEntity>) {
        transactionHelper.batchInsert(items) { item ->
            queries.insert(...)
        }
    }

    suspend fun batchUpdate(items: List<MyEntity>) {
        transactionHelper.batchUpdate(items) { item ->
            queries.update(...)
        }
    }

    suspend fun deleteMultiple(ids: List<String>) {
        transactionHelper.batchDelete(ids) { id ->
            queries.deleteById(id)
        }
    }
}
```

## 关联查询

### 一对多查询

```sql
-- 查询用户及其所有文章
selectUserWithPosts:
SELECT
    u.*,
    p.id AS post_id,
    p.title AS post_title,
    p.content AS post_content,
    p.created_at AS post_created_at
FROM UserEntity u
LEFT JOIN PostEntity p ON u.id = p.author_id
WHERE u.id = ?;
```

```kotlin
// Mapper
fun toUserWithPosts(rows: List<SelectUserWithPosts>): UserWithPosts? {
    if (rows.isEmpty()) return null

    val firstRow = rows.first()
    val user = User(
        id = firstRow.id,
        name = firstRow.name,
        email = firstRow.email,
        // ...
    )

    val posts = rows.mapNotNull { row ->
        row.post_id?.let {
            Post(
                id = it,
                title = row.post_title ?: "",
                content = row.post_content ?: "",
                // ...
            )
        }
    }

    return UserWithPosts(user, posts)
}

// DataSource
fun getUserWithPosts(userId: String): Flow<UserWithPosts?> {
    return queries
        .selectUserWithPosts(userId)
        .asFlow()
        .mapToList(dispatcher)
        .map { MyMapper.toUserWithPosts(it) }
}
```

### 多对多查询

```sql
-- 查询文章及其所有标签
selectPostWithTags:
SELECT
    p.*,
    t.id AS tag_id,
    t.name AS tag_name
FROM PostEntity p
LEFT JOIN PostTagCrossRef pt ON p.id = pt.post_id
LEFT JOIN TagEntity t ON pt.tag_id = t.id
WHERE p.id = ?;
```

```kotlin
fun getPostWithTags(postId: String): Flow<PostWithTags?> {
    return queries
        .selectPostWithTags(postId)
        .asFlow()
        .mapToList(dispatcher)
        .map { MyMapper.toPostWithTags(it) }
}
```

## 事务处理

### 简单事务

```kotlin
suspend fun transferOwnership(projectId: String, newOwnerId: String) {
    withTransaction {
        // 1. 更新项目拥有者
        queries.updateOwner(newOwnerId, projectId)

        // 2. 将原拥有者降级
        queries.updateRole("ADMIN", oldOwnerId, projectId)

        // 3. 添加新拥有者为成员
        queries.insertMember(newOwnerId, projectId, "OWNER", now())
    }
}
```

### 复杂事务

```kotlin
suspend fun createProjectWithMembers(
    project: Project,
    members: List<String>
) {
    database.transactionWithResult {
        // 插入项目
        queries.insertProject(...)

        // 批量插入成员
        members.forEach { userId ->
            queries.insertMember(userId, project.id, "MEMBER", now())
        }

        // 创建默认设置
        queries.insertProjectSettings(project.id, ...)
    }
}
```

## 分页查询

```kotlin
class PaginatedDataSource(database: MyDatabase)
    : BaseDataSource<MyDatabase>(database) {

    private val queries = database.myEntityQueries

    fun getPaginated(params: PaginationParams): Flow<PagedResult<Item>> {
        return combine(
            queries.selectPage(params.pageSize.toLong(), params.offset.toLong())
                .asFlow()
                .mapToList(dispatcher),
            queries.count().asFlow().mapToOneOrNull(dispatcher)
        ) { items, count ->
            val total = count ?: 0L
            PagedResult(
                data = items.map { ItemMapper.toDomain(it) },
                page = params.page,
                pageSize = params.pageSize,
                totalCount = total,
                hasMore = (params.offset + items.size) < total
            )
        }
    }
}
```

```sql
-- 分页查询
selectPage:
SELECT * FROM ItemEntity
ORDER BY created_at DESC
LIMIT ? OFFSET ?;

-- 总数查询
count:
SELECT COUNT(*) FROM ItemEntity;
```

## 最佳实践

### 1. 使用领域实体

```kotlin
// ✅ 正确：在 DataSource 中使用领域实体
data class User(
    val id: String,
    val name: String,
    val email: String
)

interface UserDataSource {
    fun getById(id: String): Flow<User?>  // 返回领域实体
}

// ❌ 错误：直接暴露数据库实体
interface UserDataSource {
    fun getById(id: String): Flow<UserEntity?>  // 不要这样做
}
```

### 2. 处理时间戳

```kotlin
object TimeUtils {
    fun Long.toInstant(): Instant =
        Instant.fromEpochMilliseconds(this)

    fun Instant.toMillis(): Long =
        this.toEpochMilliseconds()
}

// 使用
val createdAt = entity.created_at.toInstant()
val millis = domain.createdAt.toMillis()
```

### 3. 错误处理

```kotlin
class SafeDataSource(database: MyDatabase)
    : BaseDataSource<MyDatabase>(database) {

    suspend fun <T> safeExecute(
        block: suspend () -> T
    ): Result<T> = runCatching {
        withTransaction { block() }
    }.onFailure { error ->
        // 记录错误日志
        Log.e("Database", "Operation failed", error)
    }
}
```

### 4. 使用索引

```sql
-- 为常用查询字段创建索引
CREATE INDEX index_user_email ON UserEntity(email);
CREATE INDEX index_post_author ON PostEntity(author_id);
CREATE INDEX index_post_created ON PostEntity(created_at);
```

### 5. 数据库迁移

```kotlin
// 在 sqldelight 配置中启用迁移验证
sqldelight {
    databases {
        create("MyDatabase") {
            // ...
            verifyMigrations.set(true)
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}
```

创建迁移文件：
```
src/commonMain/sqldelight/databases/
├── 1.db      # 版本 1 的 schema
├── 2.db      # 版本 2 的 schema
└── 2.sqm     # 从 1 到 2 的迁移脚本
```

迁移脚本示例 (2.sqm)：
```sql
ALTER TABLE UserEntity ADD COLUMN phone TEXT;
CREATE INDEX index_user_phone ON UserEntity(phone);
```

### 6. 测试

```kotlin
class MyDataSourceTest {

    private lateinit var database: MyDatabase
    private lateinit var dataSource: MyDataSource

    @BeforeTest
    fun setup() {
        // 使用内存数据库
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        MyDatabase.Schema.create(driver)
        database = MyDatabase(driver)
        dataSource = MyDataSourceImpl(database)
    }

    @Test
    fun testInsertAndGet() = runTest {
        val entity = createTestEntity()
        dataSource.insert(entity)

        val result = dataSource.getById(entity.id).first()
        assertEquals(entity, result)
    }
}
```

## 常见问题

### Q: 如何处理外键约束？

```sql
-- 定义外键时添加级联操作
CREATE TABLE PostEntity (
    id TEXT PRIMARY KEY NOT NULL,
    author_id TEXT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES UserEntity(id)
        ON DELETE CASCADE  -- 用户删除时自动删除文章
        ON UPDATE CASCADE  -- 用户 ID 更新时自动更新
);
```

### Q: 如何处理枚举类型？

```kotlin
// Mapper 中转换
fun fromStatus(status: String): TaskStatus = when (status) {
    "pending" -> TaskStatus.PENDING
    "in_progress" -> TaskStatus.IN_PROGRESS
    "completed" -> TaskStatus.COMPLETED
    else -> TaskStatus.PENDING
}

fun toStatus(status: TaskStatus): String = status.name.lowercase()
```

### Q: 如何执行原始 SQL？

```kotlin
// 在 DataSource 中
fun rawQuery(sql: String): List<Map<String, Any?>> {
    return database.rawQuery(sql) { cursor ->
        // 处理结果
    }
}
```

## 总结

使用 core:database 模块的优势：

1. **统一的基础设施**：跨平台的数据库驱动工厂
2. **减少样板代码**：BaseDataSource 提供通用操作封装
3. **类型安全**：通过 SQLDelight 生成类型安全的代码
4. **响应式编程**：内置 Flow 支持
5. **事务支持**：简化复杂事务处理

开始创建你的数据库吧！
