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
        date: Long,
        time: String,
        preyType: String,
        preyCondition: String,
        preySize: String,
        preyWeightGrams: Float?,
        accepted: Boolean,
        durationMinutes: Int?,
        notes: String?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            saveFeedingLogUseCase(
                FeedingLog(
                    petId = petId,
                    date = date,
                    time = time,
                    preyType = preyType,
                    preyCondition = preyCondition,
                    preySize = preySize,
                    preyWeightGrams = preyWeightGrams,
                    accepted = accepted,
                    durationMinutes = if (accepted) durationMinutes else null,
                    notes = notes
                )
            )
            onSaved()
        }
    }
}
