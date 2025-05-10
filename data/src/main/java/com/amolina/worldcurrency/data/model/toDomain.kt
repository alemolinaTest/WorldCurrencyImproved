package com.amolina.worldcurrency.data.model

import com.amolina.worldcurrency.data.local.room.entity.CurrencyRateEntity
import com.amolina.worldcurrency.domain.model.Currency

fun CurrencyDto.toEntity(): CurrencyRateEntity =
    CurrencyRateEntity(
        code = code,
        name = name,
        rate = rate
    )

fun CurrencyDto.toDomain(): Currency = Currency(code, name, rate)

fun Map.Entry<String, String>.toCurrency(): Currency =
    Currency(code = this.key, name = this.value)

fun Map.Entry<String, Double>.toRateEntity(): CurrencyRateEntity =
    CurrencyRateEntity(
        code = this.key, name = this.key, rate = this.value
    )

