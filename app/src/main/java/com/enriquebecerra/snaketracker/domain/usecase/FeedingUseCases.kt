package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.FeedingRepository
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFeedingLogsUseCase(private val repository: FeedingRepository) {
    operator fun invoke(petId: Long): Flow<List<FeedingLog>> =
        repository.getFeedingLogsForPet(petId).map { logs -> logs.map { it.toDomain() } }
}

class SaveFeedingLogUseCase(private val repository: FeedingRepository) {
    suspend operator fun invoke(feedingLog: FeedingLog): Long =
        repository.insertFeedingLog(feedingLog.toEntity())
}

class DeleteFeedingLogUseCase(private val repository: FeedingRepository) {
    suspend operator fun invoke(feedingLog: FeedingLog) =
        repository.deleteFeedingLog(feedingLog.toEntity())
}
