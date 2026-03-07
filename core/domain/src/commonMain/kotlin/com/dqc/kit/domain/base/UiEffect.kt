package com.dqc.kit.domain.base

/**
 * MVI 架构中的副作用接口
 * 用于表示一次性事件（如 Toast、导航、Snackbar 等）
 * 这些事件不应该影响 UI 状态的持久性
 */
interface UiEffect
