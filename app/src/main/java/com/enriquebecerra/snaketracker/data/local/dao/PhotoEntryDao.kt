package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.PhotoEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoEntryDao {

    @Query("SELECT * FROM photo_entries WHERE petId = :petId ORDER BY date DESC")
    fun getByPetId(petId: Long): Flow<List<PhotoEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photoEntry: PhotoEntry): Long

    @Query("DELETE FROM photo_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
