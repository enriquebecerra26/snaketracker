package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.FeedingLogDao
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog
import kotlinx.coroutines.flow.Flow

class FeedingRepository(private val feedingLogDao: FeedingLogDao) {

    fun getFeedingLogsForPet(petId: Long): Flow<List<FeedingLog>> =
        feedingLogDao.getFeedingLogsForPet(petId)

    suspend fun insertFeedingLog(feedingLog: FeedingLog): Long =
        feedingLogDao.insertFeedingLog(feedingLog)

    suspend fun updateFeedingLog(feedingLog: FeedingLog) =
        feedingLogDao.updateFeedingLog(feedingLog)

    suspend fun deleteFeedingLog(feedingLog: FeedingLog) =
        feedingLogDao.deleteFeedingLog(feedingLog)
}
