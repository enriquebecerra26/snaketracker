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
        birthDate: Long,
        weight: Double,
        photoUri: String?,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            savePetUseCase(
                Pet(
                    name = name,
                    species = species,
                    birthDate = birthDate,
                    weight = weight,
                    photoUri = photoUri,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
