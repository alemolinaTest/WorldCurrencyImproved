package com.amolina.worldcurrency.domain.util

sealed class Resource<out T> {

    object Loading : Resource<Nothing>()

    data class Success<out T>(
        val data: T,
        val isFromCache: Boolean = false
    ) : Resource<T>()

    data class Error(
        val exception: Throwable,
        val message: String? = exception.localizedMessage,
        val isFromCache: Boolean = false
    ) : Resource<Nothing>()
}

