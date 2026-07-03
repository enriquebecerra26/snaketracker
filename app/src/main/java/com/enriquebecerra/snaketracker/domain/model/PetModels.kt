package com.enriquebecerra.snaketracker.domain.model

val PetSexOptions = listOf("Macho", "Hembra", "Desconocido")

data class Pet(
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

data class PetListItem(
    val pet: Pet,
    val daysSinceLastFeeding: Int?
)
