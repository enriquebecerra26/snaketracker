package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "defecation_logs",
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
data class DefecationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val type: String,
    val notes: String? = null
)
