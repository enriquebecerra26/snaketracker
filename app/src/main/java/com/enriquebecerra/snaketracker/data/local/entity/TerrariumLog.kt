package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "terrarium_logs",
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
data class TerrariumLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val hotSpotTemp: Float? = null,
    val coldSideTemp: Float? = null,
    val humidityPercent: Int? = null,
    val substrateType: String? = null,
    val substrateChangedDate: Long? = null,
    val heatSource: String? = null,
    val notes: String? = null
)
