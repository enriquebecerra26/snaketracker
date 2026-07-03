package com.enriquebecerra.snaketracker.ui.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")).format(Date(millis))
