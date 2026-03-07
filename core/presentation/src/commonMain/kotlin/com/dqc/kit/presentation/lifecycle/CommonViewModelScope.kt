package com.dqc.kit.presentation.lifecycle

import kotlinx.coroutines.CoroutineScope

/**
 * 通用 ViewModel 作用域接口
 * 为 Android 和 iOS 提供统一的协程作用域访问方式
 *
 * 设计原则：
 * 1. 提供统一的 viewModelScope 访问
 * 2. 提供统一的 onCleared 生命周期方法
 * 3. 与平台无关，可在 commonMain 中使用
 *
 * 使用示例：
 * ```kotlin
 * // Android 平台
 * class AndroidViewModelWrapper(
 *     private val viewModel: BaseMviViewModel<*, *, *>
 * ) : ViewModel() {
 *     override fun onCleared() {
 *         viewModel.onCleared()
 *     }
 * }
 *
 * // iOS 平台
 * class IOSViewModelWrapper {
 *     private val viewModel: BaseMviViewModel<*, *, *>
 *
 *     fun deinit() {
 *         viewModel.onCleared()
 *     }
 * }
 * ```
 */
interface CommonViewModelScope {
    /**
     * ViewModel 的协程作用域
     * 所有协程操作都应该在此作用域中执行
     */
    val viewModelScope: CoroutineScope

    /**
     * 当 ViewModel 被销毁时调用
     * 应该在此方法中取消所有协程并清理资源
     */
    fun onCleared()
}

/**
 * 生命周期状态枚举
 * 表示 ViewModel 的生命周期状态
 */
enum class LifecycleState {
    INITIALIZED,
    STARTED,
    RESUMED,
    PAUSED,
    STOPPED,
    DESTROYED
}

/**
 * 生命周期观察者接口
 * 用于观察 ViewModel 的生命周期变化
 */
interface LifecycleObserver {
    /**
     * 生命周期状态变化回调
     * @param state 新的生命周期状态
     */
    fun onStateChanged(state: LifecycleState)
}

/**
 * 生命周期持有者
 * 管理生命周期状态和观察者
 */
class LifecycleHolder {
    private var currentState: LifecycleState = LifecycleState.INITIALIZED
    private val observers = mutableListOf<LifecycleObserver>()

    /**
     * 添加生命周期观察者
     */
    fun addObserver(observer: LifecycleObserver) {
        observers.add(observer)
        // 立即通知当前状态
        observer.onStateChanged(currentState)
    }

    /**
     * 移除生命周期观察者
     */
    fun removeObserver(observer: LifecycleObserver) {
        observers.remove(observer)
    }

    /**
     * 设置生命周期状态
     */
    fun setState(state: LifecycleState) {
        if (currentState != state) {
            currentState = state
            observers.forEach { it.onStateChanged(state) }
        }
    }

    /**
     * 获取当前生命周期状态
     */
    fun getCurrentState(): LifecycleState = currentState

    /**
     * 判断当前是否处于活跃状态（STARTED 或 RESUMED）
     */
    fun isActive(): Boolean = currentState == LifecycleState.STARTED ||
            currentState == LifecycleState.RESUMED

    /**
     * 判断当前是否处于前台状态（RESUMED）
     */
    fun isResumed(): Boolean = currentState == LifecycleState.RESUMED
}
