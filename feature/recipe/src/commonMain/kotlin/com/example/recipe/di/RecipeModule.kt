package com.example.recipe.di

import com.example.recipe.data.RecipeDatabase
import com.example.recipe.data.datasource.local.RecipeLocalDataSource
import com.example.recipe.data.datasource.local.RecipeLocalDataSourceImpl
import com.example.recipe.data.datasource.remote.RecipeRemoteDataSource
import com.example.recipe.data.repository.RecipeRepositoryImpl
import com.example.recipe.domain.repository.RecipeRepository
import com.example.recipe.domain.usecase.FetchRecipesUseCase
import com.example.recipe.domain.usecase.GetFavoriteRecipesUseCase
import com.example.recipe.domain.usecase.GetRecipeDetailUseCase
import com.example.recipe.domain.usecase.GetRecipesUseCase
import com.example.recipe.domain.usecase.SearchRecipesUseCase
import com.example.recipe.domain.usecase.SyncRecipesUseCase
import com.example.recipe.domain.usecase.ToggleFavoriteUseCase
import com.example.recipe.presentation.viewmodel.RecipeListViewModel
import com.example.recipe.presentation.viewmodel.RecipeDetailViewModel
import com.timelinesolutions.kmpstack.core.database.DatabaseDriverFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Recipe 模块依赖注入模块
 */
val recipeModule: Module = module {

    // ==================== Database ====================
    single {
        val driverFactory = get<DatabaseDriverFactory>()
        val driver = driverFactory.createDriver(RecipeDatabase.Schema, "recipe_database.db")
        RecipeDatabase(driver)
    }

    // ==================== DataSource ====================
    single<RecipeLocalDataSource> {
        RecipeLocalDataSourceImpl(get())
    }

    single {
        RecipeRemoteDataSource(get())
    }

    // ==================== Repository ====================
    single<RecipeRepository> {
        RecipeRepositoryImpl(get(), get())
    }

    // ==================== UseCase ====================
    single { GetRecipesUseCase(get()) }
    single { GetRecipeDetailUseCase(get()) }
    single { GetFavoriteRecipesUseCase(get()) }
    single { FetchRecipesUseCase(get()) }
    single { ToggleFavoriteUseCase(get()) }
    single { SearchRecipesUseCase(get()) }
    single { SyncRecipesUseCase(get()) }

    // ==================== ViewModel ====================
    factory {
        RecipeListViewModel(
            getRecipesUseCase = get(),
            fetchRecipesUseCase = get(),
            toggleFavoriteUseCase = get(),
            dispatcher = Dispatchers.IO
        )
    }

    factory {
        RecipeDetailViewModel(
            getRecipeDetailUseCase = get(),
            toggleFavoriteUseCase = get(),
            dispatcher = Dispatchers.IO
        )
    }
}

val recipeModules = listOf(recipeModule)
