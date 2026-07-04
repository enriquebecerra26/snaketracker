package com.enriquebecerra.snaketracker.ui.screens.breeding

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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SaveBreedingRecordUseCase
import com.enriquebecerra.snaketracker.ui.common.DateField
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBreedingScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddBreedingViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        AddBreedingViewModel(handle, GetPetsUseCase(app.petRepository), SaveBreedingRecordUseCase(app.breedingRepository))
    }
) {
    val availableMales by viewModel.availableMales.collectAsStateWithLifecycle()

    var maleMenuExpanded by remember { mutableStateOf(false) }
    var selectedMaleId by remember { mutableStateOf<Long?>(null) }
    var pairingDate by remember { mutableStateOf<Long?>(null) }
    var ovulationDate by remember { mutableStateOf<Long?>(null) }
    var layingDate by remember { mutableStateOf<Long?>(null) }
    var totalEggs by remember { mutableStateOf("") }
    var fertileEggs by remember { mutableStateOf("") }
    var incubationStartDate by remember { mutableStateOf<Long?>(null) }
    var hatchDate by remember { mutableStateOf<Long?>(null) }
    var incubationTemp by remember { mutableStateOf("") }
    var incubationHumidity by remember { mutableStateOf("") }
    var hatchlings by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val selectedMaleName = availableMales.find { it.id == selectedMaleId }?.name ?: "Externo"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar reproducción") },
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
            ExposedDropdownMenuBox(
                expanded = maleMenuExpanded,
                onExpandedChange = { maleMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedMaleName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Macho") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = maleMenuExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = maleMenuExpanded,
                    onDismissRequest = { maleMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Externo") },
                        onClick = {
                            selectedMaleId = null
                            maleMenuExpanded = false
                        }
                    )
                    availableMales.forEach { pet ->
                        DropdownMenuItem(
                            text = { Text(pet.name) },
                            onClick = {
                                selectedMaleId = pet.id
                                maleMenuExpanded = false
                            }
                        )
                    }
                }
            }

            DateField(
                label = "Fecha de emparejamiento (opcional)",
                dateMillis = pairingDate,
                onDateChange = { pairingDate = it },
                allowClear = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            DateField(
                label = "Fecha de ovulación (opcional)",
                dateMillis = ovulationDate,
                onDateChange = { ovulationDate = it },
                allowClear = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            DateField(
                label = "Fecha de puesta (opcional)",
                dateMillis = layingDate,
                onDateChange = { layingDate = it },
                allowClear = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                OutlinedTextField(
                    value = totalEggs,
                    onValueChange = { input -> if (input.isEmpty() || input.matches(Regex("^\\d*$"))) totalEggs = input },
                    label = { Text("Total de huevos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = fertileEggs,
                    onValueChange = { input -> if (input.isEmpty() || input.matches(Regex("^\\d*$"))) fertileEggs = input },
                    label = { Text("Huevos fértiles") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }

            DateField(
                label = "Fecha inicio de incubación (opcional)",
                dateMillis = incubationStartDate,
                onDateChange = { incubationStartDate = it },
                allowClear = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            DateField(
                label = "Fecha de eclosión (opcional)",
                dateMillis = hatchDate,
                onDateChange = { hatchDate = it },
                allowClear = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                OutlinedTextField(
                    value = incubationTemp,
                    onValueChange = { input -> if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) incubationTemp = input },
                    label = { Text("Temp. incubación (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = incubationHumidity,
                    onValueChange = { input -> if (input.isEmpty() || input.matches(Regex("^\\d*$"))) incubationHumidity = input },
                    label = { Text("Humedad incubación (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                )
            }

            OutlinedTextField(
                value = hatchlings,
                onValueChange = { input -> if (input.isEmpty() || input.matches(Regex("^\\d*$"))) hatchlings = input },
                label = { Text("Número de crías") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
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
                        viewModel.saveBreedingRecord(
                            maleId = selectedMaleId,
                            pairingDate = pairingDate,
                            ovulationDate = ovulationDate,
                            layingDate = layingDate,
                            totalEggs = totalEggs.toIntOrNull(),
                            fertileEggs = fertileEggs.toIntOrNull(),
                            incubationStartDate = incubationStartDate,
                            hatchDate = hatchDate,
                            hatchlings = hatchlings.toIntOrNull(),
                            incubationTempC = incubationTemp.toFloatOrNull(),
                            incubationHumidity = incubationHumidity.toIntOrNull(),
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
