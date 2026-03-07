package com.dqc.kit.presentation.example

import com.dqc.kit.presentation.base.BaseMviViewModel
import com.dqc.kit.presentation.base.DebugConfig
import com.dqc.kit.presentation.base.UiEffect
import com.dqc.kit.presentation.base.UiIntent
import com.dqc.kit.presentation.base.UiState
import com.dqc.kit.presentation.model.UiMapper
import com.dqc.kit.presentation.model.UiText
import com.dqc.kit.presentation.model.UiValue
import com.dqc.kit.presentation.model.asString
import com.dqc.kit.presentation.model.toUi
import com.dqc.kit.presentation.navigation.NavigationEffect
import com.dqc.kit.presentation.navigation.Routes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import com.dqc.kit.presentation.model.UiFormatter

// ============================================================================
// Domain 层实体（示例）
// ============================================================================

/**
 * Domain 层用户实体
 */
data class UserEntity(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val createdAt: Long
)

// ============================================================================
// UI 模型定义
// ============================================================================

/**
 * 用户 UI 模型
 * 已经过格式化，可直接用于 UI 显示
 */
data class UserUiModel(
    val id: String,
    val displayName: String,
    val avatarUrl: String,
    val joinTime: String
)

/**
 * 用户 UI 映射器
 */
class UserUiMapper(
    private val timeFormatter: UiFormatter<Long, String>
) : UiMapper<UserEntity, UserUiModel> {
    override fun map(input: UserEntity): UserUiModel {
        return UserUiModel(
            id = input.id,
            displayName = input.name.ifEmpty { "匿名用户" },
            avatarUrl = input.avatarUrl,
            joinTime = timeFormatter.format(input.createdAt)
        )
    }
}

// ============================================================================
// MVI 契约定义
// ============================================================================

/**
 * 用户资料页面状态
 */
data class UserProfileUiState(
    val user: UiValue<UserUiModel> = UiValue.Empty,
    val isRefreshing: Boolean = false,
    val errorMessage: UiText? = null
) : UiState

/**
 * 用户资料页面意图
 */
sealed class UserProfileUiIntent : UiIntent {
    /**
     * 加载用户数据
     */
    data class LoadUser(val userId: String) : UserProfileUiIntent()

    /**
     * 刷新用户数据
     */
    data object Refresh : UserProfileUiIntent()

    /**
     * 点击返回
     */
    data object ClickBack : UserProfileUiIntent()

    /**
     * 点击设置
     */
    data object ClickSettings : UserProfileUiIntent()
}

/**
 * 用户资料页面副作用
 */
sealed class UserProfileUiEffect : UiEffect {
    /**
     * 显示提示
     */
    data class ShowToast(val message: UiText) : UserProfileUiEffect()

    /**
     * 导航效果
     */
    data class Navigate(val navigation: NavigationEffect) : UserProfileUiEffect()
}

// ============================================================================
// ViewModel 实现
// ============================================================================

/**
 * 用户资料 ViewModel
 * 展示如何使用 presentation 模块的所有功能
 */
class UserProfileViewModel(
    private val getUserUseCase: suspend (String) -> Result<UserEntity>,
    private val userMapper: UserUiMapper,
    dispatcher: CoroutineDispatcher
) : BaseMviViewModel<UserProfileUiState, UserProfileUiIntent, UserProfileUiEffect>(
    initialState = UserProfileUiState(),
    dispatcher = dispatcher,
    debugConfig = DebugConfig.ENABLED // 启用调试
) {

    override fun registerIntents() {
        // 注册加载用户处理器
        registerIntent<UserProfileUiIntent.LoadUser> { intent ->
            loadUser(intent.userId)
        }

        // 注册刷新处理器
        registerIntent<UserProfileUiIntent.Refresh> {
            refreshUser()
        }

        // 注册返回处理器
        registerIntent<UserProfileUiIntent.ClickBack> {
            // 发出导航效果
            sendEffect(
                UserProfileUiEffect.Navigate(
                    NavigationEffect.PopBackStack()
                )
            )
        }

        // 注册设置处理器
        registerIntent<UserProfileUiIntent.ClickSettings> {
            // 导航到设置页面
            sendEffect(
                UserProfileUiEffect.Navigate(
                    NavigationEffect.ToRoute(Routes.SETTINGS)
                )
            )
        }
    }

    private fun loadUser(userId: String) {
        launchRequest(
            setLoading = { loading ->
                copy(
                    user = if (loading && this.user is UiValue.Empty) {
                        UiValue.Loading
                    } else {
                        this.user
                    }
                )
            }
        ) {
            val result = getUserUseCase(userId)

            result.onSuccess { entity ->
                val uiModel = entity.toUi(userMapper)
                updateState {
                    copy(user = UiValue.of(uiModel))
                }
            }.onFailure { error ->
                updateState {
                    copy(
                        user = UiValue.error(error.message ?: "加载失败"),
                        errorMessage = UiText.plain(error.message ?: "加载失败")
                    )
                }
                sendEffect(
                    UserProfileUiEffect.ShowToast(
                        UiText.plain(error.message ?: "加载失败")
                    )
                )
            }
        }
    }

    private fun refreshUser() {
        // 检查当前是否有用户数据
        val currentUser = (currentState.user as? UiValue.Value<UserUiModel>)?.value
            ?: return

        updateState { copy(isRefreshing = true) }

        launch {
            val result = getUserUseCase(currentUser.id)

            result.onSuccess { entity ->
                val uiModel = entity.toUi(userMapper)
                updateState {
                    copy(
                        user = UiValue.of(uiModel),
                        isRefreshing = false
                    )
                }
            }.onFailure { error ->
                updateState {
                    copy(
                        isRefreshing = false,
                        errorMessage = UiText.plain(error.message ?: "刷新失败")
                    )
                }
                sendEffect(
                    UserProfileUiEffect.ShowToast(
                        UiText.plain(error.message ?: "刷新失败")
                    )
                )
            }
        }
    }

    override fun onInitialize() {
        // ViewModel 初始化时的逻辑
        println("UserProfileViewModel initialized")
    }

    override fun onCleared() {
        super.onCleared()
        // 清理资源
        println("UserProfileViewModel cleared")
    }
}

