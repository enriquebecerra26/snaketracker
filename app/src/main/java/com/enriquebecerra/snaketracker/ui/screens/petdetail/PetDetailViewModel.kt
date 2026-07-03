package com.enriquebecerra.snaketracker.ui.screens.petdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.model.LengthLog
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetFeedingLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetLengthLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetByIdUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetWeightLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    getPetByIdUseCase: GetPetByIdUseCase,
    getFeedingLogsUseCase: GetFeedingLogsUseCase,
    getWeightLogsUseCase: GetWeightLogsUseCase,
    getLengthLogsUseCase: GetLengthLogsUseCase,
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

    // Ordenado por fecha descendente desde el DAO (más reciente primero)
    val lengthLogs: StateFlow<List<LengthLog>> = getLengthLogsUseCase(petId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentWeight: StateFlow<Double> = combine(pet, weightLogs) { currentPet, logs ->
        logs.firstOrNull()?.weight ?: currentPet?.weight ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentLength: StateFlow<Float?> = lengthLogs
        .map { logs -> logs.firstOrNull()?.lengthCm }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weightVariation: StateFlow<String> = weightLogs
        .map(::computeWeightVariation)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

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

    private fun computeWeightVariation(logs: List<WeightLog>): String {
        if (logs.size < 2) return ""
        val latest = logs.maxByOrNull { it.date } ?: return ""
        val thirtyDaysAgo = latest.date - TimeUnit.DAYS.toMillis(30)
        val reference = logs
            .filter { it.id != latest.id && it.date <= thirtyDaysAgo }
            .maxByOrNull { it.date }
            ?: logs.filter { it.id != latest.id }.minByOrNull { it.date }
            ?: return ""

        val diff = latest.weight - reference.weight
        val diffText = if (diff == diff.toLong().toDouble()) {
            kotlin.math.abs(diff).toLong().toString()
        } else {
            String.format(Locale("es", "ES"), "%.1f", kotlin.math.abs(diff))
        }
        val sign = when {
            diff > 0 -> "+"
            diff < 0 -> "-"
            else -> ""
        }
        return "$sign$diffText g desde el mes pasado"
    }
}
