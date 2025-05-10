package com.amolina.worldcurrency.domain.model

data class Currency(
    val code: String,
    val name: String,
    val rate: Double = 1.0
)