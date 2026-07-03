package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedingLogDao {

    @Query("SELECT * FROM feeding_logs WHERE petId = :petId ORDER BY date DESC")
    fun getFeedingLogsForPet(petId: Long): Flow<List<FeedingLog>>

    @Query("SELECT * FROM feeding_logs ORDER BY date DESC")
    fun getAllFeedingLogs(): Flow<List<FeedingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedingLog(feedingLog: FeedingLog): Long

    @Update
    suspend fun updateFeedingLog(feedingLog: FeedingLog)

    @Delete
    suspend fun deleteFeedingLog(feedingLog: FeedingLog)
}
