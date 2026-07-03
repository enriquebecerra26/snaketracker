package com.enriquebecerra.snaketracker.domain.model

data class Pet(
    val id: Long = 0,
    val name: String,
    val species: String,
    val birthDate: Long,
    val weight: Double,
    val photoUri: String? = null,
    val notes: String? = null
)

data class FeedingLog(
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val preyType: String,
    val preyWeight: Double,
    val accepted: Boolean,
    val notes: String? = null
)

data class WeightLog(
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val weight: Double
)
