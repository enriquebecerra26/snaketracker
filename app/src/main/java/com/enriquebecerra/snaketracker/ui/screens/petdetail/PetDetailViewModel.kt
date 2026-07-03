package com.enriquebecerra.snaketracker.ui.screens.petdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetFeedingLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetByIdUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetWeightLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    getPetByIdUseCase: GetPetByIdUseCase,
    getFeedingLogsUseCase: GetFeedingLogsUseCase,
    getWeightLogsUseCase: GetWeightLogsUseCase,
    private val savePetUseCase: SavePetUseCase,
    private val deletePetUseCase: DeletePetUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    val pet: StateFlow<Pet?> = getPetByIdUseCase(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val feedingLogs: StateFlow<List<FeedingLog>> = getFeedingLogsUseCase(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ordenado por fecha descendente desde el DAO (más reciente primero)
    val weightLogs: StateFlow<List<WeightLog>> = getWeightLogsUseCase(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentWeight: StateFlow<Double> = combine(pet, weightLogs) { currentPet, logs ->
        logs.firstOrNull()?.weight ?: currentPet?.weight ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun updateNotes(notes: String) {
        val currentPet = pet.value ?: return
        viewModelScope.launch {
            savePetUseCase(currentPet.copy(notes = notes.ifBlank { null }))
        }
    }

    fun deletePet(onDeleted: () -> Unit) {
        val currentPet = pet.value ?: return
        viewModelScope.launch {
            deletePetUseCase(currentPet)
            onDeleted()
        }
    }
}
