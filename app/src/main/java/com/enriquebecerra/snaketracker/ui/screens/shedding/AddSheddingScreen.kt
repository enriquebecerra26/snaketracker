package com.enriquebecerra.snaketracker.ui.screens.shedding

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
import com.enriquebecerra.snaketracker.domain.usecase.SaveSheddingLogUseCase
import com.enriquebecerra.snaketracker.ui.common.DateField
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSheddingScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddSheddingViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        AddSheddingViewModel(handle, SaveSheddingLogUseCase(app.sheddingRepository))
    }
) {
    var bluePhaseStart by remember { mutableStateOf<Long?>(null) }
    var sheddingStart by remember { mutableStateOf<Long?>(null) }
    var completedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var wasComplete by remember { mutableStateOf(true) }
    var humidityPercent by remember { mutableStateOf("") }
    var problems by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar muda") },
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
                label = "Inicio de ojos azules (opcional)",
                dateMillis = bluePhaseStart,
                onDateChange = { bluePhaseStart = it },
                allowClear = true,
                modifier = Modifier.fillMaxWidth()
            )
            DateField(
                label = "Inicio de muda (opcional)",
                dateMillis = sheddingStart,
                onDateChange = { sheddingStart = it },
                allowClear = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            DateField(
                label = "Fecha completada",
                dateMillis = completedDate,
                onDateChange = { it?.let { millis -> completedDate = millis } },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (wasComplete) "Muda completa" else "Muda incompleta",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Switch(checked = wasComplete, onCheckedChange = { wasComplete = it })
            }

            OutlinedTextField(
                value = humidityPercent,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*$"))) {
                        humidityPercent = input
                    }
                },
                label = { Text("Humedad durante la muda (%, opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            OutlinedTextField(
                value = problems,
                onValueChange = { problems = it },
                label = { Text("Problemas encontrados (opcional)") },
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
                        viewModel.saveSheddingLog(
                            bluePhaseStart = bluePhaseStart,
                            sheddingStart = sheddingStart,
                            completedDate = completedDate,
                            wasComplete = wasComplete,
                            humidityPercent = humidityPercent.toIntOrNull(),
                            problems = problems.ifBlank { null },
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
