package com.dqc.kit.domain.example

import com.dqc.kit.domain.entity.UserEntity
import com.dqc.kit.domain.repository.UserRepository
import com.dqc.kit.domain.result.DomainError
import com.dqc.kit.domain.result.DomainResult
import com.dqc.kit.domain.usecase.UseCase

/**
 * 登录 UseCase
 * 处理用户登录业务逻辑
 *
 * 使用方式：
 * ```kotlin
 * val result = loginUseCase(LoginParams("user", "pass"))
 * when (result) {
 *     is DomainResult.Success -> handleSuccess(result.data)
 *     is DomainResult.Error -> handleError(result.error)
 * }
 * ```
 */
class LoginUseCase(
    private val userRepository: UserRepository
) : UseCase<LoginParams, UserEntity>() {

    override suspend fun execute(params: LoginParams): DomainResult<UserEntity> {
        // 1. 验证输入参数
        if (params.username.isBlank()) {
            return DomainResult.Error(
                DomainError.ValidationError(
                    field = "username",
                    message = "用户名不能为空"
                )
            )
        }

        if (params.password.length < 6) {
            return DomainResult.Error(
                DomainError.ValidationError(
                    field = "password",
                    message = "密码至少需要 6 位"
                )
            )
        }

        // 2. 调用 Repository 进行登录
        return when (val result = userRepository.login(params.username, params.password)) {
            is DomainResult.Success -> {
                // 登录成功，返回用户信息
                DomainResult.Success(result.data.user)
            }

            is DomainResult.Error -> {
                // 登录失败，转换错误类型
                when (result.error) {
                    is DomainError.Unauthorized -> {
                        DomainResult.Error(
                            DomainError.ValidationError(
                                message = "用户名或密码错误"
                            )
                        )
                    }

                    is DomainError.NetworkError -> {
                        DomainResult.Error(
                            DomainError.NetworkError("网络连接失败，请检查网络设置")
                        )
                    }

                    else -> result
                }
            }
        }
    }
}

/**
 * 验证登录参数 UseCase
 * 用于实时验证用户输入
 */
class ValidateLoginParamsUseCase : UseCase<LoginParams, Unit>() {

    override suspend fun execute(params: LoginParams): DomainResult<Unit> {
        val errors = mutableListOf<String>()

        if (params.username.isBlank()) {
            errors.add("用户名不能为空")
        }

        if (params.password.length < 6) {
            errors.add("密码至少需要 6 位")
        }

        return if (errors.isEmpty()) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Error(
                DomainError.ValidationError(message = errors.first())
            )
        }
    }
}
