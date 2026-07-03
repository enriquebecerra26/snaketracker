package com.enriquebecerra.snaketracker.ui.screens.editpet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.GetPetByIdUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditPetViewModel(
    savedStateHandle: SavedStateHandle,
    getPetByIdUseCase: GetPetByIdUseCase,
    private val savePetUseCase: SavePetUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    val pet: StateFlow<Pet?> = getPetByIdUseCase(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun savePet(updatedPet: Pet, onSaved: () -> Unit) {
        viewModelScope.launch {
            savePetUseCase(updatedPet)
            onSaved()
        }
    }
}
