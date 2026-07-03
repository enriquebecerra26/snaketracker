package com.enriquebecerra.snaketracker.ui.screens.weight

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.WeightLog
import com.enriquebecerra.snaketracker.domain.usecase.SaveWeightLogUseCase
import kotlinx.coroutines.launch

class AddWeightViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveWeightLogUseCase: SaveWeightLogUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun saveWeightLog(weight: Double, onSaved: () -> Unit) {
        viewModelScope.launch {
            saveWeightLogUseCase(
                WeightLog(
                    petId = petId,
                    date = System.currentTimeMillis(),
                    weight = weight
                )
            )
            onSaved()
        }
    }
}
