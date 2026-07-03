package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.DefecationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DefecationLogDao {

    @Query("SELECT * FROM defecation_logs WHERE petId = :petId ORDER BY date DESC")
    fun getByPetId(petId: Long): Flow<List<DefecationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(defecationLog: DefecationLog): Long

    @Query("DELETE FROM defecation_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
