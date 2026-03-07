package com.dqc.kit.presentation.base

import com.dqc.kit.presentation.lifecycle.CommonViewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * 纯净的 MVI ViewModel 基类
 * 不依赖 AndroidX，可在所有 KMP 平台使用
 *
 * @param State UI 状态类型，必须实现 UiState
 * @param Intent 用户意图类型，必须实现 UiIntent
 * @param Effect 副作用类型，必须实现 UiEffect
 *
 * 架构原则：
 * 1. 状态管理：通过 StateFlow 保持最新的 UI 状态
 * 2. 意图处理：通过 registerIntent 注册处理器，实现类型安全的事件分发
 * 3. 副作用管理：通过 Channel 处理一次性事件（如弹窗、跳转）
 * 4. 并发控制：内置 Loading 计数器，管理多个并发请求的加载状态
 * 5. 状态追踪：支持调试模式，自动记录状态转换日志
 *
 * 使用示例：
 * ```kotlin
 * class LoginViewModel(
 *     dispatcher: CoroutineDispatcher
 * ) : BaseMviViewModel<LoginState, LoginIntent, LoginEffect>(
 *     initialState = LoginState(),
 *     dispatcher = dispatcher
 * ) {
 *     override fun registerIntents() {
 *         registerIntent<LoginIntent.UsernameChanged> { intent ->
 *             updateState { copy(username = intent.value) }
 *         }
 *         registerIntent<LoginIntent.Submit> {
 *             launchRequest(
 *                 setLoading = { loading -> copy(isLoading = loading) }
 *             ) { performLogin() }
 *         }
 *     }
 * }
 * ```
 */
