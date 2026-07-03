package com.enriquebecerra.snaketracker.ui.screens.length

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.LengthLog
import com.enriquebecerra.snaketracker.domain.usecase.SaveLengthLogUseCase
import kotlinx.coroutines.launch

class AddLengthViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveLengthLogUseCase: SaveLengthLogUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun saveLengthLog(date: Long, lengthCm: Float, notes: String?, onSaved: () -> Unit) {
        viewModelScope.launch {
            saveLengthLogUseCase(
                LengthLog(
                    petId = petId,
                    date = date,
                    lengthCm = lengthCm,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
