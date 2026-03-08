package com.example.recipe.presentation.screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.recipe.domain.model.Difficulty
import com.example.recipe.domain.model.Ingredient
import com.example.recipe.domain.model.Recipe
import com.example.recipe.presentation.component.ErrorView
import com.example.recipe.presentation.component.LoadingIndicator
import com.example.recipe.presentation.contract.RecipeDetailUiEffect
import com.example.recipe.presentation.contract.RecipeDetailUiIntent
import com.example.recipe.presentation.viewmodel.RecipeDetailViewModel
import kotlinx.coroutines.flow.Flow

/**
 * 食谱详情屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel,
    recipeId: String,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit,
    onShareRecipe: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // 加载数据
    LaunchedEffect(recipeId) {
        viewModel.dispatch(RecipeDetailUiIntent.LoadRecipe(recipeId))
    }

    // 处理副作用
    HandleEffects(
        effectFlow = viewModel.effect,
        onNavigateBack = onNavigateBack,
        onShowToast = onShowToast,
        onShareRecipe = onShareRecipe
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.title ?: "食谱详情") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.dispatch(RecipeDetailUiIntent.NavigateBack) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.dispatch(RecipeDetailUiIntent.ShareRecipe) }) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
                    }
                }
            )
        },
        floatingActionButton = {
            uiState.recipe?.let { recipe ->
                FavoriteFab(
                    isFavorite = uiState.isFavorite,
                    onToggle = {
                        viewModel.dispatch(RecipeDetailUiIntent.ToggleFavorite)
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.errorMessage != null -> ErrorView(
                    message = uiState.errorMessage!!,
                    onRetry = {
                        viewModel.clearError()
                        viewModel.dispatch(RecipeDetailUiIntent.LoadRecipe(recipeId))
                    }
                )
                uiState.recipe != null -> RecipeDetailContent(recipe = uiState.recipe!!)
            }
        }
    }
}

@Composable
private fun FavoriteFab(
    isFavorite: Boolean,
    onToggle: () -> Unit
) {
    FloatingActionButton(
        onClick = onToggle,
        shape = CircleShape
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorite) "取消收藏" else "收藏"
        )
    }
}

@Composable
private fun RecipeDetailContent(recipe: Recipe) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // 图片
        item {
            recipe.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 标题和基本信息
        item {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 元信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetaInfoItem(icon = "⏱", label = "烹饪时间", value = "${recipe.cookingTime}分钟")
                    MetaInfoItem(icon = "🍽", label = "份量", value = "${recipe.servings}人份")
                    MetaInfoItem(
                        icon = "📊",
                        label = "难度",
                        value = when (recipe.difficulty) {
                            Difficulty.EASY -> "简单"
                            Difficulty.MEDIUM -> "中等"
                            Difficulty.HARD -> "困难"
                        }
                    )
                }
            }
        }

        // 作者信息
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    recipe.author.avatarUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = recipe.author.name,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = recipe.author.name.firstOrNull()?.toString() ?: "?",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "食谱作者",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = recipe.author.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // 描述
        if (!recipe.description.isNullOrBlank()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(title = "介绍")
                Text(
                    text = recipe.description,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // 食材列表
        if (recipe.ingredients.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(title = "食材")
            }

            items(recipe.ingredients) { ingredient ->
                IngredientItem(ingredient = ingredient)
            }
        }
    }
}

@Composable
private fun MetaInfoItem(icon: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun IngredientItem(ingredient: Ingredient) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${ingredient.amount} ${ingredient.unit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HandleEffects(
    effectFlow: Flow<RecipeDetailUiEffect>,
    onNavigateBack: () -> Unit,
    onShowToast: (String) -> Unit,
    onShareRecipe: (String, String) -> Unit
) {
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is RecipeDetailUiEffect.NavigateBack -> onNavigateBack()
                is RecipeDetailUiEffect.ShowToast -> onShowToast(effect.message)
                is RecipeDetailUiEffect.ShareRecipeContent -> {
                    onShareRecipe(effect.title, effect.description)
                }
            }
        }
    }
}
