package com.amolina.worldcurrency.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyDto(val code: String, val name: String, val rate: Double)

