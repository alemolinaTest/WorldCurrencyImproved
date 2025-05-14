package com.amolina.worldcurrency.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LatestRatesResponseDto(
    val success: Boolean,
    val terms: String? = null,
    val privacy: String? = null,
    val timestamp: Long? = null,
    val source: String?="USD",
    val quotes: Map<String, Double> = emptyMap()
)