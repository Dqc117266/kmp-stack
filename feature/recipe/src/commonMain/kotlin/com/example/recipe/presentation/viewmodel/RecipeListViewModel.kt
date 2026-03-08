package com.example.recipe.presentation.viewmodel

import com.dqc.kit.presentation.base.BaseMviViewModel
import com.example.recipe.domain.model.RecipeFilter
import com.example.recipe.domain.usecase.FetchRecipesUseCase
import com.example.recipe.domain.usecase.GetRecipesUseCase
import com.example.recipe.domain.usecase.ToggleFavoriteUseCase
import com.example.recipe.presentation.contract.RecipeListUiEffect
import com.example.recipe.presentation.contract.RecipeListUiIntent
import com.example.recipe.presentation.contract.RecipeListUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * 食谱列表 ViewModel
 */
class RecipeListViewModel(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val fetchRecipesUseCase: FetchRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    dispatcher: CoroutineDispatcher
) : BaseMviViewModel<RecipeListUiState, RecipeListUiIntent, RecipeListUiEffect>(
    initialState = RecipeListUiState(),
    dispatcher = dispatcher
) {

    init {
        // 观察本地食谱数据
        observeRecipes()
        // 从远程获取数据
        dispatch(RecipeListUiIntent.LoadRecipes)
    }

    override fun registerIntents() {
        registerIntent<RecipeListUiIntent.LoadRecipes> {
            loadRecipes()
        }

        registerIntent<RecipeListUiIntent.RefreshRecipes> {
            refreshRecipes()
        }

        registerIntent<RecipeListUiIntent.ToggleFavorite> { intent ->
            toggleFavorite(intent.recipeId)
        }

        registerIntent<RecipeListUiIntent.SearchRecipes> { intent ->
            updateState { copy(searchQuery = intent.query) }
            if (intent.query.isNotBlank()) {
                updateState { copy(filter = RecipeFilter.Search(intent.query)) }
            } else {
                updateState { copy(filter = RecipeFilter.All) }
            }
        }

        registerIntent<RecipeListUiIntent.FilterRecipes> { intent ->
            updateState { copy(filter = intent.filter) }
        }

        registerIntent<RecipeListUiIntent.SelectRecipe> { intent ->
            sendEffect(RecipeListUiEffect.NavigateToDetail(intent.recipeId))
        }
    }

    private fun observeRecipes() {
        getRecipesUseCase(currentState.filter)
            .onEach { recipes ->
                updateState { copy(recipes = recipes, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadRecipes() {
        launchRequest(
            setLoading = { loading -> copy(isLoading = loading) }
        ) {
            fetchRecipesUseCase()
                .onSuccess { recipes ->
                    updateState { copy(recipes = recipes) }
                }
                .onError { error ->
                    updateState { copy(errorMessage = error.message) }
                    sendEffect(RecipeListUiEffect.ShowError(error.message))
                }
        }
    }

    private fun refreshRecipes() {
        launchRequest(
            setLoading = { loading -> copy(isRefreshing = loading) }
        ) {
            fetchRecipesUseCase()
                .onSuccess { recipes ->
                    updateState { copy(recipes = recipes, isRefreshing = false) }
                    sendEffect(RecipeListUiEffect.ShowToast("刷新成功"))
                }
                .onError { error ->
                    updateState { copy(isRefreshing = false) }
                    sendEffect(RecipeListUiEffect.ShowError(error.message))
                }
        }
    }

    private fun toggleFavorite(recipeId: String) {
        launch {
            toggleFavoriteUseCase(recipeId)
                .onSuccess { isFavorite ->
                    val message = if (isFavorite) "已收藏" else "已取消收藏"
                    sendEffect(RecipeListUiEffect.ShowToast(message))
                }
                .onError { error ->
                    sendEffect(RecipeListUiEffect.ShowError(error.message))
                }
        }
    }

    fun clearError() {
        updateState { copy(errorMessage = null) }
    }
}
