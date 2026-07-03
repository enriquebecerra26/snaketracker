package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shedding_logs",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("petId")]
)
data class SheddingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val bluePhaseStart: Long? = null,
    val sheddingStart: Long? = null,
    val completedDate: Long,
    val wasComplete: Boolean,
    val problems: String? = null,
    val humidityPercent: Int? = null,
    val notes: String? = null
)
