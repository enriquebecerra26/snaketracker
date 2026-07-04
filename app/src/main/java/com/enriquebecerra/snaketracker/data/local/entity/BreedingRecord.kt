package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breeding_records",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["id"],
            childColumns = ["maleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("petId"), Index("maleId")]
)
data class BreedingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val maleId: Long? = null,
    val pairingDate: Long? = null,
    val ovulationDate: Long? = null,
    val layingDate: Long? = null,
    val totalEggs: Int? = null,
    val fertileEggs: Int? = null,
    val incubationStartDate: Long? = null,
    val hatchDate: Long? = null,
    val hatchlings: Int? = null,
    val incubationTempC: Float? = null,
    val incubationHumidity: Int? = null,
    val notes: String? = null
)
