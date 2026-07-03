package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.ExpenseRecordDao
import com.enriquebecerra.snaketracker.data.local.entity.ExpenseRecord
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseRecordDao: ExpenseRecordDao) {

    fun getAllExpenses(): Flow<List<ExpenseRecord>> = expenseRecordDao.getAll()

    suspend fun insertExpense(expenseRecord: ExpenseRecord): Long =
        expenseRecordDao.insert(expenseRecord)

    suspend fun deleteExpense(id: Long) =
        expenseRecordDao.deleteById(id)
}
