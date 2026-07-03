package com.enriquebecerra.snaketracker.ui.screens.expense

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import com.enriquebecerra.snaketracker.domain.model.ExpenseCategoryOptions
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SaveExpenseUseCase
import com.enriquebecerra.snaketracker.ui.common.DateField
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddExpenseViewModel = snakeTrackerViewModel { app: SnakeTrackerApplication ->
        AddExpenseViewModel(GetPetsUseCase(app.petRepository), SaveExpenseUseCase(app.expenseRepository))
    }
) {
    val pets by viewModel.pets.collectAsStateWithLifecycle()

    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var category by remember { mutableStateOf(ExpenseCategoryOptions.first()) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedPetId by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var attemptedSave by remember { mutableStateOf(false) }
    var petMenuExpanded by remember { mutableStateOf(false) }

    val descriptionError = attemptedSave && description.isBlank()
    val amountError = attemptedSave && amount.toFloatOrNull() == null
    val selectedPetName = pets.find { it.id == selectedPetId }?.name ?: "General"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar gasto") },
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
            DateField(
                label = "Fecha",
                dateMillis = dateMillis,
                onDateChange = { it?.let { millis -> dateMillis = millis } },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Categoría",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseCategoryOptions.forEach { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = { category = option },
                        label = { Text(option) }
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                isError = descriptionError,
                supportingText = { if (descriptionError) Text("La descripción es obligatoria") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        amount = input
                    }
                },
                label = { Text("Monto (MXN)") },
                isError = amountError,
                supportingText = { if (amountError) Text("Ingresa un monto válido") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            ExposedDropdownMenuBox(
                expanded = petMenuExpanded,
                onExpandedChange = { petMenuExpanded = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedPetName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mascota asociada") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = petMenuExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = petMenuExpanded,
                    onDismissRequest = { petMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("General") },
                        onClick = {
                            selectedPetId = null
                            petMenuExpanded = false
                        }
                    )
                    pets.forEach { pet ->
                        DropdownMenuItem(
                            text = { Text(pet.name) },
                            onClick = {
                                selectedPetId = pet.id
                                petMenuExpanded = false
                            }
                        )
                    }
                }
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
                        val amountValue = amount.toFloatOrNull()
                        if (description.isNotBlank() && amountValue != null) {
                            viewModel.saveExpense(
                                date = dateMillis,
                                category = category,
                                description = description,
                                amountMXN = amountValue,
                                petId = selectedPetId,
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
