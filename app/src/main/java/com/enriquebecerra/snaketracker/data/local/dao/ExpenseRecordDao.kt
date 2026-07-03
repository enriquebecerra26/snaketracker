package com.enriquebecerra.snaketracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enriquebecerra.snaketracker.data.local.entity.ExpenseRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseRecordDao {

    @Query("SELECT * FROM expense_records ORDER BY date DESC")
    fun getAll(): Flow<List<ExpenseRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expenseRecord: ExpenseRecord): Long

    @Query("DELETE FROM expense_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
