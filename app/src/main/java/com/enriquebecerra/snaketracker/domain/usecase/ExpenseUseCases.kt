package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.ExpenseRepository
import com.enriquebecerra.snaketracker.domain.model.ExpenseRecord
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetExpensesUseCase(private val repository: ExpenseRepository) {
    operator fun invoke(): Flow<List<ExpenseRecord>> =
        repository.getAllExpenses().map { logs -> logs.map { it.toDomain() } }
}

class SaveExpenseUseCase(private val repository: ExpenseRepository) {
    suspend operator fun invoke(expenseRecord: ExpenseRecord): Long =
        repository.insertExpense(expenseRecord.toEntity())
}

class DeleteExpenseUseCase(private val repository: ExpenseRepository) {
    suspend operator fun invoke(expenseRecord: ExpenseRecord) =
        repository.deleteExpense(expenseRecord.id)
}
