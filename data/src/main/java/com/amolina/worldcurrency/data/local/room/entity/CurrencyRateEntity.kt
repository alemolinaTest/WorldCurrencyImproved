package com.amolina.worldcurrency.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amolina.worldcurrency.domain.model.Currency

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey val code: String,
    val name: String,
    val rate: Double
)

fun Currency.toEntity(): CurrencyRateEntity = CurrencyRateEntity(code, name, rate)
fun CurrencyRateEntity.toDomain(): Currency = Currency(code, name, rate)