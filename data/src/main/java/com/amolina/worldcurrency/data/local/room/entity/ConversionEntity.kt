package com.amolina.worldcurrency.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amolina.worldcurrency.domain.model.Conversion

@Entity(tableName = "conversion_history")
data class ConversionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromCode: String,
    val fromName: String,
    val toCode: String,
    val toName: String,
    val amount: Double,
    val rate: Double,
    val result: Double,
    val timestamp: Long = System.currentTimeMillis()
)
fun ConversionEntity.toDomain(): Conversion = Conversion(
    id = id,
    fromCode = fromCode,
    fromName = fromName,
    toCode = toCode,
    toName = toName,
    amount = amount,
    rate = rate,
    result = result,
    timestamp = timestamp
)

fun Conversion.toEntity(): ConversionEntity = ConversionEntity(
    id = id,
    fromCode = fromCode,
    fromName = fromName,
    toCode = toCode,
    toName = toName,
    amount = amount,
    rate = rate,
    result = result,
    timestamp = timestamp
)
