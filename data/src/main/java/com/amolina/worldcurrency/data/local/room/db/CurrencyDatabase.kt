package com.amolina.worldcurrency.data.local.room.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amolina.worldcurrency.data.local.room.dao.ConversionDao
import com.amolina.worldcurrency.data.local.room.dao.CurrencyDao
import com.amolina.worldcurrency.data.local.room.entity.ConversionEntity
import com.amolina.worldcurrency.data.local.room.entity.CurrencyRateEntity


@Database(entities = [CurrencyRateEntity::class, ConversionEntity::class], version = 1, exportSchema = false)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun conversionDao(): ConversionDao
}

