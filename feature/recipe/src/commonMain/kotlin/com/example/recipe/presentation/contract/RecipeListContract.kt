package com.example.recipe.presentation.contract

import com.dqc.kit.presentation.base.UiEffect
import com.dqc.kit.presentation.base.UiIntent
import com.dqc.kit.presentation.base.UiState
import com.example.recipe.domain.model.Recipe
import com.example.recipe.domain.model.RecipeFilter

/**
 * 食谱列表界面状态
 */
data class RecipeListUiState(
    val recipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val filter: RecipeFilter = RecipeFilter.All,
    val searchQuery: String = ""
) : UiState

/**
 * 食谱列表用户意图
 */
sealed class RecipeListUiIntent : UiIntent {
    data object LoadRecipes : RecipeListUiIntent()
    data object RefreshRecipes : RecipeListUiIntent()
    data class ToggleFavorite(val recipeId: String) : RecipeListUiIntent()
    data class SearchRecipes(val query: String) : RecipeListUiIntent()
    data class FilterRecipes(val filter: RecipeFilter) : RecipeListUiIntent()
    data class SelectRecipe(val recipeId: String) : RecipeListUiIntent()
}

/**
 * 食谱列表副作用
 */
sealed class RecipeListUiEffect : UiEffect {
    data class NavigateToDetail(val recipeId: String) : RecipeListUiEffect()
    data class ShowToast(val message: String) : RecipeListUiEffect()
    data class ShowError(val message: String) : RecipeListUiEffect()
}
