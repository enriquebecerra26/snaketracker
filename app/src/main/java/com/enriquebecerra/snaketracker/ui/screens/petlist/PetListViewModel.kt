package com.enriquebecerra.snaketracker.ui.screens.petlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.Alert
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.PetDashboardSummary
import com.enriquebecerra.snaketracker.domain.usecase.AlertEngine
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetDashboardSummariesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PetListViewModel(
    getDashboardSummariesUseCase: GetDashboardSummariesUseCase,
    alertEngine: AlertEngine,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModel() {

    val dashboardSummaries: StateFlow<List<PetDashboardSummary>> = getDashboardSummariesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<Alert>> = alertEngine()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            deletePetUseCase(pet)
        }
    }
}
