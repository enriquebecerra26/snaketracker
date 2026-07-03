package com.enriquebecerra.snaketracker.ui.common

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.enriquebecerra.snaketracker.R
import com.enriquebecerra.snaketracker.data.local.util.PhotoStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Selector de foto con preview circular y menú para elegir entre cámara o galería.
 * La foto elegida siempre se persiste en almacenamiento interno (ver [PhotoStorage])
 * antes de reportarse vía [onPhotoUriChange], para que el URI sobreviva reinicios de la app.
 */
@Composable
fun PetPhotoPicker(
    photoUri: Uri?,
    onPhotoUriChange: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showMenu by rememberSaveable { mutableStateOf(false) }
    var pendingCameraTarget by remember { mutableStateOf<PhotoStorage.CameraCaptureTarget?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraTarget?.fileUri?.let(onPhotoUriChange)
        }
        pendingCameraTarget = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val target = PhotoStorage.createCameraCaptureTarget(context)
            pendingCameraTarget = target
            cameraLauncher.launch(target.contentUri)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val persistedUri = withContext(Dispatchers.IO) {
                    PhotoStorage.persistPickedImage(context, uri)
                }
                persistedUri?.let(onPhotoUriChange)
            }
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) galleryLauncher.launch("image/*")
    }

    fun onCameraOptionClick() {
        showMenu = false
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val target = PhotoStorage.createCameraCaptureTarget(context)
            pendingCameraTarget = target
            cameraLauncher.launch(target.contentUri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun onGalleryOptionClick() {
        showMenu = false
        val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            galleryPermission
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            galleryLauncher.launch("image/*")
        } else {
            galleryPermissionLauncher.launch(galleryPermission)
        }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_snake),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Box {
            TextButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Text(
                    text = if (photoUri != null) "Cambiar foto" else "Agregar foto",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Cámara") }, onClick = ::onCameraOptionClick)
                DropdownMenuItem(text = { Text("Galería") }, onClick = ::onGalleryOptionClick)
            }
        }
    }
}
