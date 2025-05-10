package com.amolina.worldcurrency.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amolina.worldcurrency.data.local.room.entity.ConversionEntity

@Dao
interface ConversionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversion: ConversionEntity)

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    suspend fun getAll(): List<ConversionEntity>

    @Query("SELECT * FROM conversion_history WHERE id = :id")
    suspend fun getById(id: Long): ConversionEntity

    @Query("DELETE FROM conversion_history WHERE id = :id")
    suspend fun deleteById(id: Long)
}
