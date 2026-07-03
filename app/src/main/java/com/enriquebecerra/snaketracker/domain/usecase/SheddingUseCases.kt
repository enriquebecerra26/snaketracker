package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.SheddingRepository
import com.enriquebecerra.snaketracker.domain.model.SheddingLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSheddingLogsUseCase(private val repository: SheddingRepository) {
    operator fun invoke(petId: Long): Flow<List<SheddingLog>> =
        repository.getSheddingLogsForPet(petId).map { logs -> logs.map { it.toDomain() } }
}

class SaveSheddingLogUseCase(private val repository: SheddingRepository) {
    suspend operator fun invoke(sheddingLog: SheddingLog): Long =
        repository.insertSheddingLog(sheddingLog.toEntity())
}

class DeleteSheddingLogUseCase(private val repository: SheddingRepository) {
    suspend operator fun invoke(sheddingLog: SheddingLog) =
        repository.deleteSheddingLog(sheddingLog.id)
}
