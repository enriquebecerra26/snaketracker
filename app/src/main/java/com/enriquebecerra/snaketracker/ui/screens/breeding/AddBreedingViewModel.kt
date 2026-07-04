package com.enriquebecerra.snaketracker.ui.screens.breeding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.BreedingRecord
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SaveBreedingRecordUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddBreedingViewModel(
    savedStateHandle: SavedStateHandle,
    getPetsUseCase: GetPetsUseCase,
    private val saveBreedingRecordUseCase: SaveBreedingRecordUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    val availableMales: StateFlow<List<Pet>> = getPetsUseCase()
        .map { pets -> pets.filter { it.id != petId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveBreedingRecord(
        maleId: Long?,
        pairingDate: Long?,
        ovulationDate: Long?,
        layingDate: Long?,
        totalEggs: Int?,
        fertileEggs: Int?,
        incubationStartDate: Long?,
        hatchDate: Long?,
        hatchlings: Int?,
        incubationTempC: Float?,
        incubationHumidity: Int?,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            saveBreedingRecordUseCase(
                BreedingRecord(
                    petId = petId,
                    maleId = maleId,
                    pairingDate = pairingDate,
                    ovulationDate = ovulationDate,
                    layingDate = layingDate,
                    totalEggs = totalEggs,
                    fertileEggs = fertileEggs,
                    incubationStartDate = incubationStartDate,
                    hatchDate = hatchDate,
                    hatchlings = hatchlings,
                    incubationTempC = incubationTempC,
                    incubationHumidity = incubationHumidity,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
