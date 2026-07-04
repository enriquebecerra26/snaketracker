package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.DefecationRepository
import com.enriquebecerra.snaketracker.data.repository.FeedingRepository
import com.enriquebecerra.snaketracker.data.repository.HealthRepository
import com.enriquebecerra.snaketracker.data.repository.PetRepository
import com.enriquebecerra.snaketracker.data.repository.SheddingRepository
import com.enriquebecerra.snaketracker.data.repository.TerrariumRepository
import com.enriquebecerra.snaketracker.data.repository.WeightRepository
import com.enriquebecerra.snaketracker.domain.model.Alert
import com.enriquebecerra.snaketracker.domain.model.AlertIcon
import com.enriquebecerra.snaketracker.domain.model.AlertType
import com.enriquebecerra.snaketracker.domain.model.DefecationLog
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.HealthRecord
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.SheddingLog
import com.enriquebecerra.snaketracker.domain.model.TerrariumLog
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Calcula alertas de salud/bienestar por mascota combinando todos los repositorios relevantes.
 * Vive en domain/usecase (no en la capa de datos) porque encapsula reglas de negocio, no acceso
 * a datos crudo.
 */
class AlertEngine(
    private val petRepository: PetRepository,
    private val feedingRepository: FeedingRepository,
    private val weightRepository: WeightRepository,
    private val sheddingRepository: SheddingRepository,
    private val defecationRepository: DefecationRepository,
    private val healthRepository: HealthRepository,
    private val terrariumRepository: TerrariumRepository
) {
    operator fun invoke(): Flow<List<Alert>> {
        val groupA = combine(
            petRepository.getAllPets(),
            feedingRepository.getAllFeedingLogs(),
            weightRepository.getAllWeightLogs(),
            sheddingRepository.getAllSheddingLogs(),
            defecationRepository.getAllDefecationLogs()
        ) { pets, feeding, weight, shedding, defecation ->
            GroupA(
                pets = pets.map { it.toDomain() },
                feeding = feeding.map { it.toDomain() },
                weight = weight.map { it.toDomain() },
                shedding = shedding.map { it.toDomain() },
                defecation = defecation.map { it.toDomain() }
            )
        }
        val groupB = combine(
            healthRepository.getAllHealthRecords(),
            terrariumRepository.getAllTerrariumLogs()
        ) { health, terrarium ->
            GroupB(health = health.map { it.toDomain() }, terrarium = terrarium.map { it.toDomain() })
        }
        return combine(groupA, groupB) { a, b -> computeAlerts(a, b) }
    }

    private data class GroupA(
        val pets: List<Pet>,
        val feeding: List<FeedingLog>,
        val weight: List<WeightLog>,
        val shedding: List<SheddingLog>,
        val defecation: List<DefecationLog>
    )

    private data class GroupB(val health: List<HealthRecord>, val terrarium: List<TerrariumLog>)

    private fun computeAlerts(a: GroupA, b: GroupB): List<Alert> {
        val alerts = mutableListOf<Alert>()

        for (pet in a.pets) {
            val feeding = a.feeding.filter { it.petId == pet.id }
            val weight = a.weight.filter { it.petId == pet.id }
            val shedding = a.shedding.filter { it.petId == pet.id }
            val defecation = a.defecation.filter { it.petId == pet.id }
            val health = b.health.filter { it.petId == pet.id }
            val terrarium = b.terrarium.filter { it.petId == pet.id }

            feeding.maxByOrNull { it.date }?.let { last ->
                val days = daysSinceNow(last.date)
                if (days > 28) {
                    alerts += Alert(
                        petId = pet.id,
                        petName = pet.name,
                        type = AlertType.CRITICA,
                        message = "Lleva $days días sin comer",
                        icon = AlertIcon.FEEDING
                    )
                }
            }

            computeMonthlyWeightLossPercent(weight)?.let { lossPercent ->
                if (lossPercent >= 10.0) {
                    alerts += Alert(
                        petId = pet.id,
                        petName = pet.name,
                        type = AlertType.CRITICA,
                        message = "Perdió ${"%.1f".format(lossPercent)}% de peso en el último mes",
                        icon = AlertIcon.WEIGHT
                    )
                }
            }

            defecation.maxByOrNull { it.date }?.let { last ->
                val days = daysSinceNow(last.date)
                when {
                    days > 30 -> alerts += Alert(
                        petId = pet.id,
                        petName = pet.name,
                        type = AlertType.CRITICA,
                        message = "Lleva $days días sin defecar",
                        icon = AlertIcon.DEFECATION
                    )
                    days > 25 -> alerts += Alert(
                        petId = pet.id,
                        petName = pet.name,
                        type = AlertType.ADVERTENCIA,
                        message = "Lleva $days días sin defecar",
                        icon = AlertIcon.DEFECATION
                    )
                }
            }

            shedding.maxByOrNull { it.completedDate }?.let { last ->
                val days = daysSinceNow(last.completedDate)
                if (days > 60) {
                    alerts += Alert(
                        petId = pet.id,
                        petName = pet.name,
                        type = AlertType.ADVERTENCIA,
                        message = "Han pasado $days días desde la última muda",
                        icon = AlertIcon.SHEDDING
                    )
                }
            }

            val pendingCount = health.count { !it.resolved }
            if (pendingCount > 0) {
                val message = if (pendingCount == 1) {
                    "Tiene un registro de salud pendiente"
                } else {
                    "Tiene $pendingCount registros de salud pendientes"
                }
                alerts += Alert(
                    petId = pet.id,
                    petName = pet.name,
                    type = AlertType.ADVERTENCIA,
                    message = message,
                    icon = AlertIcon.HEALTH
                )
            }

            terrarium.maxByOrNull { it.date }?.let { last ->
                val hotOutOfRange = last.hotSpotTemp?.let { it < 28f || it > 32f } ?: false
                val humidityLow = last.humidityPercent?.let { it < 50 } ?: false
                if (hotOutOfRange || humidityLow) {
                    alerts += Alert(
                        petId = pet.id,
                        petName = pet.name,
                        type = AlertType.ADVERTENCIA,
                        message = "Temperatura o humedad del terrario fuera de rango",
                        icon = AlertIcon.TERRARIUM
                    )
                }
            }

            health.mapNotNull { it.nextVisitDate }.minOrNull()?.let { nextVisit ->
                val daysUntil = daysBetween(System.currentTimeMillis(), nextVisit)
                if (daysUntil in 0..7) {
                    alerts += Alert(
                        petId = pet.id,
                        petName = pet.name,
                        type = AlertType.INFO,
                        message = "Próxima visita veterinaria en $daysUntil días",
                        icon = AlertIcon.HEALTH
                    )
                }
            }
        }

        return alerts
    }

    private fun computeMonthlyWeightLossPercent(logs: List<WeightLog>): Double? {
        if (logs.size < 2) return null
        val latest = logs.maxByOrNull { it.date } ?: return null
        val thirtyDaysAgo = latest.date - java.util.concurrent.TimeUnit.DAYS.toMillis(30)
        val reference = logs
            .filter { it.id != latest.id && it.date <= thirtyDaysAgo }
            .maxByOrNull { it.date }
            ?: logs.filter { it.id != latest.id }.minByOrNull { it.date }
            ?: return null
        if (reference.weight <= 0.0) return null
        return ((reference.weight - latest.weight) / reference.weight) * 100
    }
}
