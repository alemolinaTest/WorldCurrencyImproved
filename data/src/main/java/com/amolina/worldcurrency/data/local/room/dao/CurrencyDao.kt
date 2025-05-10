package com.amolina.worldcurrency.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amolina.worldcurrency.data.local.room.entity.CurrencyRateEntity

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency_rates")
    suspend fun getAllRates(): List<CurrencyRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)
}