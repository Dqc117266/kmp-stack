package com.example.recipe.domain.model

import kotlinx.datetime.Instant

/**
 * 难度等级
 */
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

/**
 * 食谱领域模型
 */
data class Recipe(
    val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String?,
    val cookingTime: Int, // 分钟
    val servings: Int,
    val difficulty: Difficulty,
    val author: Author,
    val ingredients: List<Ingredient> = emptyList(),
    val isFavorite: Boolean = false,
    val isSynced: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun empty() = Recipe(
            id = "",
            title = "",
            description = null,
            imageUrl = null,
            cookingTime = 0,
            servings = 1,
            difficulty = Difficulty.EASY,
            author = Author.empty(),
            ingredients = emptyList(),
            isFavorite = false,
            isSynced = false,
            createdAt = Instant.DISTANT_PAST,
            updatedAt = Instant.DISTANT_PAST
        )
    }
}

/**
 * 作者信息
 */
data class Author(
    val name: String,
    val avatarUrl: String?
) {
    companion object {
        fun empty() = Author("", null)
    }
}

/**
 * 食材领域模型
 */
data class Ingredient(
    val id: String,
    val name: String,
    val amount: Double,
    val unit: String
)

/**
 * 食谱列表过滤器
 */
sealed class RecipeFilter {
    data object All : RecipeFilter()
    data object Favorites : RecipeFilter()
    data class Search(val query: String) : RecipeFilter()
}
