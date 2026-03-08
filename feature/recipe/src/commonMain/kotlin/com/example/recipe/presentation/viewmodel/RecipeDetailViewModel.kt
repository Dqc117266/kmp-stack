package com.example.recipe.presentation.viewmodel

import com.dqc.kit.domain.result.onError
import com.dqc.kit.domain.result.onSuccess
import com.dqc.kit.presentation.base.BaseMviViewModel
import com.example.recipe.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe.domain.usecase.ToggleFavoriteUseCase
import com.example.recipe.presentation.contract.RecipeDetailUiEffect
import com.example.recipe.presentation.contract.RecipeDetailUiIntent
import com.example.recipe.presentation.contract.RecipeDetailUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 食谱详情 ViewModel
 */
class RecipeDetailViewModel(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    dispatcher: CoroutineDispatcher
) : BaseMviViewModel<RecipeDetailUiState, RecipeDetailUiIntent, RecipeDetailUiEffect>(
    initialState = RecipeDetailUiState(),
    dispatcher = dispatcher
) {

    private var currentRecipeId: String? = null

    override fun registerIntents() {
        registerIntent<RecipeDetailUiIntent.LoadRecipe> { intent ->
            currentRecipeId = intent.recipeId
            loadRecipe(intent.recipeId)
        }

        registerIntent<RecipeDetailUiIntent.ToggleFavorite> {
            toggleFavorite()
        }

        registerIntent<RecipeDetailUiIntent.ShareRecipe> {
            shareRecipe()
        }

        registerIntent<RecipeDetailUiIntent.NavigateBack> {
            sendEffect(RecipeDetailUiEffect.NavigateBack)
        }
    }

    private fun loadRecipe(recipeId: String) {
        // 观察本地数据
        getRecipeDetailUseCase(recipeId)
            .onEach { recipe ->
                recipe?.let {
                    updateState {
                        copy(
                            recipe = it,
                            isFavorite = it.isFavorite,
                            isLoading = false
                        )
                    }
                } ?: run {
                    updateState { copy(isLoading = false, errorMessage = "Recipe not found") }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun toggleFavorite() {
        currentRecipeId?.let { id ->
            launch {
                toggleFavoriteUseCase(id)
                    .onSuccess { isFavorite ->
                        updateState { copy(isFavorite = isFavorite) }
                        val message = if (isFavorite) "已收藏" else "已取消收藏"
                        sendEffect(RecipeDetailUiEffect.ShowToast(message))
                    }
                    .onError { error ->
                        sendEffect(RecipeDetailUiEffect.ShowToast(error.message))
                    }
            }
        }
    }

    private fun shareRecipe() {
        currentState.recipe?.let { recipe ->
            sendEffect(
                RecipeDetailUiEffect.ShareRecipeContent(
                    title = recipe.title,
                    description = recipe.description ?: ""
                )
            )
        }
    }

    fun clearError() {
        updateState { copy(errorMessage = null) }
    }
}
