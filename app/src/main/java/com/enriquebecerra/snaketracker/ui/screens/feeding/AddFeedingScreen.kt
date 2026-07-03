package com.enriquebecerra.snaketracker.ui.screens.feeding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.usecase.SaveFeedingLogUseCase
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedingScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddFeedingViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        AddFeedingViewModel(handle, SaveFeedingLogUseCase(app.feedingRepository))
    }
) {
    var preyType by remember { mutableStateOf("") }
    var preyWeight by remember { mutableStateOf("") }
    var accepted by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar alimentación") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = preyType,
                onValueChange = { preyType = it },
                label = { Text("Tipo de presa") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = preyWeight,
                onValueChange = { preyWeight = it },
                label = { Text("Peso de la presa (g)") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Checkbox(checked = accepted, onCheckedChange = { accepted = it })
                Text("Aceptó la presa")
            }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Button(
                onClick = {
                    viewModel.saveFeedingLog(
                        preyType = preyType,
                        preyWeight = preyWeight.toDoubleOrNull() ?: 0.0,
                        accepted = accepted,
                        notes = notes.ifBlank { null },
                        onSaved = onSaved
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = preyType.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}
