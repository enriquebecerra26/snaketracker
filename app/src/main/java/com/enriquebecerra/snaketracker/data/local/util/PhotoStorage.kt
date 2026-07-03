package com.enriquebecerra.snaketracker.data.local.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Las fotos se copian siempre a almacenamiento interno de la app (files/photos) para que el
 * URI guardado en Room siga siendo válido entre reinicios, sin depender de permisos de
 * lectura sobre content:// URIs externos (galería) que pueden expirar.
 */
object PhotoStorage {

    data class CameraCaptureTarget(val contentUri: Uri, val fileUri: Uri)

    private fun photosDir(context: Context): File =
        File(context.filesDir, "photos").apply { mkdirs() }

    fun createCameraCaptureTarget(context: Context): CameraCaptureTarget {
        val file = File(photosDir(context), "pet_${System.currentTimeMillis()}.jpg")
        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return CameraCaptureTarget(contentUri = contentUri, fileUri = Uri.fromFile(file))
    }

    fun persistPickedImage(context: Context, sourceUri: Uri): Uri? {
        return try {
            val destFile = File(photosDir(context), "pet_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output -> input.copyTo(output) }
            } ?: return null
            Uri.fromFile(destFile)
        } catch (e: IOException) {
            null
        }
    }
}
