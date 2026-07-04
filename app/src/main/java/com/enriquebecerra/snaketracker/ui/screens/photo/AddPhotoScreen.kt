package com.enriquebecerra.snaketracker.ui.screens.photo

import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.PhotoEventTypeOptions
import com.enriquebecerra.snaketracker.domain.usecase.SavePhotoUseCase
import com.enriquebecerra.snaketracker.ui.common.DateField
import com.enriquebecerra.snaketracker.ui.common.PetPhotoPicker
import com.enriquebecerra.snaketracker.ui.common.snakeTrackerViewModelWithSavedState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhotoScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: AddPhotoViewModel = snakeTrackerViewModelWithSavedState { app: SnakeTrackerApplication, handle ->
        AddPhotoViewModel(handle, SavePhotoUseCase(app.photoRepository))
    }
) {
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var caption by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf<String?>(null) }
    var attemptedSave by remember { mutableStateOf(false) }

    val photoError = attemptedSave && photoUri == null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar foto") },
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
            PetPhotoPicker(
                photoUri = photoUri,
                onPhotoUriChange = { photoUri = it },
                modifier = Modifier.fillMaxWidth()
            )
            if (photoError) {
                Text(
                    text = "Selecciona una foto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DateField(
                label = "Fecha",
                dateMillis = dateMillis,
                onDateChange = { it?.let { millis -> dateMillis = millis } },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            Text(
                text = "Tipo de evento (opcional)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PhotoEventTypeOptions.forEach { option ->
                    FilterChip(
                        selected = eventType == option,
                        onClick = { eventType = if (eventType == option) null else option },
                        label = { Text(option) }
                    )
                }
            }

            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
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
                        val uri = photoUri
                        if (uri != null) {
                            viewModel.savePhoto(
                                date = dateMillis,
                                photoUri = uri.toString(),
                                caption = caption.ifBlank { null },
                                eventType = eventType,
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
