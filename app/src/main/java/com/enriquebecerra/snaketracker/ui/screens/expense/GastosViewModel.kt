package com.enriquebecerra.snaketracker.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.ExpenseRecord
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.GetExpensesUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GastosViewModel(
    getExpensesUseCase: GetExpensesUseCase,
    getPetsUseCase: GetPetsUseCase
) : ViewModel() {

    val expenses: StateFlow<List<ExpenseRecord>> = getExpensesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pets: StateFlow<List<Pet>> = getPetsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
