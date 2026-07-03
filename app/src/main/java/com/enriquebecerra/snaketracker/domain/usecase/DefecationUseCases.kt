package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.DefecationRepository
import com.enriquebecerra.snaketracker.domain.model.DefecationLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetDefecationLogsUseCase(private val repository: DefecationRepository) {
    operator fun invoke(petId: Long): Flow<List<DefecationLog>> =
        repository.getDefecationLogsForPet(petId).map { logs -> logs.map { it.toDomain() } }
}

class SaveDefecationLogUseCase(private val repository: DefecationRepository) {
    suspend operator fun invoke(defecationLog: DefecationLog): Long =
        repository.insertDefecationLog(defecationLog.toEntity())
}

class DeleteDefecationLogUseCase(private val repository: DefecationRepository) {
    suspend operator fun invoke(defecationLog: DefecationLog) =
        repository.deleteDefecationLog(defecationLog.id)
}
