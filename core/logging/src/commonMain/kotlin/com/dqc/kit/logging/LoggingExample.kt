package com.dqc.kit.logging

import com.dqc.kit.logging.api.LogLevel
import com.dqc.kit.logging.api.Logger
import com.dqc.kit.logging.api.LoggerConfig

/**
 * Logging 模块使用示例
 *
 * ## 1. 基本使用（静态方式）
 *
 * ```kotlin
 * // 简单日志
 * Log.d("调试信息")
 * Log.i("普通信息")
 * Log.e("错误信息", exception)
 *
 * // 带标签的日志
 * Log.tag("Network").i("请求开始")
 * Log.tag("Database").d("查询用户")
 * ```
 *
 * ## 2. 在类中使用
 *
 * ```kotlin
 * class MyRepository {
 *     private val logger = Log.of(this::class)
 *
 *     suspend fun fetchData() {
 *         logger.d("开始获取数据")
 *         try {
 *             // 网络请求...
 *             logger.i("数据获取成功")
 *         } catch (e: Exception) {
 *             logger.e("数据获取失败", e)
 *         }
 *     }
 * }
 * ```
 *
 * ## 3. 延迟计算（性能优化）
 *
 * ```kotlin
 * // 使用 lambda 形式，仅在日志级别允许时才会执行字符串拼接
 * Log.d { "用户信息: ${user.toDetailedString()}" }
 *
 * // 带异常
 * Log.e(exception) { "操作失败" }
 * ```
 *
 * ## 4. 配置日志
 *
 * ```kotlin
 * // 在 Application 初始化时配置
 * Log.config(
 *     LoggerConfig(
 *         isEnabled = true,
 *         minLogLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
 *     )
 * )
 *
 * // 或单独设置
 * Log.isEnabled = true
 * Log.minLogLevel = LogLevel.DEBUG
 * ```
 *
 * ## 5. 直接使用 Logger 接口
 *
 * ```kotlin
 * class MyService {
 *     // 创建带特定标签的 Logger
 *     private val logger: Logger = Log.tag("MyService")
 *
 *     fun doWork() {
 *         logger.i("开始工作")
 *     }
 * }
 * ```
 */
object LoggingExample {

    fun demonstrateLogging() {
        // 基本日志
        Log.v("详细日志")
        Log.d("调试日志")
        Log.i("信息日志")
        Log.w("警告日志")
        Log.e("错误日志")

        // 带异常的日志
        val exception = RuntimeException("示例异常")
        Log.e("发生错误", exception)

        // 延迟计算（推荐用于复杂日志）
        Log.d { "这是一条 ${"复杂".uppercase()} 的日志消息" }

        // 使用带标签的 Logger
        Log.tag("Network").i("网络请求开始")
        Log.tag("Database").d("数据库查询")

        // 为类创建 Logger
        val logger = Log.of("CustomTag")
        logger.i("使用工厂创建的 Logger")
    }
}

/**
 * 使用 Logger 的最佳实践
 */
object LoggingBestPractices {

    /**
     * 1. 使用延迟计算避免不必要的字符串拼接
     */
    fun lazyEvaluationExample(user: Any) {
        // ✅ 推荐：仅在 DEBUG 级别启用时执行 toString()
        Log.d { "用户信息: $user" }

        // ❌ 避免：每次都会执行 toString()，即使日志被过滤
        // Log.d("用户信息: $user")
    }

    /**
     * 2. 敏感信息脱敏
     */
    fun sensitiveDataLogging(password: String, token: String) {
        // ✅ 推荐：不记录敏感信息
        Log.i("用户登录成功")

        // ❌ 避免：记录敏感信息
        // Log.i("用户登录，密码: $password, token: $token")
    }

    /**
     * 3. 使用合适的日志级别
     */
    fun logLevelsExample() {
        Log.v("进入方法: calculate()")           // VERBOSE: 最详细的跟踪
        Log.d("计算结果: result = 42")          // DEBUG: 开发调试信息
        Log.i("用户完成购买")                  // INFO: 重要业务事件
        Log.w("网络连接缓慢")                  // WARN: 潜在问题
        Log.e("数据库连接失败")                // ERROR: 错误事件
    }

    /**
     * 4. 为不同模块使用不同标签
     */
    class UserRepository {
        private val logger = Log.of(this::class)

        fun fetchUser() {
            logger.d("获取用户信息")
        }
    }

    class OrderService {
        private val logger = Log.of("OrderService")

        fun createOrder() {
            logger.i("创建订单")
        }
    }

    /**
     * 5. 初始化配置示例
     */
    fun setupLogging(isDebug: Boolean) {
        Log.config(
            LoggerConfig(
                isEnabled = true,
                minLogLevel = if (isDebug) LogLevel.DEBUG else LogLevel.INFO
            )
        )
    }
}
