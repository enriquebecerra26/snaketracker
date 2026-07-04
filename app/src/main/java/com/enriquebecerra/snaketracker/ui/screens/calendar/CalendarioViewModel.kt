package com.enriquebecerra.snaketracker.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enriquebecerra.snaketracker.domain.model.CalendarEvent
import com.enriquebecerra.snaketracker.domain.usecase.GetCalendarEventsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CalendarioViewModel(getCalendarEventsUseCase: GetCalendarEventsUseCase) : ViewModel() {

    val events: StateFlow<List<CalendarEvent>> = getCalendarEventsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
