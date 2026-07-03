package com.enriquebecerra.snaketracker.ui.screens.feeding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.FeedingLog
import com.enriquebecerra.snaketracker.domain.usecase.SaveFeedingLogUseCase
import kotlinx.coroutines.launch

class AddFeedingViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveFeedingLogUseCase: SaveFeedingLogUseCase
) : ViewModel() {

    val petId: Long = checkNotNull(savedStateHandle["petId"])

    fun saveFeedingLog(
        preyType: String,
        preyWeight: Double,
        accepted: Boolean,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            saveFeedingLogUseCase(
                FeedingLog(
                    petId = petId,
                    date = System.currentTimeMillis(),
                    preyType = preyType,
                    preyWeight = preyWeight,
                    accepted = accepted,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
