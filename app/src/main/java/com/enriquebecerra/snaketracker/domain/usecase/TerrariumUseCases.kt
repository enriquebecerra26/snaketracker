package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.TerrariumRepository
import com.enriquebecerra.snaketracker.domain.model.TerrariumLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTerrariumLogsUseCase(private val repository: TerrariumRepository) {
    operator fun invoke(petId: Long): Flow<List<TerrariumLog>> =
        repository.getTerrariumLogsForPet(petId).map { logs -> logs.map { it.toDomain() } }
}

class SaveTerrariumLogUseCase(private val repository: TerrariumRepository) {
    suspend operator fun invoke(terrariumLog: TerrariumLog): Long =
        repository.insertTerrariumLog(terrariumLog.toEntity())
}

class DeleteTerrariumLogUseCase(private val repository: TerrariumRepository) {
    suspend operator fun invoke(terrariumLog: TerrariumLog) =
        repository.deleteTerrariumLog(terrariumLog.id)
}
