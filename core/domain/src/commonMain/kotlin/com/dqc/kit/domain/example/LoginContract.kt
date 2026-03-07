package com.dqc.kit.domain.example

import com.dqc.kit.domain.base.BaseMviViewModel
import com.dqc.kit.domain.base.UiEffect
import com.dqc.kit.domain.base.UiIntent
import com.dqc.kit.domain.base.UiState
import com.dqc.kit.domain.entity.UserEntity
import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay

// ============================================================================
// Login MVI 契约定义
// ============================================================================

/**
 * 登录页面状态
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val user: UserEntity? = null
) : UiState {
    /**
     * 表单是否有效
     */
    val isFormValid: Boolean
        get() = username.isNotBlank() && password.length >= 6

    /**
     * 是否可以提交
     */
    val canSubmit: Boolean
        get() = isFormValid && !isLoading
}

/**
 * 登录用户意图
 */
sealed class LoginUiIntent : UiIntent {
    /**
     * 用户名输入
     * @param value 用户名
     */
    data class UsernameChanged(val value: String) : LoginUiIntent()

    /**
     * 密码输入
     * @param value 密码
     */
    data class PasswordChanged(val value: String) : LoginUiIntent()

    /**
     * 点击登录按钮
     */
    data object SubmitLogin : LoginUiIntent()

    /**
     * 点击注册按钮
     */
    data object NavigateToRegister : LoginUiIntent()

    /**
     * 清除错误
     */
    data object ClearError : LoginUiIntent()
}

/**
 * 登录副作用
 * 一次性事件，如导航、Toast 等
 */
sealed class LoginUiEffect : UiEffect {
    /**
     * 导航到首页
     */
    data object NavigateToHome : LoginUiEffect()

    /**
     * 导航到注册页
     */
    data object NavigateToRegister : LoginUiEffect()

    /**
     * 显示错误提示
     * @param message 错误信息
     */
    data class ShowError(val message: String) : LoginUiEffect()

    /**
     * 显示成功提示
     * @param message 成功信息
     */
    data class ShowSuccess(val message: String) : LoginUiEffect()
}

/**
 * 登录参数数据类
 */
data class LoginParams(
    val username: String,
    val password: String
)