// ============================================================================
// 使用说明
// ============================================================================

/**
 * ## Android 平台使用示例
 *
 * ```kotlin
 * @Composable
 * fun UserProfileScreen(
 *     viewModel: UserProfileViewModel = koinViewModel(),
 *     navController: NavController
 * ) {
 *     val state by viewModel.uiState.collectAsState()
 *
 *     // 处理导航效果
 *     LaunchedEffect(Unit) {
 *         viewModel.effect.collect { effect ->
 *             when (effect) {
 *                 is UserProfileUiEffect.Navigate -> {
 *                     handleNavigationEffect(effect.navigation, navController)
 *                 }
 *                 is UserProfileUiEffect.ShowToast -> {
 *                     Toast.makeText(
 *                         context,
 *                         effect.message.asString(),
 *                         Toast.LENGTH_SHORT
 *                     ).show()
 *                 }
 *             }
 *         }
 *     }
 *
 *     // UI 内容
 *     when (val userValue = state.user) {
 *         is UiValue.Loading -> {
 *             CircularProgressIndicator()
 *         }
 *         is UiValue.Error -> {
 *             ErrorView(message = userValue.message.asString())
 *         }
 *         is UiValue.Value -> {
 *             UserProfileContent(
 *                 user = userValue.value,
 *                 onBackClick = { viewModel.dispatch(UserProfileUiIntent.ClickBack) },
 *                 onSettingsClick = { viewModel.dispatch(UserProfileUiIntent.ClickSettings) }
 *             )
 *         }
 *         UiValue.Empty -> {
 *             // 初始状态，可以自动加载或显示空视图
 *         }
 *     }
 * }
 *
 * // 导航效果处理
 * private fun handleNavigationEffect(
 *     effect: NavigationEffect,
 *     navController: NavController
 * ) {
 *     when (effect) {
 *         is NavigationEffect.ToRoute -> {
 *             navController.navigate(effect.buildRoute())
 *         }
 *         is NavigationEffect.PopBackStack -> {
 *             navController.popBackStack()
 *         }
 *         // ...
 *     }
 * }
 * ```
 *
 * ## iOS 平台使用示例
 *
 * ```swift
 * import SwiftUI
 * import Shared
 *
 * class UserProfileViewModelWrapper: ObservableObject {
 *     private let viewModel: UserProfileViewModel
 *
 *     @Published var state: UserProfileUiState
 *
 *     init(viewModel: UserProfileViewModel) {
 *         self.viewModel = viewModel
 *         self.state = viewModel.uiState.value
 *
 *         // 收集状态
 *         viewModel.uiState.collect { [weak self] newState in
 *             DispatchQueue.main.async {
 *                 self?.state = newState
 *             }
 *         }
 *     }
 *
 *     func dispatch(_ intent: UserProfileUiIntent) {
 *         viewModel.dispatch(intent: intent)
 *     }
 *
 *     deinit {
 *         viewModel.onCleared()
 *     }
 * }
 *
 * struct UserProfileView: View {
 *     @StateObject private var viewModel: UserProfileViewModelWrapper
 *
 *     var body: some View {
 *         VStack {
 *             switch viewModel.state.user {
 *             case .loading:
 *                 ProgressView()
 *             case .error(let message):
 *                 Text(message.asString())
 *             case .value(let user):
 *                 UserProfileContent(user: user)
 *             default:
 *                 EmptyView()
 *             }
 *         }
 *     }
 * }
 * ```
 */
