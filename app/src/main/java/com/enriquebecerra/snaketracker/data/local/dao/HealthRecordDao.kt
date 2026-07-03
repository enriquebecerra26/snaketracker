package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.HealthRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {

    @Query("SELECT * FROM health_records WHERE petId = :petId ORDER BY date DESC")
    fun getByPetId(petId: Long): Flow<List<HealthRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(healthRecord: HealthRecord): Long

    @Query("DELETE FROM health_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
