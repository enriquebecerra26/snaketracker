package com.enriquebecerra.snaketracker.domain.model

enum class CalendarEventType { FEEDING, BIOMETRICS, SHEDDING, DEFECATION, HEALTH, TERRARIUM, EXPENSE }

data class CalendarEvent(
    val date: Long,
    val type: CalendarEventType,
    val title: String,
    val petName: String?
)
