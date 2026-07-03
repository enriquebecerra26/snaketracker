package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: String,
    val sex: String = "Desconocido",
    val morph: String = "",
    val birthDate: Long,
    val acquisitionDate: Long? = null,
    val weight: Double,
    val photoUri: String? = null,
    val breeder: String? = null,
    val chipNumber: String? = null,
    val notes: String? = null
)
