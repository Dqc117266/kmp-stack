package com.dqc.kit.network.data

/**
 * Sealed class for network operation results
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int? = null, val message: String) : NetworkResult<Nothing>()
}