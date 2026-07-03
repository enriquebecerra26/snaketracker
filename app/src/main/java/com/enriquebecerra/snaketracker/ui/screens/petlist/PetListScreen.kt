package com.enriquebecerra.snaketracker.ui.screens.petlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.enriquebecerra.snaketracker.R
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.PetListItem
import com.enriquebecerra.snaketracker.domain.usecase.DeletePetUseCase
import com.enriquebecerra.snaketracker.domain.usecase.GetPetListItemsUseCase
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModel
import com.enriquebecerra.snaketracker.ui.theme.SnakeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    onAddPetClick: () -> Unit,
    onPetClick: (Long) -> Unit,
    viewModel: PetListViewModel = snakeTrackerViewModel { app: SnakeTrackerApplication ->
        PetListViewModel(
            getPetListItemsUseCase = GetPetListItemsUseCase(app.petRepository, app.feedingRepository),
            deletePetUseCase = DeletePetUseCase(app.petRepository)
        )
    }
) {
    val petListItems by viewModel.petListItems.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis mascotas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPetClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar mascota")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PetListContent(
            petListItems = petListItems,
            padding = padding,
            onPetClick = onPetClick
        )
    }
}

@Composable
private fun PetListContent(
    petListItems: List<PetListItem>,
    padding: PaddingValues,
    onPetClick: (Long) -> Unit
) {
    if (petListItems.isEmpty()) {
        EmptyPetListState(modifier = Modifier.fillMaxSize().padding(padding))
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(petListItems, key = { it.pet.id }) { item ->
            PetCard(item = item, onClick = { onPetClick(item.pet.id) })
        }
    }
}

@Composable
private fun EmptyPetListState(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_snake),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }
            Text(
                text = "Agrega tu primera mascota",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Registra a tu reptil para llevar el control de su alimentación y peso.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun PetCard(item: PetListItem, onClick: () -> Unit) {
    val pet = item.pet
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PetPhoto(photoUri = pet.photoUri)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = pet.species,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = lastFeedingLabel(item.daysSinceLastFeeding),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PetPhoto(photoUri: String?) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_snake),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

private fun lastFeedingLabel(daysSinceLastFeeding: Int?): String = when {
    daysSinceLastFeeding == null -> "Sin registro de alimentación"
    daysSinceLastFeeding <= 0 -> "Alimentado hoy"
    daysSinceLastFeeding == 1 -> "Hace 1 día"
    else -> "Hace $daysSinceLastFeeding días"
}

@Preview(showBackground = true)
@Composable
private fun PetListPreview() {
    SnakeTrackerTheme {
        PetListContent(
            petListItems = listOf(
                PetListItem(
                    pet = Pet(id = 1, name = "Nagini", species = "Python regius", birthDate = 0L, weight = 850.0),
                    daysSinceLastFeeding = 6
                )
            ),
            padding = PaddingValues(0.dp),
            onPetClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PetListEmptyPreview() {
    SnakeTrackerTheme {
        PetListContent(
            petListItems = emptyList(),
            padding = PaddingValues(0.dp),
            onPetClick = {}
        )
    }
}
