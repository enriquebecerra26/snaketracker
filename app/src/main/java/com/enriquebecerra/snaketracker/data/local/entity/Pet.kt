package com.enriquebecerra.snaketracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val species: String,
    val birthDate: Long,
    val weight: Double,
    val photoUri: String? = null,
    val notes: String? = null
)
