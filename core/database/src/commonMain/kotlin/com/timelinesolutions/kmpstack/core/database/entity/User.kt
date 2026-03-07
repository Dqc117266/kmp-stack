package com.timelinesolutions.kmpstack.core.database.entity

import kotlinx.datetime.Instant

/**
 * 领域层用户实体
 * 纯净的领域模型，不依赖任何框架
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
