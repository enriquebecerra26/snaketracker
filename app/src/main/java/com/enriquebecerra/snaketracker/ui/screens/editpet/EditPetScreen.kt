package com.enriquebecerra.snaketracker.ui.screens.editpet

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.usecase.GetPetByIdUseCase
import com.enriquebecerra.snaketracker.domain.usecase.SavePetUseCase
import com.enriquebecerra.snaketracker.ui.common.DateField
import com.enriquebecerra.snaketracker.ui.common.PetPhotoPicker
import com.enriquebecerra.snaketracker.ui.common.SexSelector
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPetScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: EditPetViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        EditPetViewModel(
            savedStateHandle = handle,
            getPetByIdUseCase = GetPetByIdUseCase(app.petRepository),
            savePetUseCase = SavePetUseCase(app.petRepository)
        )
    }
) {
    val pet by viewModel.pet.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar mascota") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val currentPet = pet
        if (currentPet == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            EditPetForm(
                pet = currentPet,
                padding = padding,
                onSave = { updated -> viewModel.savePet(updated, onSaved) },
                onCancel = onBackClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPetForm(
    pet: Pet,
    padding: PaddingValues,
    onSave: (Pet) -> Unit,
    onCancel: () -> Unit
) {
    var initialized by rememberSaveable(pet.id) { mutableStateOf(false) }

    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf("Desconocido") }
    var morph by remember { mutableStateOf("") }
    var birthDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var acquisitionDateMillis by remember { mutableStateOf<Long?>(null) }
    var weight by remember { mutableStateOf("") }
    var breeder by remember { mutableStateOf("") }
    var chipNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var attemptedSave by remember { mutableStateOf(false) }

    LaunchedEffect(pet.id) {
        if (!initialized) {
            photoUri = pet.photoUri?.let(Uri::parse)
            name = pet.name
            species = pet.species
            sex = pet.sex
            morph = pet.morph
            birthDateMillis = pet.birthDate
            acquisitionDateMillis = pet.acquisitionDate
            weight = if (pet.weight == pet.weight.toLong().toDouble()) {
                pet.weight.toLong().toString()
            } else {
                pet.weight.toString()
            }
            breeder = pet.breeder ?: ""
            chipNumber = pet.chipNumber ?: ""
            notes = pet.notes ?: ""
            initialized = true
        }
    }

    val nameError = attemptedSave && name.isBlank()
    val speciesError = attemptedSave && species.isBlank()

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        PetPhotoPicker(
            photoUri = photoUri,
            onPhotoUriChange = { photoUri = it },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            isError = nameError,
            supportingText = { if (nameError) Text("El nombre es obligatorio") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
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
            value = morph,
            onValueChange = { morph = it },
            label = { Text("Morfo (opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Text(
            text = "Sexo",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
        )
        SexSelector(selectedSex = sex, onSexChange = { sex = it })
        DateField(
            label = "Fecha de nacimiento",
            dateMillis = birthDateMillis,
            onDateChange = { it?.let { millis -> birthDateMillis = millis } },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        )
        DateField(
            label = "Fecha de adquisición (opcional)",
            dateMillis = acquisitionDateMillis,
            onDateChange = { acquisitionDateMillis = it },
            allowClear = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = weight,
            onValueChange = { input ->
                if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                    weight = input
                }
            },
            label = { Text("Peso (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = breeder,
            onValueChange = { breeder = it },
            label = { Text("Criador o tienda (opcional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = chipNumber,
            onValueChange = { chipNumber = it },
            label = { Text("Número de chip (opcional)") },
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
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    attemptedSave = true
                    if (name.isNotBlank() && species.isNotBlank()) {
                        onSave(
                            pet.copy(
                                name = name,
                                species = species,
                                sex = sex,
                                morph = morph,
                                birthDate = birthDateMillis,
                                acquisitionDate = acquisitionDateMillis,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                photoUri = photoUri?.toString(),
                                breeder = breeder.ifBlank { null },
                                chipNumber = chipNumber.ifBlank { null },
                                notes = notes.ifBlank { null }
                            )
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
