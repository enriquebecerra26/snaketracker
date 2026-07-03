package com.enriquebecerra.snaketracker.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.Calendar

/**
 * Campo de hora (formato "HH:mm") con [TimePicker] de Material 3.
 * Material3 no ofrece un "TimePickerDialog" listo como con DatePicker, así que se envuelve
 * manualmente en un [Dialog] siguiendo el patrón recomendado por la documentación de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = time,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Default.AccessTime, contentDescription = "Elegir hora")
            }
        },
        modifier = modifier
    )

    if (showPicker) {
        val (initialHour, initialMinute) = remember(time) { parseTimeOrNow(time) }
        val pickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )
        Dialog(onDismissRequest = { showPicker = false }) {
            Surface(shape = MaterialTheme.shapes.extraLarge, tonalElevation = 6.dp) {
                Column(modifier = Modifier.padding(24.dp)) {
                    TimePicker(state = pickerState)
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        TextButton(onClick = { showPicker = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                        TextButton(
                            onClick = {
                                onTimeChange("%02d:%02d".format(pickerState.hour, pickerState.minute))
                                showPicker = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Aceptar")
                        }
                    }
                }
            }
        }
    }
}

private fun parseTimeOrNow(time: String): Pair<Int, Int> {
    val parts = time.split(":")
    if (parts.size == 2) {
        val hour = parts[0].toIntOrNull()
        val minute = parts[1].toIntOrNull()
        if (hour != null && minute != null) return hour to minute
    }
    val now = Calendar.getInstance()
    return now.get(Calendar.HOUR_OF_DAY) to now.get(Calendar.MINUTE)
}

fun currentTimeString(): String {
    val now = Calendar.getInstance()
    return "%02d:%02d".format(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
}
