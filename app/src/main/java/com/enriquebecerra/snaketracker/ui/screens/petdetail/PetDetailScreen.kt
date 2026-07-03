package com.enriquebecerra.snaketracker.ui.screens.petdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetFeedingLogsUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetByIdUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetWeightLogsUseCase
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailScreen(
    onBackClick: () -> Unit,
    onPetDeleted: () -> Unit,
    onAddFeedingClick: (Long) -> Unit,
    onAddWeightClick: (Long) -> Unit,
    viewModel: PetDetailViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        PetDetailViewModel(
            savedStateHandle = handle,
            getPetByIdUseCase = GetPetByIdUseCase(app.petRepository),
            getFeedingLogsUseCase = GetFeedingLogsUseCase(app.feedingRepository),
            getWeightLogsUseCase = GetWeightLogsUseCase(app.weightRepository),
            deletePetUseCase = DeletePetUseCase(app.petRepository)
        )
    }
) {
    val pet by viewModel.pet.collectAsStateWithLifecycle()
    val feedingLogs by viewModel.feedingLogs.collectAsStateWithLifecycle()
    val weightLogs by viewModel.weightLogs.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pet?.name ?: "Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.deletePet(onPetDeleted) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar mascota")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            pet?.let {
                Text("Especie: ${it.species}")
                Text("Peso actual: ${it.weight} g")
                it.notes?.let { notes -> Text("Notas: $notes") }
            }

            Button(
                onClick = { viewModel.petId.let(onAddFeedingClick) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Registrar alimentación")
            }
            Button(
                onClick = { viewModel.petId.let(onAddWeightClick) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Registrar peso")
            }

            Text("Historial de alimentación", modifier = Modifier.padding(top = 16.dp))
            LazyColumn {
                items(feedingLogs, key = { it.id }) { log ->
                    Text("${log.preyType} - ${log.preyWeight}g - ${if (log.accepted) "Aceptó" else "Rechazó"}")
                }
            }

            Text("Historial de peso", modifier = Modifier.padding(top = 16.dp))
            LazyColumn {
                items(weightLogs, key = { it.id }) { log ->
                    Text("${log.weight} g")
                }
            }
        }
    }
}
