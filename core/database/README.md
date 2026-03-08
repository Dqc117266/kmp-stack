# Core Database 模块

通用的数据库基础设施模块，提供跨平台的数据库驱动工厂和数据库操作工具。

## 设计目标

此模块只提供**通用的数据库基础设施**，不包含任何业务相关的实体或数据源实现。其他模块可以基于这些基础设施轻松创建自己的数据库。

## 提供的功能

### 1. DatabaseDriverFactory
跨平台的数据库驱动工厂，支持 Android 和 iOS。

```kotlin
// 在其他模块中创建数据库
val driverFactory = DatabaseDriverFactory(context) // Android 需要 context
val driver = driverFactory.createDriver(MyDatabase.Schema, "my_database.db")
val database = MyDatabase(driver)
```

### 2. BaseDataSource
数据源基类，提供通用的数据库操作封装。

```kotlin
class MyDataSource(database: MyDatabase) : BaseDataSource<MyDatabase>(database) {

    private val queries = database.myEntityQueries

    // 使用基类提供的 flowList 方法
    fun getAll(): Flow<List<MyEntity>> = flowList(
        query = queries.selectAll(),
        mapper = { it.toDomain() }
    )

    // 使用基类提供的 flowOneOrNull 方法
    fun getById(id: String): Flow<MyEntity?> = flowOneOrNull(
        query = queries.selectById(id),
        mapper = { it.toDomain() }
    )

    // 使用基类提供的事务支持
    suspend fun insert(entity: MyEntity) = withTransaction {
        queries.insert(...)
    }
}
```

### 3. BaseMapper
实体映射器接口，规范数据库模型和领域模型之间的转换。

```kotlin
object MyMapper : BaseMapper<MyEntityDb, MyEntity> {
    override fun toDomain(entity: MyEntityDb): MyEntity {
        return MyEntity(
            id = entity.id,
            name = entity.name,
            createdAt = entity.createdAt.toInstant() // 使用 TimeUtils
        )
    }

    override fun toInsertParams(domain: MyEntity): List<Any?> {
        return listOf(domain.id, domain.name, domain.createdAt.toMillis())
    }

    override fun toUpdateParams(domain: MyEntity): List<Any?> {
        return listOf(domain.name, domain.updatedAt.toMillis(), domain.id)
    }
}
```

### 4. TransactionHelper
事务帮助类，提供批量操作和复杂事务支持。

```kotlin
val transactionHelper = TransactionHelper(database)

// 批量插入
transactionHelper.batchInsert(items) { item ->
    queries.insert(item.id, item.name, ...)
}

// 批量更新
transactionHelper.batchUpdate(items) { item ->
    queries.update(item.name, ..., item.id)
}

// 执行多个操作的事务
transactionHelper.executeInTransaction(
    { queries.insert(...) },
    { queries.update(...) },
    { queries.delete(...) }
)
```

### 5. DatabaseConfig
数据库配置类。

```kotlin
val config = DatabaseConfig(
    name = "my_app.db",
    version = 1,
    enableWal = true,
    maxSize = 100 * 1024 * 1024 // 100MB
)
```

## 在其他模块中使用

### 步骤 1: 添加依赖

在需要使用数据库的模块的 `build.gradle.kts` 中添加：

```kotlin
dependencies {
    implementation(project(":core:database"))

    // SQLDelight 插件（用于生成数据库）
    alias(libs.plugins.sqldelight)
}

// 配置 SQLDelight
sqldelight {
    databases {
        create("MyDatabase") {
            packageName.set("com.example.myapp.data")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
            generateAsync.set(true)
        }
    }
}
```

### 步骤 2: 定义数据库表

在模块的 `src/commonMain/sqldelight/com/example/myapp/data/` 目录下创建 `.sq` 文件：

```sql
-- MyEntity.sq
CREATE TABLE MyEntity (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

selectAll:
SELECT * FROM MyEntity;

selectById:
SELECT * FROM MyEntity WHERE id = ?;

insert:
INSERT INTO MyEntity (id, name, created_at, updated_at)
VALUES (?, ?, ?, ?);

update:
UPDATE MyEntity SET name = ?, updated_at = ? WHERE id = ?;

deleteById:
DELETE FROM MyEntity WHERE id = ?;
```

### 步骤 3: 创建 DataSource

```kotlin
// MyDataSource.kt
interface MyDataSource {
    fun getAll(): Flow<List<MyEntity>>
    fun getById(id: String): Flow<MyEntity?>
    suspend fun insert(entity: MyEntity)
    suspend fun update(entity: MyEntity)
    suspend fun delete(id: String)
}

// MyDataSourceImpl.kt
class MyDataSourceImpl(
    database: MyDatabase
) : BaseDataSource<MyDatabase>(database), MyDataSource {

    private val queries = database.myEntityQueries

    override fun getAll(): Flow<List<MyEntity>> = flowList(
        query = queries.selectAll(),
        mapper = { MyMapper.toDomain(it) }
    )

    override fun getById(id: String): Flow<MyEntity?> = flowOneOrNull(
        query = queries.selectById(id),
        mapper = { MyMapper.toDomain(it) }
    )

    override suspend fun insert(entity: MyEntity) = withTransaction {
        val params = MyMapper.toInsertParams(entity)
        queries.insert(params[0] as String, params[1] as String,
                      params[2] as Long, params[3] as Long)
    }

    override suspend fun update(entity: MyEntity) = withTransaction {
        val params = MyMapper.toUpdateParams(entity)
        queries.update(params[0] as String, params[1] as Long, params[2] as String)
    }

    override suspend fun delete(id: String) = withTransaction {
        queries.deleteById(id)
    }
}
```

### 步骤 4: 配置 Koin DI

```kotlin
// MyDataModule.kt
val myDataModule = module {
    // 创建数据库
    single {
        val driverFactory = get<DatabaseDriverFactory>()
        val driver = driverFactory.createDriver(MyDatabase.Schema, "my_database.db")
        MyDatabase(driver)
    }

    // 数据源
    single<MyDataSource> { MyDataSourceImpl(get()) }
}
```

### 步骤 5: 在应用中使用

```kotlin
class MyRepository(private val dataSource: MyDataSource) {

    fun observeAll(): Flow<List<MyEntity>> = dataSource.getAll()

    suspend fun save(entity: MyEntity) {
        if (entity.isNew) {
            dataSource.insert(entity)
        } else {
            dataSource.update(entity)
        }
    }
}
```

## 完整示例

查看 [GUIDE.md](GUIDE.md) 获取完整的使用指南和高级用法。
