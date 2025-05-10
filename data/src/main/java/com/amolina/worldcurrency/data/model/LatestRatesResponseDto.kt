package com.amolina.worldcurrency.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LatestRatesResponseDto(
    val success: Boolean,
    val timestamp: Long,
    val source: String,
    val quotes: Map<String, Double>
)