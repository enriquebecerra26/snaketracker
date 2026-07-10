package com.enriquebecerra.snaketracker.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Campo de fecha reutilizable con [DatePicker] de Material 3.
 * Si [allowClear] es true y hay un valor seleccionado, muestra un ícono para dejarlo vacío
 * (usado en campos opcionales como la fecha de adquisición).
 *
 * [DatePicker] de Material 3 opera internamente en UTC (su `selectedDateMillis`
 * representa la medianoche UTC del día elegido), mientras que el resto de la app
 * trabaja con millis de medianoche en la zona horaria local. Las funciones de
 * abajo convierten entre ambas representaciones para evitar que el día
 * seleccionado se desplace en zonas horarias distintas de UTC.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    dateMillis: Long?,
    onDateChange: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    allowClear: Boolean = false
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = dateMillis?.let { formatDate(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            Row {
                if (allowClear && dateMillis != null) {
                    IconButton(onClick = { onDateChange(null) }) {
                        Icon(Icons.Default.Close, contentDescription = "Quitar fecha")
                    }
                }
                IconButton(onClick = { showPicker = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Elegir fecha")
                }
            }
        },
        modifier = modifier
    )

    if (showPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = toUtcMidnightMillis(dateMillis ?: System.currentTimeMillis())
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(toLocalMidnightMillis(it)) }
                    showPicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/** Convierte millis de medianoche local a millis de medianoche UTC del mismo día calendario. */
private fun toUtcMidnightMillis(localMillis: Long): Long {
    val localDate = Instant.ofEpochMilli(localMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

/** Convierte millis de medianoche UTC (los que entrega [DatePicker]) a millis de medianoche local. */
private fun toLocalMidnightMillis(utcMillis: Long): Long {
    val localDate = Instant.ofEpochMilli(utcMillis).atZone(ZoneOffset.UTC).toLocalDate()
    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
