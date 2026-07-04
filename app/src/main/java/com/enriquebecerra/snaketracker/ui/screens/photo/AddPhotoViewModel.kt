package com.enriquebecerra.snaketracker.ui.screens.photo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.PhotoEntry
import com.enriquebecerra.snaketracker.domain.usecase.SavePhotoUseCase
import kotlinx.coroutines.launch

class AddPhotoViewModel(
    savedStateHandle: SavedStateHandle,
    private val savePhotoUseCase: SavePhotoUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun savePhoto(date: Long, photoUri: String, caption: String?, eventType: String?, onSaved: () -> Unit) {
        viewModelScope.launch {
            savePhotoUseCase(
                PhotoEntry(
                    petId = petId,
                    date = date,
                    photoUri = photoUri,
                    caption = caption,
                    eventType = eventType
                )
            )
            onSaved()
        }
    }
}
