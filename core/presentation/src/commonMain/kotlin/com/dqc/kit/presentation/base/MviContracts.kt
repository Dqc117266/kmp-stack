package com.dqc.kit.presentation.base

/**
 * MVI 架构中的 UI 状态接口
 * 所有状态类都应实现此接口
 *
 * 设计原则：
 * 1. 状态必须是不可变的（使用 data class）
 * 2. 所有属性必须有默认值，确保可以创建初始状态
 * 3. 状态类应该包含 UI 显示所需的所有数据
 *
 * 使用示例：
 * ```kotlin
 * data class LoginUiState(
 *     val username: String = "",
 *     val password: String = "",
 *     val isLoading: Boolean = false,
 *     val errorMessage: String? = null
 * ) : UiState
 * ```
 */
interface UiState

/**
 * MVI 架构中的用户意图/动作接口
 * 所有用户交互事件都应实现此接口
 *
 * 设计原则：
 * 1. 使用密封类（sealed class）定义所有可能的用户意图
 * 2. 意图命名应该使用动词+名词的形式（如 UsernameChanged, SubmitLogin）
 * 3. 意图应该包含所有需要的数据，不要依赖外部状态
 *
 * 使用示例：
 * ```kotlin
 * sealed class LoginUiIntent : UiIntent {
 *     data class UsernameChanged(val value: String) : LoginUiIntent()
 *     data class PasswordChanged(val value: String) : LoginUiIntent()
 *     data object SubmitLogin : LoginUiIntent()
 * }
 * ```
 */
interface UiIntent

/**
 * MVI 架构中的副作用接口
 * 用于表示一次性事件（如 Toast、导航、Snackbar 等）
 * 这些事件不应该影响 UI 状态的持久性
 *
 * 设计原则：
 * 1. 使用密封类（sealed class）定义所有可能的副作用
 * 2. 副作用是"一次性"的，消费后不会重复触发
 * 3. 副作用不应该改变 UI 状态
 *
 * 使用示例：
 * ```kotlin
 * sealed class LoginUiEffect : UiEffect {
 *     data object NavigateToHome : LoginUiEffect()
 *     data class ShowError(val message: String) : LoginUiEffect()
 *     data class ShowSuccess(val message: String) : LoginUiEffect()
 * }
 * ```
 */
interface UiEffect
