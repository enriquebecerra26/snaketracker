package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.SheddingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SheddingLogDao {

    @Query("SELECT * FROM shedding_logs WHERE petId = :petId ORDER BY completedDate DESC")
    fun getByPetId(petId: Long): Flow<List<SheddingLog>>

    @Query("SELECT * FROM shedding_logs ORDER BY completedDate DESC")
    fun getAll(): Flow<List<SheddingLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sheddingLog: SheddingLog): Long

    @Query("DELETE FROM shedding_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
