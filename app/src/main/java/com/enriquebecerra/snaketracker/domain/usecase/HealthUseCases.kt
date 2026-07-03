package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.HealthRepository
import com.enriquebecerra.snaketracker.domain.model.HealthRecord
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetHealthRecordsUseCase(private val repository: HealthRepository) {
    operator fun invoke(petId: Long): Flow<List<HealthRecord>> =
        repository.getHealthRecordsForPet(petId).map { logs -> logs.map { it.toDomain() } }
}

class SaveHealthRecordUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(healthRecord: HealthRecord): Long =
        repository.insertHealthRecord(healthRecord.toEntity())
}

class DeleteHealthRecordUseCase(private val repository: HealthRepository) {
    suspend operator fun invoke(healthRecord: HealthRecord) =
        repository.deleteHealthRecord(healthRecord.id)
}
