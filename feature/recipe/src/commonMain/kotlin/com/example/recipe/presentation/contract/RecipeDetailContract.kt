package com.example.recipe.presentation.contract

import com.dqc.kit.presentation.base.UiEffect
import com.dqc.kit.presentation.base.UiIntent
import com.dqc.kit.presentation.base.UiState
import com.example.recipe.domain.model.Recipe

/**
 * 食谱详情界面状态
 */
data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false
) : UiState

/**
 * 食谱详情用户意图
 */
sealed class RecipeDetailUiIntent : UiIntent {
    data class LoadRecipe(val recipeId: String) : RecipeDetailUiIntent()
    data object ToggleFavorite : RecipeDetailUiIntent()
    data object ShareRecipe : RecipeDetailUiIntent()
    data object NavigateBack : RecipeDetailUiIntent()
}

/**
 * 食谱详情副作用
 */
sealed class RecipeDetailUiEffect : UiEffect {
    data class ShareRecipeContent(val title: String, val description: String) : RecipeDetailUiEffect()
    data class ShowToast(val message: String) : RecipeDetailUiEffect()
    data object NavigateBack : RecipeDetailUiEffect()
}
