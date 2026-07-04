package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.DefecationRepository
import com.enriquebecerra.snaketracker.data.repository.ExpenseRepository
import com.enriquebecerra.snaketracker.data.repository.FeedingRepository
import com.enriquebecerra.snaketracker.data.repository.HealthRepository
import com.enriquebecerra.snaketracker.data.repository.LengthRepository
import com.enriquebecerra.snaketracker.data.repository.PetRepository
import com.enriquebecerra.snaketracker.data.repository.SheddingRepository
import com.enriquebecerra.snaketracker.data.repository.TerrariumRepository
import com.enriquebecerra.snaketracker.data.repository.WeightRepository
import com.enriquebecerra.snaketracker.domain.model.CalendarEvent
import com.enriquebecerra.snaketracker.domain.model.CalendarEventType
import com.enriquebecerra.snaketracker.domain.model.DefecationLog
import com.enriquebecerra.snaketracker.domain.model.ExpenseRecord
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.HealthRecord
import com.enriquebecerra.snaketracker.domain.model.LengthLog
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.SheddingLog
import com.enriquebecerra.snaketracker.domain.model.TerrariumLog
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetCalendarEventsUseCase(
    private val petRepository: PetRepository,
    private val feedingRepository: FeedingRepository,
    private val weightRepository: WeightRepository,
    private val lengthRepository: LengthRepository,
    private val sheddingRepository: SheddingRepository,
    private val defecationRepository: DefecationRepository,
    private val healthRepository: HealthRepository,
    private val terrariumRepository: TerrariumRepository,
    private val expenseRepository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<CalendarEvent>> {
        val groupA = combine(
            petRepository.getAllPets(),
            feedingRepository.getAllFeedingLogs(),
            weightRepository.getAllWeightLogs(),
            lengthRepository.getAllLengthLogs(),
            sheddingRepository.getAllSheddingLogs()
        ) { pets, feeding, weight, length, shedding ->
            GroupA(
                pets = pets.map { it.toDomain() },
                feeding = feeding.map { it.toDomain() },
                weight = weight.map { it.toDomain() },
                length = length.map { it.toDomain() },
                shedding = shedding.map { it.toDomain() }
            )
        }
        val groupB = combine(
            defecationRepository.getAllDefecationLogs(),
            healthRepository.getAllHealthRecords(),
            terrariumRepository.getAllTerrariumLogs(),
            expenseRepository.getAllExpenses()
        ) { defecation, health, terrarium, expenses ->
            GroupB(
                defecation = defecation.map { it.toDomain() },
                health = health.map { it.toDomain() },
                terrarium = terrarium.map { it.toDomain() },
                expenses = expenses.map { it.toDomain() }
            )
        }
        return combine(groupA, groupB) { a, b -> buildEvents(a, b) }
    }

    private data class GroupA(
        val pets: List<Pet>,
        val feeding: List<FeedingLog>,
        val weight: List<WeightLog>,
        val length: List<LengthLog>,
        val shedding: List<SheddingLog>
    )

    private data class GroupB(
        val defecation: List<DefecationLog>,
        val health: List<HealthRecord>,
        val terrarium: List<TerrariumLog>,
        val expenses: List<ExpenseRecord>
    )

    private fun buildEvents(a: GroupA, b: GroupB): List<CalendarEvent> {
        val petNames = a.pets.associateBy({ it.id }, { it.name })
        val events = mutableListOf<CalendarEvent>()

        a.feeding.forEach { log ->
            events += CalendarEvent(
                date = log.date,
                type = CalendarEventType.FEEDING,
                title = "Alimentación: ${log.preyType}",
                petName = petNames[log.petId]
            )
        }
        a.weight.forEach { log ->
            events += CalendarEvent(
                date = log.date,
                type = CalendarEventType.BIOMETRICS,
                title = "Peso registrado",
                petName = petNames[log.petId]
            )
        }
        a.length.forEach { log ->
            events += CalendarEvent(
                date = log.date,
                type = CalendarEventType.BIOMETRICS,
                title = "Longitud registrada",
                petName = petNames[log.petId]
            )
        }
        a.shedding.forEach { log ->
            events += CalendarEvent(
                date = log.completedDate,
                type = CalendarEventType.SHEDDING,
                title = if (log.wasComplete) "Muda completa" else "Muda incompleta",
                petName = petNames[log.petId]
            )
        }
        b.defecation.forEach { log ->
            events += CalendarEvent(
                date = log.date,
                type = CalendarEventType.DEFECATION,
                title = "Defecación (${log.type})",
                petName = petNames[log.petId]
            )
        }
        b.health.forEach { record ->
            events += CalendarEvent(
                date = record.date,
                type = CalendarEventType.HEALTH,
                title = "${record.type}: ${record.title}",
                petName = petNames[record.petId]
            )
            record.nextVisitDate?.let { nextVisit ->
                events += CalendarEvent(
                    date = nextVisit,
                    type = CalendarEventType.HEALTH,
                    title = "Próxima visita: ${record.title}",
                    petName = petNames[record.petId]
                )
            }
        }
        b.terrarium.forEach { log ->
            events += CalendarEvent(
                date = log.date,
                type = CalendarEventType.TERRARIUM,
                title = "Registro de terrario",
                petName = petNames[log.petId]
            )
        }
        b.expenses.forEach { expense ->
            events += CalendarEvent(
                date = expense.date,
                type = CalendarEventType.EXPENSE,
                title = "${expense.category}: ${expense.description}",
                petName = expense.petId?.let { petNames[it] } ?: "General"
            )
        }

        return events
    }
}
