package com.example.recipe.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.recipe.domain.model.Difficulty
import com.example.recipe.domain.model.Recipe
import com.example.recipe.domain.model.RecipeFilter
import com.example.recipe.presentation.component.EmptyView
import com.example.recipe.presentation.component.ErrorView
import com.example.recipe.presentation.component.LoadingIndicator
import com.example.recipe.presentation.contract.RecipeListUiEffect
import com.example.recipe.presentation.contract.RecipeListUiIntent
import com.example.recipe.presentation.contract.RecipeListUiState
import com.example.recipe.presentation.viewmodel.RecipeListViewModel
import kotlinx.coroutines.flow.Flow

/**
 * 食谱列表屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel,
    onNavigateToDetail: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    // 处理副作用
    HandleEffects(
        effectFlow = viewModel.effect,
        onNavigateToDetail = onNavigateToDetail,
        onShowToast = onShowToast
    )

    // 下拉刷新
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(Unit) {
            viewModel.dispatch(RecipeListUiIntent.RefreshRecipes)
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("食谱大全") },
                actions = {
                    IconButton(onClick = {
                        viewModel.dispatch(RecipeListUiIntent.RefreshRecipes)
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 搜索栏
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = {
                        viewModel.dispatch(RecipeListUiIntent.SearchRecipes(it))
                    },
                    onSearch = {
                        viewModel.dispatch(RecipeListUiIntent.SearchRecipes(it))
                    },
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("搜索食谱...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                ) {}

                // 过滤器
                FilterChips(
                    currentFilter = uiState.filter,
                    onFilterSelected = {
                        viewModel.dispatch(RecipeListUiIntent.FilterRecipes(it))
                    }
                )

                // 内容区域
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        uiState.isLoading && uiState.recipes.isEmpty() -> {
                            LoadingIndicator()
                        }
                        uiState.errorMessage != null && uiState.recipes.isEmpty() -> {
                            ErrorView(
                                message = uiState.errorMessage!!,
                                onRetry = {
                                    viewModel.clearError()
                                    viewModel.dispatch(RecipeListUiIntent.LoadRecipes)
                                }
                            )
                        }
                        uiState.recipes.isEmpty() -> {
                            EmptyView(message = "暂无食谱")
                        }
                        else -> {
                            RecipeList(
                                recipes = uiState.recipes,
                                onRecipeClick = {
                                    viewModel.dispatch(RecipeListUiIntent.SelectRecipe(it.id))
                                },
                                onToggleFavorite = {
                                    viewModel.dispatch(RecipeListUiIntent.ToggleFavorite(it.id))
                                }
                            )
                        }
                    }

                    // 下拉刷新指示器
                    PullToRefreshContainer(
                        state = pullToRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    currentFilter: RecipeFilter,
    onFilterSelected: (RecipeFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter is RecipeFilter.All,
            onClick = { onFilterSelected(RecipeFilter.All) },
            label = { Text("全部") }
        )
        FilterChip(
            selected = currentFilter is RecipeFilter.Favorites,
            onClick = { onFilterSelected(RecipeFilter.Favorites) },
            label = { Text("收藏") }
        )
    }
}

@Composable
private fun RecipeList(
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit,
    onToggleFavorite: (Recipe) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(recipes, key = { it.id }) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe) },
                onToggleFavorite = { onToggleFavorite(recipe) }
            )
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 图片
            recipe.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 标题和收藏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (recipe.isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = if (recipe.isFavorite) "取消收藏" else "收藏",
                            tint = if (recipe.isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                // 描述
                recipe.description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 元信息
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetaInfo(icon = "⏱", text = "${recipe.cookingTime}分钟")
                    MetaInfo(icon = "🍽", text = "${recipe.servings}人份")
                    DifficultyBadge(difficulty = recipe.difficulty)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 作者
                Text(
                    text = "by ${recipe.author.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetaInfo(icon: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DifficultyBadge(difficulty: Difficulty) {
    val (text, color) = when (difficulty) {
        Difficulty.EASY -> "简单" to MaterialTheme.colorScheme.primary
        Difficulty.MEDIUM -> "中等" to MaterialTheme.colorScheme.secondary
        Difficulty.HARD -> "困难" to MaterialTheme.colorScheme.error
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color
    )
}

@Composable
private fun HandleEffects(
    effectFlow: Flow<RecipeListUiEffect>,
    onNavigateToDetail: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is RecipeListUiEffect.NavigateToDetail -> {
                    onNavigateToDetail(effect.recipeId)
                }
                is RecipeListUiEffect.ShowToast -> {
                    onShowToast(effect.message)
                }
                is RecipeListUiEffect.ShowError -> {
                    onShowToast(effect.message)
                }
            }
        }
    }
}
