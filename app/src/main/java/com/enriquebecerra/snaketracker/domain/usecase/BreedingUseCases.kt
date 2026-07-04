package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.BreedingRepository
import com.enriquebecerra.snaketracker.domain.model.BreedingRecord
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBreedingRecordsUseCase(private val repository: BreedingRepository) {
    operator fun invoke(petId: Long): Flow<List<BreedingRecord>> =
        repository.getBreedingRecordsForPet(petId).map { records -> records.map { it.toDomain() } }
}

class SaveBreedingRecordUseCase(private val repository: BreedingRepository) {
    suspend operator fun invoke(breedingRecord: BreedingRecord): Long =
        repository.insertBreedingRecord(breedingRecord.toEntity())
}

class DeleteBreedingRecordUseCase(private val repository: BreedingRepository) {
    suspend operator fun invoke(breedingRecord: BreedingRecord) =
        repository.deleteBreedingRecord(breedingRecord.id)
}
