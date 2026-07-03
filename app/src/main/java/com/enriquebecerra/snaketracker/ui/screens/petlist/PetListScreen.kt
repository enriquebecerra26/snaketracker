package com.enriquebecerra.snaketracker.ui.screens.petlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetsUseCase
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModel
import com.enriquebecerra.snaketracker.ui.theme.SnakeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    onAddPetClick: () -> Unit,
    onPetClick: (Long) -> Unit,
    viewModel: PetListViewModel = snakeTrackerViewModel { app: SnakeTrackerApplication ->
        PetListViewModel(
            getPetsUseCase = GetPetsUseCase(app.petRepository),
            deletePetUseCase = DeletePetUseCase(app.petRepository)
        )
    }
) {
    val pets by viewModel.pets.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mis mascotas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPetClick) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        }
    ) { padding ->
        PetListContent(
            pets = pets,
            padding = padding,
            onPetClick = onPetClick
        )
    }
}

@Composable
private fun PetListContent(
    pets: List<Pet>,
    padding: PaddingValues,
    onPetClick: (Long) -> Unit
) {
    if (pets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = "Aún no tienes mascotas registradas.",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
        items(pets, key = { it.id }) { pet ->
            Card(modifier = Modifier.padding(8.dp).clickable { onPetClick(pet.id) }) {
                ListItem(
                    headlineContent = { Text(pet.name) },
                    supportingContent = { Text(pet.species) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetListPreview() {
    SnakeTrackerTheme {
        PetListContent(
            pets = listOf(
                Pet(id = 1, name = "Nagini", species = "Python regius", birthDate = 0L, weight = 850.0)
            ),
            padding = PaddingValues(0.dp),
            onPetClick = {}
        )
    }
}
