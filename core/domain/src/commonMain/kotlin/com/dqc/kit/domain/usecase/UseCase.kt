package com.dqc.kit.domain.usecase

import com.dqc.kit.domain.result.DomainResult
import kotlinx.coroutines.flow.Flow

/**
 * UseCase 基类
 * 所有 UseCase 都应该继承此类，确保一致的错误处理
 *
 * @param P 输入参数类型
 * @param T 输出结果类型
 *
 * 使用示例：
 * ```kotlin
 * class GetUserUseCase(
 *     private val repository: UserRepository
 * ) : UseCase<String, User>() {
 *     override suspend fun execute(params: String): DomainResult<User> {
 *         return repository.getUser(params)
 *     }
 * }
 *
 * // 调用
 * val result = getUserUseCase("userId")
 * ```
 */
abstract class UseCase<in P, out T> {

    /**
     * 执行用例的核心逻辑
     * 子类实现此方法定义具体业务逻辑
     *
     * @param params 输入参数
     * @return 业务结果
     */
    protected abstract suspend fun execute(params: P): DomainResult<T>

    /**
     * 调用运算符重载，支持函数式调用
     * 自动包装异常为 DomainResult
     *
 * @param params 输入参数
     * @return 业务结果
     */
    suspend operator fun invoke(params: P): DomainResult<T> = try {
        execute(params)
    } catch (e: Throwable) {
        DomainResult.Error(com.dqc.kit.domain.result.DomainError.Unknown(e))
    }
}

/**
 * 无参数 UseCase
 *
 * @param T 输出结果类型
 *
 * 使用示例：
 * ```kotlin
 * class GetCurrentUserUseCase(
 *     private val repository: UserRepository
 * ) : NoParamsUseCase<User>() {
 *     override suspend fun execute(): DomainResult<User> {
 *         return repository.getCurrentUser()
 *     }
 * }
 * ```
 */
abstract class NoParamsUseCase<out T> : UseCase<Unit, T>() {
    protected abstract suspend fun execute(): DomainResult<T>

    override suspend fun execute(params: Unit): DomainResult<T> = execute()

    /**
     * 调用运算符重载（无参版本）
     */
    suspend operator fun invoke(): DomainResult<T> = invoke(Unit)
}

/**
 * Flow UseCase 基类
 * 用于返回数据流的业务逻辑（如实时数据、观察数据变化）
 *
 * @param P 输入参数类型
 * @param T 输出数据类型
 *
 * 使用示例：
 * ```kotlin
 * class ObserveUserUseCase(
 *     private val repository: UserRepository
 * ) : FlowUseCase<String, User>() {
 *     override fun execute(params: String): Flow<User> {
 *         return repository.observeUser(params)
 *     }
 * }
 *
 * // 收集数据流
 * observeUserUseCase("userId").collect { user -> ... }
 * ```
 */
abstract class FlowUseCase<in P, out T> {

    /**
     * 执行用例的核心逻辑
     *
     * @param params 输入参数
     * @return 数据流
     */
    protected abstract fun execute(params: P): Flow<T>

    /**
     * 调用运算符重载
     *
     * @param params 输入参数
     * @return 数据流
     */
    operator fun invoke(params: P): Flow<T> = execute(params)
}

/**
 * 无参数 Flow UseCase
 *
 * @param T 输出数据类型
 */
abstract class NoParamsFlowUseCase<out T> : FlowUseCase<Unit, T>() {
    protected abstract fun execute(): Flow<T>

    override fun execute(params: Unit): Flow<T> = execute()

    /**
     * 调用运算符重载（无参版本）
     */
    operator fun invoke(): Flow<T> = invoke(Unit)
}

/**
 * 组合 UseCase：顺序执行两个 UseCase，第一个的结果作为第二个的参数
 *
 * 使用示例：
 * ```kotlin
 * val useCase = combineUseCases(
 *     getUserIdUseCase,
 *     getUserDetailUseCase
 * )
 * val result = useCase(Unit) // 先获取用户ID，再获取详情
 * ```
 */
fun <P, M, R> combineUseCases(
    first: UseCase<P, M>,
    second: UseCase<M, R>
): suspend (P) -> DomainResult<R> = { params ->
    when (val firstResult = first(params)) {
        is DomainResult.Success -> second(firstResult.data)
        is DomainResult.Error -> DomainResult.Error(firstResult.error)
    }
}

/**
 * 组合 UseCase：并行执行两个 UseCase，结果合并为 Pair
 *
 * 使用示例：
 * ```kotlin
 * val useCase = zipUseCases(
 *     getUserUseCase,
 *     getOrdersUseCase
 * )
 * val result = useCase(Pair("userId", "userId"))
 * // result: DomainResult<Pair<User, List<Order>>>
 * ```
 */
fun <P1, P2, T1, T2> zipUseCases(
    first: UseCase<P1, T1>,
    second: UseCase<P2, T2>
): suspend (Pair<P1, P2>) -> DomainResult<Pair<T1, T2>> = { (p1, p2) ->
    val r1 = first(p1)
    val r2 = second(p2)
    r1.zip(r2)
}

/**
 * UseCase 组合操作符
 * 顺序执行两个 UseCase
 *
 * 使用示例：
 * ```kotlin
 * val combined = getUserIdUseCase andThen getUserDetailUseCase
 * ```
 */
infix fun <P, M, R> UseCase<P, M>.andThen(
    other: UseCase<M, R>
): suspend (P) -> DomainResult<R> = combineUseCases(this, other)
