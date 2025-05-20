package com.amolina.worldcurrency.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorDto(
    val code: Int? = null,
    val info: String? = null
)
