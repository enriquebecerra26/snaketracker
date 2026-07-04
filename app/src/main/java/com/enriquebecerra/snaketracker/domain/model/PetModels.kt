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

val HealthRecordTypeOptions = listOf(
    "Enfermedad",
    "Medicamento",
    "Cirugía",
    "Desparasitación",
    "Tratamiento",
    "Visita veterinario"
)
val ExpenseCategoryOptions = listOf("Alimento", "Veterinario", "Terrario", "Calefacción", "Accesorios", "Otro")

data class HealthRecord(
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

data class TerrariumLog(
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

data class ExpenseRecord(
    val id: Long = 0,
    val petId: Long? = null,
    val date: Long,
    val category: String,
    val description: String,
    val amountMXN: Float,
    val notes: String? = null
)

val PhotoEventTypeOptions = listOf("Muda", "Peso", "Mensual", "Otro")

data class PhotoEntry(
    val id: Long = 0,
    val petId: Long,
    val date: Long,
    val photoUri: String,
    val caption: String? = null,
    val eventType: String? = null
)

data class BreedingRecord(
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
