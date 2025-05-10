package com.amolina.worldcurrency.domain.repository

import com.amolina.worldcurrency.domain.model.Currency

interface CurrencyRepository {
    suspend fun getAvailableCurrencies(): List<Currency>
    suspend fun convert(amount: Double, from: String, to: String): Double
}