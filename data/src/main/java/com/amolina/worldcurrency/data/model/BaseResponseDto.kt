package com.amolina.worldcurrency.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponseDto<T>(
    val success: Boolean = true,
    val error: ApiErrorDto? = null,
    val source: String? = null,
    val quotes: T? = null,
    val currencies: T? = null // opcional: por si querés usarlo en /list también
)

typealias LatestRatesResponseDto = BaseResponseDto<Map<String, Double>>
typealias CurrenciesResponseDto = BaseResponseDto<Map<String, String>>
