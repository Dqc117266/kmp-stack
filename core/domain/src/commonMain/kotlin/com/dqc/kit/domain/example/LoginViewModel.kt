package com.dqc.kit.domain.example

import com.dqc.kit.domain.base.BaseMviViewModel
import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay

/**
 * 登录 ViewModel 示例
 * 演示如何使用 BaseMviViewModel
 *
 * 使用方式：
 * ```kotlin
 * // 在 UI 层（Android Activity/Fragment 或 Compose）
 * val viewModel = LoginViewModel(
 *     loginUseCase = loginUseCase,
 *     validateUseCase = validateUseCase,
 *     dispatcher = Dispatchers.Main
 * )
 *
 * // 观察状态
 * viewModel.uiState.collect { state ->
 *     when {
 *         state.isLoading -> showLoading()
 *         state.user != null -> showUser(state.user)
 *         state.errorMessage != null -> showError(state.errorMessage)
 *         else -> showIdle()
 *     }
 * }
 *
 * // 观察副作用
 * viewModel.effect.collect { effect ->
 *     when (effect) {
 *         is LoginUiEffect.NavigateToHome -> navigateToHome()
 *         is LoginUiEffect.ShowError -> showToast(effect.message)
 *         // ...
 *     }
 * }
 *
 * // 发送意图
 * viewModel.dispatch(LoginUiIntent.UsernameChanged("user"))
 * viewModel.dispatch(LoginUiIntent.SubmitLogin)
 * ```
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validateUseCase: ValidateLoginParamsUseCase,
    dispatcher: CoroutineDispatcher
) : BaseMviViewModel<LoginUiState, LoginUiIntent, LoginUiEffect>(
    initialState = LoginUiState(),
    dispatcher = dispatcher
) {

    override fun registerIntents() {
        // 注册用户名变更处理器
        registerIntent<LoginUiIntent.UsernameChanged> { intent ->
            updateState {
                copy(
                    username = intent.value,
                    errorMessage = null // 清除错误
                )
            }
        }

        // 注册密码变更处理器
        registerIntent<LoginUiIntent.PasswordChanged> { intent ->
            updateState {
                copy(
                    password = intent.value,
                    errorMessage = null // 清除错误
                )
            }
        }

        // 注册登录提交处理器 - 使用 launchRequest 管理 Loading 状态
        registerIntent<LoginUiIntent.SubmitLogin> {
            performLoginWithRequest()
        }

        // 注册导航到注册页处理器
        registerIntent<LoginUiIntent.NavigateToRegister> {
            sendEffect(LoginUiEffect.NavigateToRegister)
        }

        // 注册清除错误处理器
        registerIntent<LoginUiIntent.ClearError> {
            updateState {
                copy(errorMessage = null)
            }
        }
    }

    /**
     * 使用 launchRequest 实现的登录逻辑
     * 自动管理 Loading 计数器
     */
    private fun performLoginWithRequest() {
        val current = currentState

        // 验证表单
        if (!current.isFormValid) {
            updateState {
                copy(errorMessage = "请填写正确的用户名和密码")
            }
            return
        }

        // 使用 launchRequest 启动请求，自动管理 Loading 状态
        launchRequest(
            setLoading = { loading ->
                copy(
                    isLoading = loading,
                    errorMessage = if (loading) null else errorMessage
                )
            },
            onError = { error ->
                updateState {
                    copy(errorMessage = error.message)
                }
                sendEffect(LoginUiEffect.ShowError(error.message ?: "登录失败"))
            }
        ) {
            // 执行登录
            val params = LoginParams(current.username, current.password)

            when (val result = loginUseCase(params)) {
                is DomainResult.Success -> {
                    updateState {
                        copy(user = result.data)
                    }
                    sendEffect(LoginUiEffect.ShowSuccess("登录成功！"))
                    delay(500) // 短暂延迟让用户看到成功提示
                    sendEffect(LoginUiEffect.NavigateToHome)
                }

                is DomainResult.Error -> {
                    updateState {
                        copy(errorMessage = result.error.message)
                    }
                    sendEffect(LoginUiEffect.ShowError(result.error.message))
                }
            }
        }
    }

    /**
     * 传统方式实现的登录逻辑（不使用 launchRequest）
     */
    private suspend fun performLogin() {
        val current = currentState

        // 验证表单
        if (!current.isFormValid) {
            updateState {
                copy(errorMessage = "请填写正确的用户名和密码")
            }
            return
        }

        // 设置加载状态
        updateState {
            copy(isLoading = true, errorMessage = null)
        }

        // 执行登录
        val params = LoginParams(current.username, current.password)

        when (val result = loginUseCase(params)) {
            is DomainResult.Success -> {
                updateState {
                    copy(
                        isLoading = false,
                        user = result.data
                    )
                }
                sendEffect(LoginUiEffect.ShowSuccess("登录成功！"))
                delay(500) // 短暂延迟让用户看到成功提示
                sendEffect(LoginUiEffect.NavigateToHome)
            }

            is DomainResult.Error -> {
                updateState {
                    copy(
                        isLoading = false,
                        errorMessage = result.error.message
                    )
                }
                sendEffect(LoginUiEffect.ShowError(result.error.message))
            }
        }
    }
}

