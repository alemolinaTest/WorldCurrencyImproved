package com.amolina.worldcurrency.domain.model

data class Conversion(
    val id: Long = 0,
    val fromCode: String,
    val fromName: String,
    val toCode: String,
    val toName: String,
    val amount: Double,
    val rate: Double,
    val result: Double,
    val timestamp: Long
)