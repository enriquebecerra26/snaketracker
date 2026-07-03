package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_records",
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
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val type: String,
    val title: String,
    val description: String? = null,
    val vetName: String? = null,
    val medication: String? = null,
    val dosage: String? = null,
    val nextVisitDate: Long? = null,
    val resolved: Boolean = false
)
