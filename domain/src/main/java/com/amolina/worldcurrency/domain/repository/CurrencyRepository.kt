package com.amolina.worldcurrency.domain.repository

import com.amolina.worldcurrency.domain.model.Conversion
import com.amolina.worldcurrency.domain.model.Currency



interface CurrencyRepository {
    suspend fun getAvailableCurrencies(): List<Currency>
    suspend fun convert(amount: Double, from: String, to: String): Double
    suspend fun saveConversion(entity: Conversion)
    suspend fun getConversionHistory(): List<Conversion>
    suspend fun getConversionById(id: Long): Conversion
    //suspend fun deleteConversion(id: Long)

}