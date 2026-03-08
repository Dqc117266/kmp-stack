package com.dqc.kit.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 后端通用响应格式
 * 标准 REST API 响应包装器
 *
 * @param T 实际数据类型
 * @property code 业务状态码 (非 HTTP 状态码)
 * @property data 实际响应数据
 * @property message 提示消息
 * @property success 是否成功标志
 */
@Serializable
data class BaseResponse<T>(
    @SerialName("code")
    val code: Int = 0,

    @SerialName("data")
    val data: T? = null,

    @SerialName("message")
    val message: String? = null,

    @SerialName("success")
    val success: Boolean = code == 0
)

/**
 * 分页响应包装器
 *
 * @param T 列表项类型
 * @property list 数据列表
 * @property total 总记录数
 * @property page 当前页码
 * @property pageSize 每页大小
 * @property hasMore 是否有更多数据
 */
@Serializable
data class PaginatedResponse<T>(
    @SerialName("list")
    val list: List<T> = emptyList(),

    @SerialName("total")
    val total: Long = 0,

    @SerialName("page")
    val page: Int = 1,

    @SerialName("pageSize")
    val pageSize: Int = 20,

    @SerialName("hasMore")
    val hasMore: Boolean = false
)

/**
 * 标准业务状态码定义
 */
object ResponseCodes {
    const val SUCCESS = 0
    const val ERROR_UNKNOWN = -1
    const val ERROR_NETWORK = -2
    const val ERROR_TIMEOUT = -3
    const val ERROR_UNAUTHORIZED = 401
    const val ERROR_FORBIDDEN = 403
    const val ERROR_NOT_FOUND = 404
    const val ERROR_VALIDATION = 422
    const val ERROR_SERVER = 500
}
