package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.BreedingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingRecordDao {

    @Query("SELECT * FROM breeding_records WHERE petId = :petId ORDER BY id DESC")
    fun getByPetId(petId: Long): Flow<List<BreedingRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(breedingRecord: BreedingRecord): Long

    @Query("DELETE FROM breeding_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
