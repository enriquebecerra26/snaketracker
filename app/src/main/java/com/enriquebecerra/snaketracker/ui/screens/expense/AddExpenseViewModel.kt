package com.enriquebecerra.snaketracker.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.ExpenseRecord
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SaveExpenseUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    getPetsUseCase: GetPetsUseCase,
    private val saveExpenseUseCase: SaveExpenseUseCase
) : ViewModel() {

    val pets: StateFlow<List<Pet>> = getPetsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveExpense(
        date: Long,
        category: String,
        description: String,
        amountMXN: Float,
        petId: Long?,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            saveExpenseUseCase(
                ExpenseRecord(
                    petId = petId,
                    date = date,
                    category = category,
                    description = description,
                    amountMXN = amountMXN,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
