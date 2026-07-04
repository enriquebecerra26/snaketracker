package com.enriquebecerra.snaketracker.ui.screens.petdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.enriquebecerra.snaketracker.domain.model.PhotoEntry
import com.enriquebecerra.snaketracker.domain.model.PhotoEventTypeOptions
import com.enriquebecerra.snaketracker.ui.common.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaTab(photos: List<PhotoEntry>, modifier: Modifier = Modifier) {
    var selectedFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var isTimelineView by rememberSaveable { mutableStateOf(false) }
    var selectedPhoto by remember { mutableStateOf<PhotoEntry?>(null) }

    val filteredPhotos = remember(photos, selectedFilter) {
        if (selectedFilter == null) photos else photos.filter { it.eventType == selectedFilter }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Todas") }
                )
                PhotoEventTypeOptions.forEach { option ->
                    FilterChip(
                        selected = selectedFilter == option,
                        onClick = { selectedFilter = option },
                        label = { Text(option) }
                    )
                }
            }
            IconButton(onClick = { isTimelineView = !isTimelineView }) {
                Icon(
                    imageVector = if (isTimelineView) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                    contentDescription = if (isTimelineView) "Ver como cuadrícula" else "Ver como línea del tiempo"
                )
            }
        }

        if (filteredPhotos.isEmpty()) {
            EmptyTabState(
                text = if (selectedFilter == null) "Sin fotos" else "Sin fotos de tipo $selectedFilter",
                modifier = Modifier.weight(1f)
            )
        } else if (isTimelineView) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPhotos, key = { it.id }) { photo ->
                    PhotoTimelineRow(photo = photo, onClick = { selectedPhoto = photo })
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(4.dp, 0.dp, 4.dp, 88.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                gridItems(filteredPhotos, key = { it.id }) { photo ->
                    PhotoThumbnail(photo = photo, onClick = { selectedPhoto = photo })
                }
            }
        }
    }

    selectedPhoto?.let { photo ->
        PhotoFullScreenDialog(photo = photo, onDismiss = { selectedPhoto = null })
    }
}

@Composable
private fun PhotoThumbnail(photo: PhotoEntry, onClick: () -> Unit) {
    AsyncImage(
        model = photo.photoUri,
        contentDescription = photo.caption,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun PhotoTimelineRow(photo: PhotoEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photo.photoUri,
                contentDescription = photo.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    text = formatDate(photo.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!photo.eventType.isNullOrBlank()) {
                    Text(
                        text = photo.eventType,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (!photo.caption.isNullOrBlank()) {
                    Text(
                        text = photo.caption,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoFullScreenDialog(photo: PhotoEntry, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AsyncImage(
                model = photo.photoUri,
                contentDescription = photo.caption,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(16.dp)
            ) {
                Text(text = formatDate(photo.date), color = Color.White, style = MaterialTheme.typography.bodyMedium)
                if (!photo.eventType.isNullOrBlank()) {
                    Text(text = photo.eventType, color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
                if (!photo.caption.isNullOrBlank()) {
                    Text(text = photo.caption, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
