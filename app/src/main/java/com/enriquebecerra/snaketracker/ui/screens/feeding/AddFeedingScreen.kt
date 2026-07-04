package com.enriquebecerra.snaketracker.ui.screens.feeding

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.PreyConditionOptions
import com.enriquebecerra.snaketracker.domain.model.PreySizeOptions
import com.enriquebecerra.snaketracker.domain.model.PreyTypeOptions
import com.enriquebecerra.snaketracker.domain.usecase.SaveFeedingLogUseCase
import com.enriquebecerra.snaketracker.ui.common.DateField
import com.enriquebecerra.snaketracker.ui.common.SegmentedSelector
import com.enriquebecerra.snaketracker.ui.common.TimeField
import com.enriquebecerra.snaketracker.ui.common.currentTimeString
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

private const val OtherPreyType = "Otro"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedingScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddFeedingViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        AddFeedingViewModel(handle, SaveFeedingLogUseCase(app.feedingRepository))
    }
) {
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var time by remember { mutableStateOf(currentTimeString()) }
    var selectedPreyType by remember { mutableStateOf<String?>(null) }
    var customPreyType by remember { mutableStateOf("") }
    var preyCondition by remember { mutableStateOf(PreyConditionOptions.first()) }
    var preySize by remember { mutableStateOf(PreySizeOptions[1]) }
    var preyWeight by remember { mutableStateOf("") }
    var accepted by remember { mutableStateOf(true) }
    var durationMinutes by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attemptedSave by remember { mutableStateOf(false) }

    val preyTypeError = attemptedSave &&
        (selectedPreyType == null || (selectedPreyType == OtherPreyType && customPreyType.isBlank()))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar alimentación") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DateField(
                    label = "Fecha",
                    dateMillis = dateMillis,
                    onDateChange = { it?.let { millis -> dateMillis = millis } },
                    modifier = Modifier.weight(1f)
                )
                TimeField(
                    label = "Hora",
                    time = time,
                    onTimeChange = { time = it },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }

            Text(
                text = "Tipo de presa",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PreyTypeOptions.forEach { option ->
                    FilterChip(
                        selected = selectedPreyType == option,
                        onClick = { selectedPreyType = option },
                        label = { Text(option) }
                    )
                }
            }
            if (preyTypeError) {
                Text(
                    text = "Selecciona el tipo de presa",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (selectedPreyType == OtherPreyType) {
                OutlinedTextField(
                    value = customPreyType,
                    onValueChange = { customPreyType = it },
                    label = { Text("Especifica el tipo de presa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            Text(
                text = "Condición",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            SegmentedSelector(
                options = PreyConditionOptions,
                selected = preyCondition,
                onSelect = { preyCondition = it }
            )

            Text(
                text = "Tamaño",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            SegmentedSelector(
                options = PreySizeOptions,
                selected = preySize,
                onSelect = { preySize = it }
            )

            OutlinedTextField(
                value = preyWeight,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        preyWeight = input
                    }
                },
                label = { Text("Peso de la presa (g, opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (accepted) "Comió la presa" else "No comió la presa",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(checked = accepted, onCheckedChange = { accepted = it })
            }

            if (accepted) {
                OutlinedTextField(
                    value = durationMinutes,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.matches(Regex("^\\d*$"))) {
                            durationMinutes = input
                        }
                    },
                    label = { Text("Tiempo que tardó (minutos, opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Button(
                    onClick = {
                        attemptedSave = true
                        val finalPreyType = if (selectedPreyType == OtherPreyType) {
                            customPreyType.trim()
                        } else {
                            selectedPreyType.orEmpty()
                        }
                        if (finalPreyType.isNotBlank()) {
                            viewModel.saveFeedingLog(
                                date = dateMillis,
                                time = time,
                                preyType = finalPreyType,
                                preyCondition = preyCondition,
                                preySize = preySize,
                                preyWeightGrams = preyWeight.toFloatOrNull(),
                                accepted = accepted,
                                durationMinutes = durationMinutes.toIntOrNull(),
                                notes = notes.ifBlank { null },
                                onSaved = onSaved
                            )
                        }
                    },
                    modifier = Modifier.weight(1f).padding(start = 12.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
