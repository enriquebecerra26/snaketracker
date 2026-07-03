package com.enriquebecerra.snaketracker.ui.screens.defecation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.DefecationLog
import com.enriquebecerra.snaketracker.domain.usecase.SaveDefecationLogUseCase
import kotlinx.coroutines.launch

class AddDefecationViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveDefecationLogUseCase: SaveDefecationLogUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun saveDefecationLog(date: Long, type: String, notes: String?, onSaved: () -> Unit) {
        viewModelScope.launch {
            saveDefecationLogUseCase(
                DefecationLog(
                    petId = petId,
                    date = date,
                    type = type,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
