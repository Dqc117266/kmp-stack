package com.example.recipe.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 食谱 DTO（网络模型）
 */
@Serializable
data class RecipeDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("cooking_time")
    val cookingTime: Int = 0,
    @SerialName("servings")
    val servings: Int = 1,
    @SerialName("difficulty")
    val difficulty: String = "EASY",
    @SerialName("author")
    val author: AuthorDto,
    @SerialName("ingredients")
    val ingredients: List<IngredientDto> = emptyList(),
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class AuthorDto(
    @SerialName("name")
    val name: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

@Serializable
data class IngredientDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: Double,
    @SerialName("unit")
    val unit: String
)

/**
 * API 响应包装
 */
@Serializable
data class ApiResponse<T>(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: T? = null,
    @SerialName("error")
    val error: ApiError? = null
)

@Serializable
data class ApiError(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String
)
