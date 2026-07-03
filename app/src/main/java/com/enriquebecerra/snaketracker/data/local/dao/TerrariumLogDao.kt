package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.TerrariumLog
import kotlinx.coroutines.flow.Flow

@Dao
interface TerrariumLogDao {

    @Query("SELECT * FROM terrarium_logs WHERE petId = :petId ORDER BY date DESC")
    fun getByPetId(petId: Long): Flow<List<TerrariumLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(terrariumLog: TerrariumLog): Long

    @Query("DELETE FROM terrarium_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
