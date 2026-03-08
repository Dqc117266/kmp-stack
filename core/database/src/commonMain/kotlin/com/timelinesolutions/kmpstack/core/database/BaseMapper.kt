package com.timelinesolutions.kmpstack.core.database

/**
 * 实体映射器接口
 * 定义数据库模型和领域模型之间的转换规范
 *
 * @param D 数据库模型类型（SQLDelight 生成的类型）
 * @param R 领域模型类型（Domain Entity）
 */
interface BaseMapper<D, R> {

    /**
     * 将数据库模型转换为领域模型
     *
     * @param entity 数据库实体
     * @return 领域实体
     */
    fun toDomain(entity: D): R

    /**
     * 将领域模型转换为数据库模型参数列表
     * 用于 INSERT 操作
     *
     * @param domain 领域实体
     * @return 参数列表
     */
    fun toInsertParams(domain: R): List<Any?>

    /**
     * 将领域模型转换为数据库模型参数列表
     * 用于 UPDATE 操作
     *
     * @param domain 领域实体
     * @return 参数列表
     */
    fun toUpdateParams(domain: R): List<Any?>
}

/**
 * 简化版映射器接口（用于简单实体）
 */
interface SimpleMapper<D, R> {

    /**
     * 将数据库模型转换为领域模型
     */
    fun toDomain(entity: D): R
}

/**
 * 时间戳转换工具
 */
object TimeUtils {

    /**
     * 将毫秒时间戳转换为 kotlinx.datetime.Instant
     */
    fun Long.toInstant(): kotlinx.datetime.Instant =
        kotlinx.datetime.Instant.fromEpochMilliseconds(this)

    /**
     * 将 kotlinx.datetime.Instant 转换为毫秒时间戳
     */
    fun kotlinx.datetime.Instant.toMillis(): Long = this.toEpochMilliseconds()
}
