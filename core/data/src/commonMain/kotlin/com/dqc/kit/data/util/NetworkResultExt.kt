package com.dqc.kit.data.util

import com.dqc.kit.data.network.dto.BaseResponse
import com.dqc.kit.domain.result.DomainError
import com.dqc.kit.domain.result.DomainResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

/**
 * 网络结果扩展函数
 * 将 Ktor 调用和 BaseResponse 包装为 DomainResult
 */

/**
 * 执行 HTTP 请求并返回 DomainResult
 * 自动处理 BaseResponse 包装和解包
 *
 * @param T 目标数据类型
 * @param block Ktor HTTP 请求块
 * @return DomainResult<T>
 */
internal suspend inline fun <reified T> HttpClient.executeAndMap(
    crossinline block: suspend HttpClient.() -> HttpResponse
): DomainResult<T> {
    return try {
        val response = block()

        if (!response.status.isSuccess()) {
            return DomainResult.Error(
                DomainError.NetworkError("HTTP ${response.status}")
            )
        }

        // 解析 BaseResponse
        val baseResponse: BaseResponse<T> = response.body()

        if (baseResponse.success) {
            baseResponse.data?.let {
                DomainResult.Success(it)
            } ?: DomainResult.Error(
                DomainError.Unknown(message = "Empty response data")
            )
        } else {
            DomainResult.Error(
                ErrorMapper.mapBusinessCode(
                    baseResponse.code,
                    baseResponse.message
                )
            )
        }
    } catch (e: Throwable) {
        DomainResult.Error(ErrorMapper.map(e))
    }
}

/**
 * 执行 HTTP 请求并映射为领域实体
 * 支持 DTO 到实体的转换
 *
 * @param D DTO 类型
 * @param T 领域实体类型
 * @param block Ktor HTTP 请求块
 * @param transform DTO 到实体的转换函数
 * @return DomainResult<T>
 */
internal suspend inline fun <reified D, T> HttpClient.executeAndMapToDomain(
    crossinline block: suspend HttpClient.() -> HttpResponse,
    crossinline transform: (D) -> T
): DomainResult<T> {
    return try {
        val response = block()

        if (!response.status.isSuccess()) {
            return DomainResult.Error(
                DomainError.NetworkError("HTTP ${response.status}")
            )
        }

        // 解析 BaseResponse<DTO>
        val baseResponse: BaseResponse<D> = response.body()

        if (baseResponse.success) {
            baseResponse.data?.let {
                DomainResult.Success(transform(it))
            } ?: DomainResult.Error(
                DomainError.Unknown(message = "Empty response data")
            )
        } else {
            DomainResult.Error(
                ErrorMapper.mapBusinessCode(
                    baseResponse.code,
                    baseResponse.message
                )
            )
        }
    } catch (e: Throwable) {
        DomainResult.Error(ErrorMapper.map(e))
    }
}

/**
 * 执行无需返回值的 HTTP 请求 (如 DELETE, POST 无响应体)
 */
internal suspend inline fun HttpClient.executeUnit(
    crossinline block: suspend HttpClient.() -> HttpResponse
): DomainResult<Unit> {
    return try {
        val response = block()

        if (response.status.isSuccess()) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Error(
                DomainError.NetworkError("HTTP ${response.status}")
            )
        }
    } catch (e: Throwable) {
        DomainResult.Error(ErrorMapper.map(e))
    }
}

/**
 * 执行请求并解析为 BaseResponse<Unit>
 */
internal suspend inline fun HttpClient.executeBaseUnit(
    crossinline block: suspend HttpClient.() -> HttpResponse
): DomainResult<Unit> {
    return try {
        val response = block()
        val baseResponse: BaseResponse<Unit> = response.body()

        if (baseResponse.success) {
            DomainResult.Success(Unit)
        } else {
            DomainResult.Error(
                ErrorMapper.mapBusinessCode(
                    baseResponse.code,
                    baseResponse.message
                )
            )
        }
    } catch (e: Throwable) {
        DomainResult.Error(ErrorMapper.map(e))
    }
}

/**
 * DomainResult 扩展：在成功时执行副作用（用于缓存等操作）
 */
internal inline fun <T> DomainResult<T>.onSuccessSave(
    action: (T) -> Unit
): DomainResult<T> {
    if (this is DomainResult.Success) {
        action(data)
    }
    return this
}