// ============================================================================
// 在 Android Compose 中使用示例
// ============================================================================

/**
 * ```kotlin
 * @Composable
 * fun LoginScreen(
 *     viewModel: LoginViewModel = koinViewModel()
 * ) {
 *     val state by viewModel.uiState.collectAsState()
 *     val context = LocalContext.current
 *
 *     // 处理副作用
 *     LaunchedEffect(Unit) {
 *         viewModel.effect.collect { effect ->
 *             when (effect) {
 *                 is LoginUiEffect.NavigateToHome -> {
 *                     navigator.navigateToHome()
 *                 }
 *                 is LoginUiEffect.NavigateToRegister -> {
 *                     navigator.navigateToRegister()
 *                 }
 *                 is LoginUiEffect.ShowError -> {
 *                     Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
 *                 }
 *                 is LoginUiEffect.ShowSuccess -> {
 *                     Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
 *                 }
 *             }
 *         }
 *     }
 *
 *     // UI 布局
 *     Column(
 *         modifier = Modifier
 *             .fillMaxSize()
 *             .padding(16.dp),
 *         verticalArrangement = Arrangement.Center
 *     ) {
 *         // 用户名输入
 *         OutlinedTextField(
 *             value = state.username,
 *             onValueChange = {
 *                 viewModel.dispatch(LoginUiIntent.UsernameChanged(it))
 *             },
 *             label = { Text("用户名") },
 *             modifier = Modifier.fillMaxWidth()
 *         )
 *
 *         Spacer(modifier = Modifier.height(8.dp))
 *
 *         // 密码输入
 *         OutlinedTextField(
 *             value = state.password,
 *             onValueChange = {
 *                 viewModel.dispatch(LoginUiIntent.PasswordChanged(it))
 *             },
 *             label = { Text("密码") },
 *             visualTransformation = PasswordVisualTransformation(),
 *             modifier = Modifier.fillMaxWidth()
 *         )
 *
 *         // 错误提示
 *         if (state.errorMessage != null) {
 *             Spacer(modifier = Modifier.height(8.dp))
 *             Text(
 *                 text = state.errorMessage!!,
 *                 color = MaterialTheme.colorScheme.error
 *             )
 *         }
 *
 *         Spacer(modifier = Modifier.height(16.dp))
 *
 *         // 登录按钮
 *         Button(
 *             onClick = { viewModel.dispatch(LoginUiIntent.SubmitLogin) },
 *             enabled = state.canSubmit,
 *             modifier = Modifier.fillMaxWidth()
 *         ) {
 *             if (state.isLoading) {
 *                 CircularProgressIndicator(
 *                     modifier = Modifier.size(24.dp),
 *                     color = MaterialTheme.colorScheme.onPrimary
 *                 )
 *             } else {
 *                 Text("登录")
 *             }
 *         }
 *
 *         Spacer(modifier = Modifier.height(8.dp))
 *
 *         // 注册链接
 *         TextButton(
 *             onClick = { viewModel.dispatch(LoginUiIntent.NavigateToRegister) },
 *             modifier = Modifier.fillMaxWidth()
 *         ) {
 *             Text("还没有账号？去注册")
 *         }
 *     }
 * }
 * ```
 */
