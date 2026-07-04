package com.enriquebecerra.snaketracker.ui.screens.terrarium

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
import com.enriquebecerra.snaketracker.domain.usecase.SaveTerrariumLogUseCase
import com.enriquebecerra.snaketracker.ui.common.DateField
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTerrariumLogScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddTerrariumLogViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        AddTerrariumLogViewModel(handle, SaveTerrariumLogUseCase(app.terrariumRepository))
    }
) {
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var hotSpotTemp by remember { mutableStateOf("") }
    var coldSideTemp by remember { mutableStateOf("") }
    var humidityPercent by remember { mutableStateOf("") }
    var substrateType by remember { mutableStateOf("") }
    var substrateChangedToday by remember { mutableStateOf(false) }
    var heatSource by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar terrario") },
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
            DateField(
                label = "Fecha",
                dateMillis = dateMillis,
                onDateChange = { it?.let { millis -> dateMillis = millis } },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = hotSpotTemp,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        hotSpotTemp = input
                    }
                },
                label = { Text("Temperatura punto caliente (°C)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = coldSideTemp,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        coldSideTemp = input
                    }
                },
                label = { Text("Temperatura lado frío (°C)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = humidityPercent,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*$"))) {
                        humidityPercent = input
                    }
                },
                label = { Text("Humedad (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = substrateType,
                onValueChange = { substrateType = it },
                label = { Text("Tipo de sustrato (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "¿Se cambió el sustrato hoy?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(checked = substrateChangedToday, onCheckedChange = { substrateChangedToday = it })
            }

            OutlinedTextField(
                value = heatSource,
                onValueChange = { heatSource = it },
                label = { Text("Fuente de calor (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
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
                        viewModel.saveTerrariumLog(
                            date = dateMillis,
                            hotSpotTemp = hotSpotTemp.toFloatOrNull(),
                            coldSideTemp = coldSideTemp.toFloatOrNull(),
                            humidityPercent = humidityPercent.toIntOrNull(),
                            substrateType = substrateType.ifBlank { null },
                            substrateChangedToday = substrateChangedToday,
                            heatSource = heatSource.ifBlank { null },
                            notes = notes.ifBlank { null },
                            onSaved = onSaved
                        )
                    },
                    modifier = Modifier.weight(1f).padding(start = 12.dp)
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
