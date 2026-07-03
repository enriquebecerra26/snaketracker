package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense_records",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["id"],
            childColumns = ["petId"],
            // Un gasto conserva su historial aunque se elimine la mascota asociada;
            // simplemente pasa a ser un gasto "General" (petId = null).
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("petId")]
)
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val petId: Long? = null,
    val date: Long,
    val category: String,
    val description: String,
    val amountMXN: Float,
    val notes: String? = null
)
