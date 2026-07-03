package com.enriquebecerra.snaketracker.ui.screens.petlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PetListViewModel(
    getPetsUseCase: GetPetsUseCase,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModel() {

    val pets: StateFlow<List<Pet>> = getPetsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deletePet(pet: Pet) {
        viewModelScope.launch {
            deletePetUseCase(pet)
        }
    }
}
