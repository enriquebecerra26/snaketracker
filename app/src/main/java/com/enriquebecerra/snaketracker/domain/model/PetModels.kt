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

val PreyTypeOptions = listOf("Ratón", "Rata", "ASF", "Conejo", "Pollito", "Otro")
val PreyConditionOptions = listOf("Viva", "Fresca", "Congelada-Descongelada")
val PreySizeOptions = listOf("Pequeña", "Mediana", "Grande", "Extra")
val DefecationTypeOptions = listOf("Normal", "Uratos", "Ambos")

data class FeedingLog(
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val time: String,
    val preyType: String,
    val preyCondition: String,
    val preySize: String,
    val preyWeightGrams: Float? = null,
    val accepted: Boolean,
    val durationMinutes: Int? = null,
    val notes: String? = null
)

data class WeightLog(
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val weight: Double,
    val notes: String? = null
)

data class LengthLog(
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val lengthCm: Float,
    val notes: String? = null
)

data class SheddingLog(
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

data class DefecationLog(
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val type: String,
    val notes: String? = null
)

data class PetListItem(
    val pet: Pet,
    val daysSinceLastFeeding: Int?
)
