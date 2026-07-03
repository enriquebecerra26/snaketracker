package com.enriquebecerra.snaketracker.ui.screens.health

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.HealthRecord
import com.enriquebecerra.snaketracker.domain.usecase.SaveHealthRecordUseCase
import kotlinx.coroutines.launch

class AddHealthRecordViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveHealthRecordUseCase: SaveHealthRecordUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun saveHealthRecord(
        date: Long,
        type: String,
        title: String,
        description: String?,
        vetName: String?,
        medication: String?,
        dosage: String?,
        nextVisitDate: Long?,
        resolved: Boolean,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            saveHealthRecordUseCase(
                HealthRecord(
                    petId = petId,
                    date = date,
                    type = type,
                    title = title,
                    description = description,
                    vetName = vetName,
                    medication = medication,
                    dosage = dosage,
                    nextVisitDate = nextVisitDate,
                    resolved = resolved
                )
            )
            onSaved()
        }
    }
}
