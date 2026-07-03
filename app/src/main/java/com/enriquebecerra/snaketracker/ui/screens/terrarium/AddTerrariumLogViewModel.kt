package com.enriquebecerra.snaketracker.ui.screens.terrarium

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.TerrariumLog
import com.enriquebecerra.snaketracker.domain.usecase.SaveTerrariumLogUseCase
import kotlinx.coroutines.launch

class AddTerrariumLogViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveTerrariumLogUseCase: SaveTerrariumLogUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun saveTerrariumLog(
        date: Long,
        hotSpotTemp: Float?,
        coldSideTemp: Float?,
        humidityPercent: Int?,
        substrateType: String?,
        substrateChangedToday: Boolean,
        heatSource: String?,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            saveTerrariumLogUseCase(
                TerrariumLog(
                    petId = petId,
                    date = date,
                    hotSpotTemp = hotSpotTemp,
                    coldSideTemp = coldSideTemp,
                    humidityPercent = humidityPercent,
                    substrateType = substrateType,
                    substrateChangedDate = if (substrateChangedToday) date else null,
                    heatSource = heatSource,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
