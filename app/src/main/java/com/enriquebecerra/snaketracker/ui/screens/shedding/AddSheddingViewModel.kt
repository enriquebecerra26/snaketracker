package com.enriquebecerra.snaketracker.ui.screens.shedding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.SheddingLog
import com.enriquebecerra.snaketracker.domain.usecase.SaveSheddingLogUseCase
import kotlinx.coroutines.launch

class AddSheddingViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveSheddingLogUseCase: SaveSheddingLogUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun saveSheddingLog(
        bluePhaseStart: Long?,
        sheddingStart: Long?,
        completedDate: Long,
        wasComplete: Boolean,
        humidityPercent: Int?,
        problems: String?,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            saveSheddingLogUseCase(
                SheddingLog(
                    petId = petId,
                    bluePhaseStart = bluePhaseStart,
                    sheddingStart = sheddingStart,
                    completedDate = completedDate,
                    wasComplete = wasComplete,
                    problems = problems,
                    humidityPercent = humidityPercent,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
