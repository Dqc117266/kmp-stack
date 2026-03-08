package com.example.recipe.data.mapper

import com.example.recipe.data.RecipeEntity
import com.example.recipe.data.IngredientEntity
import com.example.recipe.data.model.AuthorDto
import com.example.recipe.data.model.IngredientDto
import com.example.recipe.data.model.RecipeDto
import com.example.recipe.domain.model.Author
import com.example.recipe.domain.model.Difficulty
import com.example.recipe.domain.model.Ingredient
import com.example.recipe.domain.model.Recipe
import com.timelinesolutions.kmpstack.core.database.TimeUtils.toInstant
import com.timelinesolutions.kmpstack.core.database.TimeUtils.toMillis
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * 食谱数据映射器
 */
object RecipeMapper {

    // ========== DTO to Domain ==========

    fun toDomain(dto: RecipeDto): Recipe {
        return Recipe(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            imageUrl = dto.imageUrl,
            cookingTime = dto.cookingTime,
            servings = dto.servings,
            difficulty = toDifficulty(dto.difficulty),
            author = Author(
                name = dto.author.name,
                avatarUrl = dto.author.avatarUrl
            ),
            ingredients = dto.ingredients.map { toDomain(it) },
            isFavorite = false,
            isSynced = true,
            createdAt = Instant.parse(dto.createdAt),
            updatedAt = Instant.parse(dto.updatedAt)
        )
    }

    fun toDomain(dto: IngredientDto): Ingredient {
        return Ingredient(
            id = dto.id,
            name = dto.name,
            amount = dto.amount,
            unit = dto.unit
        )
    }

    // ========== Entity to Domain ==========

    fun toDomain(entity: RecipeEntity, ingredients: List<IngredientEntity> = emptyList()): Recipe {
        return Recipe(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            imageUrl = entity.image_url,
            cookingTime = entity.cooking_time.toInt(),
            servings = entity.servings.toInt(),
            difficulty = toDifficulty(entity.difficulty),
            author = Author(
                name = entity.author_name,
                avatarUrl = entity.author_avatar
            ),
            ingredients = ingredients.map { toDomain(it) },
            isFavorite = entity.is_favorite != 0L,
            isSynced = entity.is_synced != 0L,
            createdAt = entity.created_at.toInstant(),
            updatedAt = entity.updated_at.toInstant()
        )
    }

    fun toDomain(entity: IngredientEntity): Ingredient {
        return Ingredient(
            id = entity.id,
            name = entity.name,
            amount = entity.amount,
            unit = entity.unit
        )
    }

    // ========== Domain to DTO ==========

    fun toDto(recipe: Recipe): RecipeDto {
        return RecipeDto(
            id = recipe.id,
            title = recipe.title,
            description = recipe.description,
            imageUrl = recipe.imageUrl,
            cookingTime = recipe.cookingTime,
            servings = recipe.servings,
            difficulty = fromDifficulty(recipe.difficulty),
            author = AuthorDto(
                name = recipe.author.name,
                avatarUrl = recipe.author.avatarUrl
            ),
            ingredients = recipe.ingredients.map { toDto(it) },
            createdAt = recipe.createdAt.toString(),
            updatedAt = recipe.updatedAt.toString()
        )
    }

    fun toDto(ingredient: Ingredient): IngredientDto {
        return IngredientDto(
            id = ingredient.id,
            name = ingredient.name,
            amount = ingredient.amount,
            unit = ingredient.unit
        )
    }

    // ========== Domain to Entity Params ==========

    fun toInsertParams(recipe: Recipe): List<Any?> {
        val now = Clock.System.now().toMillis()
        return listOf(
            recipe.id,
            recipe.title,
            recipe.description,
            recipe.imageUrl,
            recipe.cookingTime.toLong(),
            recipe.servings.toLong(),
            fromDifficulty(recipe.difficulty),
            recipe.author.name,
            recipe.author.avatarUrl,
            if (recipe.isFavorite) 1L else 0L,
            if (recipe.isSynced) 1L else 0L,
            recipe.createdAt.toMillis().coerceAtLeast(now),
            recipe.updatedAt.toMillis().coerceAtLeast(now)
        )
    }

    fun toUpdateParams(recipe: Recipe): List<Any?> {
        return listOf(
            recipe.title,
            recipe.description,
            recipe.imageUrl,
            recipe.cookingTime.toLong(),
            recipe.servings.toLong(),
            fromDifficulty(recipe.difficulty),
            recipe.author.name,
            recipe.author.avatarUrl,
            if (recipe.isFavorite) 1L else 0L,
            if (recipe.isSynced) 1L else 0L,
            Clock.System.now().toMillis(),
            recipe.id
        )
    }

    fun toInsertParams(ingredient: Ingredient, recipeId: String): List<Any?> {
        return listOf(
            ingredient.id,
            recipeId,
            ingredient.name,
            ingredient.amount,
            ingredient.unit
        )
    }

    // ========== Private Helpers ==========

    private fun toDifficulty(value: String): Difficulty {
        return when (value.uppercase()) {
            "EASY" -> Difficulty.EASY
            "MEDIUM" -> Difficulty.MEDIUM
            "HARD" -> Difficulty.HARD
            else -> Difficulty.EASY
        }
    }

    private fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }
}
