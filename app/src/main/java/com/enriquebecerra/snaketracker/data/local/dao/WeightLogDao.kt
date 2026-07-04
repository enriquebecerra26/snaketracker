package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.WeightLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightLogDao {

    @Query("SELECT * FROM weight_logs WHERE petId = :petId ORDER BY date DESC")
    fun getWeightLogsForPet(petId: Long): Flow<List<WeightLog>>

    @Query("SELECT * FROM weight_logs ORDER BY date DESC")
    fun getAllWeightLogs(): Flow<List<WeightLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weightLog: WeightLog): Long

    @Delete
    suspend fun deleteWeightLog(weightLog: WeightLog)
}
