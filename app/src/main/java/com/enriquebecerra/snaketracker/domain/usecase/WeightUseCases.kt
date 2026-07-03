package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.WeightRepository
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWeightLogsUseCase(private val repository: WeightRepository) {
    operator fun invoke(petId: Long): Flow<List<WeightLog>> =
        repository.getWeightLogsForPet(petId).map { logs -> logs.map { it.toDomain() } }
}

class SaveWeightLogUseCase(private val repository: WeightRepository) {
    suspend operator fun invoke(weightLog: WeightLog): Long =
        repository.insertWeightLog(weightLog.toEntity())
}

class DeleteWeightLogUseCase(private val repository: WeightRepository) {
    suspend operator fun invoke(weightLog: WeightLog) =
        repository.deleteWeightLog(weightLog.toEntity())
}
