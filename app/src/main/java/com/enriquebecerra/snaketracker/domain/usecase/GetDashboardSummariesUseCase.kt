package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.DefecationRepository
import com.enriquebecerra.snaketracker.data.repository.FeedingRepository
import com.enriquebecerra.snaketracker.data.repository.PetRepository
import com.enriquebecerra.snaketracker.data.repository.SheddingRepository
import com.enriquebecerra.snaketracker.data.repository.TerrariumRepository
import com.enriquebecerra.snaketracker.data.repository.WeightRepository
import com.enriquebecerra.snaketracker.domain.model.DefecationLog
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.PetDashboardSummary
import com.enriquebecerra.snaketracker.domain.model.SheddingLog
import com.enriquebecerra.snaketracker.domain.model.TerrariumLog
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetDashboardSummariesUseCase(
    private val petRepository: PetRepository,
    private val feedingRepository: FeedingRepository,
    private val weightRepository: WeightRepository,
    private val sheddingRepository: SheddingRepository,
    private val defecationRepository: DefecationRepository,
    private val terrariumRepository: TerrariumRepository
) {
    operator fun invoke(): Flow<List<PetDashboardSummary>> {
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
        return combine(groupA, terrariumRepository.getAllTerrariumLogs()) { a, terrariumEntities ->
            val terrarium = terrariumEntities.map { it.toDomain() }
            a.pets.map { pet ->
                buildSummary(
                    pet = pet,
                    feeding = a.feeding.filter { it.petId == pet.id },
                    weight = a.weight.filter { it.petId == pet.id },
                    shedding = a.shedding.filter { it.petId == pet.id },
                    defecation = a.defecation.filter { it.petId == pet.id },
                    terrarium = terrarium.filter { it.petId == pet.id }
                )
            }
        }
    }

    private data class GroupA(
        val pets: List<Pet>,
        val feeding: List<FeedingLog>,
        val weight: List<WeightLog>,
        val shedding: List<SheddingLog>,
        val defecation: List<DefecationLog>
    )

    private fun buildSummary(
        pet: Pet,
        feeding: List<FeedingLog>,
        weight: List<WeightLog>,
        shedding: List<SheddingLog>,
        defecation: List<DefecationLog>,
        terrarium: List<TerrariumLog>
    ): PetDashboardSummary {
        val currentWeight = weight.maxByOrNull { it.date }?.weight ?: pet.weight
        val reminder = computeFeedingReminder(feeding)
        val terrariumHasAlert = terrarium.maxByOrNull { it.date }?.let { last ->
            val hotOutOfRange = last.hotSpotTemp?.let { it < 28f || it > 32f } ?: false
            val humidityLow = last.humidityPercent?.let { it < 50 } ?: false
            hotOutOfRange || humidityLow
        } ?: false

        return PetDashboardSummary(
            pet = pet,
            currentWeight = currentWeight,
            weightVariationGrams = computeWeightVariationGrams(weight),
            daysSinceLastFeeding = feeding.maxByOrNull { it.date }?.let { daysSinceNow(it.date) },
            feedingIsOverdue = reminder?.isOverdue ?: false,
            nextFeedingEstimate = reminder?.estimatedDate,
            daysSinceLastShedding = shedding.maxByOrNull { it.completedDate }?.let { daysSinceNow(it.completedDate) },
            daysSinceLastDefecation = defecation.maxByOrNull { it.date }?.let { daysSinceNow(it.date) },
            terrariumHasAlert = terrariumHasAlert
        )
    }

    private data class FeedingReminder(val estimatedDate: Long, val isOverdue: Boolean)

    private fun computeFeedingReminder(logs: List<FeedingLog>): FeedingReminder? {
        if (logs.size < 2) return null
        val sorted = logs.sortedBy { it.date }
        val averageDays = averageIntervalDays(sorted.map { it.date }) ?: return null
        val lastDate = sorted.last().date
        val estimatedDate = lastDate + TimeUnit.DAYS.toMillis(averageDays.toLong())
        val daysSinceLast = daysSinceNow(lastDate)
        return FeedingReminder(estimatedDate = estimatedDate, isOverdue = daysSinceLast > averageDays + 3)
    }

    private fun computeWeightVariationGrams(logs: List<WeightLog>): Double? {
        if (logs.size < 2) return null
        val latest = logs.maxByOrNull { it.date } ?: return null
        val thirtyDaysAgo = latest.date - TimeUnit.DAYS.toMillis(30)
        val reference = logs
            .filter { it.id != latest.id && it.date <= thirtyDaysAgo }
            .maxByOrNull { it.date }
            ?: logs.filter { it.id != latest.id }.minByOrNull { it.date }
            ?: return null
        return latest.weight - reference.weight
    }
}