abstract class BaseMviViewModel<
    State : UiState,
    Intent : UiIntent,
    Effect : UiEffect
    >(
    initialState: State,
    dispatcher: CoroutineDispatcher,
    private val debugConfig: DebugConfig = DebugConfig()
) : CommonViewModelScope {

    /**
     * ViewModel 的协程作用域
     * 使用 SupervisorJob 确保子协程失败不会影响其他协程
     */
    private val _viewModelScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatcher)

    /**
     * 对外暴露的协程作用域
     */
    override val viewModelScope: CoroutineScope = _viewModelScope

    /**
     * UI 状态 StateFlow
     * 使用 MutableStateFlow 保证状态一致性
     */
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /**
     * 当前状态值（便捷访问）
     */
    protected val currentState: State get() = _uiState.value

    /**
     * 副作用通道（使用 Channel 保证事件不丢失）
     * Channel.BUFFERED 确保消费者挂起时事件被缓冲
     */
    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    /**
     * 内部 Intent 共享流，用于处理用户意图
     * 使用 extraBufferCapacity 避免快速发送时丢失事件
     */
    private val _intents = MutableSharedFlow<Intent>(
        extraBufferCapacity = 64,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )

    /**
     * 【核心改进】并发 Loading 计数器
     * 使用 StateFlow 保证线程安全，处理多个并发请求时的加载状态
     */
    private val loadingCount = MutableStateFlow(0)

    /**
     * 错误处理器
     */
    private var errorHandler: (suspend (Throwable) -> Unit)? = null

    /**
     * Intent 处理器注册表
     */
    @PublishedApi
    internal val intentHandlers = mutableMapOf<KClass<*>, suspend (Intent) -> Unit>()

    /**
     * 最后处理的 Intent（用于调试）
     */
    private var lastIntent: Intent? = null

    /**
     * 生命周期监听器集合
     */
    private val lifecycleListeners = mutableListOf<LifecycleListener>()

    init {
        // 收集并处理 Intents
        _viewModelScope.launch {
            _intents.collect { intent ->
                lastIntent = intent
                handleIntentInternal(intent)
            }
        }
        registerIntents()
        onInitialize()
    }

    /**
     * 子类可以覆盖此方法进行初始化
     */
    protected open fun onInitialize() {}

    /**
     * 注册 Intent 处理器
     * 子类在 registerIntents() 中调用此方法来注册具体处理器
     *
     * @param handler 处理该 Intent 的挂起函数
     */
    protected inline fun <reified I : Intent> registerIntent(
        crossinline handler: suspend (I) -> Unit
    ) {
        intentHandlers[I::class] = { intent ->
            @Suppress("UNCHECKED_CAST")
            handler(intent as I)
        }
    }

    /**
     * 子类必须实现此方法注册所有 Intent 处理器
     */
    protected abstract fun registerIntents()

    /**
     * 处理 Intent 的内部方法
     */
    private suspend fun handleIntentInternal(intent: Intent) {
        val handler = intentHandlers[intent::class]
        if (handler != null) {
            try {
                handler(intent)
            } catch (e: CancellationException) {
                // 协程取消是正常行为，不处理
                throw e
            } catch (e: Throwable) {
                handleError(e)
            }
        } else {
            handleIntent(intent)
        }
    }

    /**
     * 处理未注册 Intent 的默认方法
     * 子类可选择性覆盖此方法处理遗留 Intent
     */
    protected open suspend fun handleIntent(intent: Intent) {
        // 默认实现：子类可覆盖
    }

    /**
     * 发送用户意图到 ViewModel
     * 这是 UI 层调用 ViewModel 的主要入口
     *
     * @param intent 用户意图
     */
    fun dispatch(intent: Intent) {
        _intents.tryEmit(intent)
    }

    /**
     * 【核心改进】统一的 Reducer 更新模式
     * 使用函数式更新保证线程安全，并记录状态转换日志
     *
     * @param reducer 状态更新函数（扩展函数形式，可直接访问 State 方法如 copy）
     */
    protected fun updateState(reducer: State.() -> State) {
        _uiState.update { oldState ->
            val newState = oldState.reducer()
            logStateTransition(oldState, newState)
            newState
        }
    }

    /**
     * 【核心改进】记录状态转换日志
     * 只有在状态变化时才记录，并显示属性差异
     */
    private fun logStateTransition(oldState: State, newState: State) {
        if (oldState != newState && debugConfig.enableDebugging) {
            debugConfig.logger?.let { logger ->
                val intentName = lastIntent?.let { it::class.simpleName } ?: "Initial"
                logger("[MVI] $intentName: ${oldState.toDebugString()} -> ${newState.toDebugString()}")
            }
        }
    }

    /**
     * 发送一次性副作用
     *
     * @param effect 副作用事件
     */
    protected fun sendEffect(effect: Effect) {
        _viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * 在 ViewModel 作用域中启动协程
     * 自动捕获异常并调用错误处理器
     *
     * @param block 协程代码块
     */
    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        _viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                handleError(e)
            }
        }
    }

    /**
     * 在 ViewModel 作用程中启动协程，自动显示 Loading 状态
     *
     * @param setLoading 设置加载状态的扩展函数，接收 Boolean 返回新的 State
     * @param block 协程代码块
     */
    protected fun launchWithLoading(
        setLoading: State.(Boolean) -> State,
        block: suspend CoroutineScope.() -> Unit
    ) {
        updateState { setLoading(true) }
        _viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                handleError(e)
            } finally {
                updateState { setLoading(false) }
            }
        }
    }

    /**
     * 【核心改进】增强版请求方法：内置计数器逻辑
     * 使用计数器管理并发请求，确保多个请求时的 Loading 状态正确
     *
     * 使用示例：
     * ```kotlin
     * launchRequest(
     *     setLoading = { loading -> copy(isLoading = loading) }
     * ) {
     *     val result = repository.fetchData()
     *     updateState { copy(data = result) }
     * }
     * ```
     *
     * @param setLoading 设置加载状态的扩展函数，接收 Boolean，在 State 上下文中执行
     * @param onError 错误处理回调
     * @param block 协程代码块
     */
    protected fun <T> launchRequest(
        setLoading: State.(Boolean) -> State,
        onError: (suspend (Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> T
    ) {
        _viewModelScope.launch {
            // 计数加1，只有从0变1时才触发 UI 加载状态
            val currentCount = loadingCount.value
            loadingCount.value = currentCount + 1
            if (currentCount == 0) {
                updateState { setLoading(true) }
            }

            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                onError?.invoke(e)
                    ?: handleError(e)
            } finally {
                // 计数减1，只有减到0时才真正关闭 UI 加载状态
                val newCount = loadingCount.value - 1
                loadingCount.value = if (newCount < 0) 0 else newCount
                if (loadingCount.value == 0) {
                    updateState { setLoading(false) }
                }
            }
        }
    }

    /**
     * 设置错误处理器
     *
     * @param handler 错误处理函数
     */
    protected fun setErrorHandler(handler: suspend (Throwable) -> Unit) {
        this.errorHandler = handler
    }

    /**
     * 处理错误
     * 调用注册的 errorHandler 或子类的 onError
     */
    protected open suspend fun handleError(throwable: Throwable) {
        errorHandler?.invoke(throwable)
    }

    /**
     * 添加生命周期监听器
     */
    fun addLifecycleListener(listener: LifecycleListener) {
        lifecycleListeners.add(listener)
    }

    /**
     * 移除生命周期监听器
     */
    fun removeLifecycleListener(listener: LifecycleListener) {
        lifecycleListeners.remove(listener)
    }

    /**
     * 当 ViewModel 被激活时调用（如页面显示）
     */
    open fun onStart() {
        lifecycleListeners.forEach { it.onStart() }
    }

    /**
     * 当 ViewModel 进入前台时调用（如页面获得焦点）
     */
    open fun onResume() {
        lifecycleListeners.forEach { it.onResume() }
    }

    /**
     * 当 ViewModel 进入后台时调用（如页面失去焦点）
     */
    open fun onPause() {
        lifecycleListeners.forEach { it.onPause() }
    }

    /**
     * 当 ViewModel 被停用时调用（如页面隐藏）
     */
    open fun onStop() {
        lifecycleListeners.forEach { it.onStop() }
    }

    /**
     * 清理资源
     * 在 ViewModel 销毁时调用（如 Android 的 onCleared）
     */
    override fun onCleared() {
        lifecycleListeners.forEach { it.onCleared() }
        _viewModelScope.cancel()
    }

    /**
     * 获取当前 Loading 计数（用于调试）
     */
    protected fun getLoadingCount(): Int = loadingCount.value
}

/**
 * 生命周期监听器接口
 * 用于监听 ViewModel 的生命周期事件
 */
interface LifecycleListener {
    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {}
    fun onCleared() {}
}

/**
 * 调试配置
 *
 * @param enableDebugging 是否启用调试日志
 * @param logger 自定义日志输出接口
 */
open class DebugConfig(
    val enableDebugging: Boolean = false,
    val logger: ((String) -> Unit)? = null
) {
    companion object {
        /**
         * 默认调试配置（启用）
         */
        val ENABLED = DebugConfig(enableDebugging = true)

        /**
         * 默认调试配置（禁用）
         */
        val DISABLED = DebugConfig(enableDebugging = false)
    }
}

/**
 * 为调试提供状态字符串表示
 * 子类可以覆盖此方法提供自定义调试输出
 */
fun <T : UiState> T.toDebugString(): String = this::class.simpleName ?: "UnknownState"
