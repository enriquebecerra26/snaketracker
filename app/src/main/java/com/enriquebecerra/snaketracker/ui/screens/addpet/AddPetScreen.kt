package com.enriquebecerra.snaketracker.ui.screens.addpet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    onPetSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddPetViewModel = snakeTrackerViewModel { app: SnakeTrackerApplication ->
        AddPetViewModel(SavePetUseCase(app.petRepository))
    }
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var birthDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var weight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var attemptedSave by remember { mutableStateOf(false) }

    val nameError = attemptedSave && name.isBlank()
    val speciesError = attemptedSave && species.isBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva mascota") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                isError = nameError,
                supportingText = { if (nameError) Text("El nombre es obligatorio") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Especie") },
                isError = speciesError,
                supportingText = { if (speciesError) Text("La especie es obligatoria") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = formatDate(birthDateMillis),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de nacimiento") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Elegir fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        weight = input
                    }
                },
                label = { Text("Peso inicial (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas") },
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
                        if (name.isNotBlank() && species.isNotBlank()) {
                            viewModel.savePet(
                                name = name,
                                species = species,
                                birthDate = birthDateMillis,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                photoUri = null,
                                notes = notes.ifBlank { null },
                                onSaved = onPetSaved
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { birthDateMillis = it }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES")).format(Date(millis))
