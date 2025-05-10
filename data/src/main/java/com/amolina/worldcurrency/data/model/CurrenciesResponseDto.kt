package com.amolina.worldcurrency.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrenciesResponseDto(
    val success: Boolean,
    val currencies: Map<String, String>
)
