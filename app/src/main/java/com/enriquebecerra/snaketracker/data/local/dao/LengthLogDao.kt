package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.LengthLog
import kotlinx.coroutines.flow.Flow

@Dao
interface LengthLogDao {

    @Query("SELECT * FROM length_logs WHERE petId = :petId ORDER BY date DESC")
    fun getByPetId(petId: Long): Flow<List<LengthLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lengthLog: LengthLog): Long

    @Query("DELETE FROM length_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
