package com.amolina.worldcurrency.domain.repository

import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.model.Currency
import com.amolina.worldcurrency.domain.util.Resource


interface CurrencyRepository {
    suspend fun getAvailableCurrencies(): Resource<List<Currency>>
    suspend fun convert(amount: Double, from: String, to: String): Resource<Double>
    suspend fun saveConversion(entity: Conversion)
    suspend fun getConversionHistory(): List<Conversion>
    suspend fun getConversionById(id: Long): Conversion
}