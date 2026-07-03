package com.enriquebecerra.snaketracker.ui.screens.addpet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import kotlinx.coroutines.launch

class AddPetViewModel(
    private val savePetUseCase: SavePetUseCase
) : ViewModel() {

    fun savePet(
        name: String,
        species: String,
        sex: String,
        morph: String,
        birthDate: Long,
        acquisitionDate: Long?,
        weight: Double,
        photoUri: String?,
        breeder: String?,
        chipNumber: String?,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            savePetUseCase(
                Pet(
                    name = name,
                    species = species,
                    sex = sex,
                    morph = morph,
                    birthDate = birthDate,
                    acquisitionDate = acquisitionDate,
                    weight = weight,
                    photoUri = photoUri,
                    breeder = breeder,
                    chipNumber = chipNumber,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
