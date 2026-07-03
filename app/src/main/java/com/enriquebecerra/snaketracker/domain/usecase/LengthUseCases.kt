package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.LengthRepository
import com.enriquebecerra.snaketracker.domain.model.LengthLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetLengthLogsUseCase(private val repository: LengthRepository) {
    operator fun invoke(petId: Long): Flow<List<LengthLog>> =
        repository.getLengthLogsForPet(petId).map { logs -> logs.map { it.toDomain() } }
}

class SaveLengthLogUseCase(private val repository: LengthRepository) {
    suspend operator fun invoke(lengthLog: LengthLog): Long =
        repository.insertLengthLog(lengthLog.toEntity())
}

class DeleteLengthLogUseCase(private val repository: LengthRepository) {
    suspend operator fun invoke(lengthLog: LengthLog) =
        repository.deleteLengthLog(lengthLog.id)
}
