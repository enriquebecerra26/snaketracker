package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.PhotoRepository
import com.enriquebecerra.snaketracker.domain.model.PhotoEntry
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPhotosUseCase(private val repository: PhotoRepository) {
    operator fun invoke(petId: Long): Flow<List<PhotoEntry>> =
        repository.getPhotosForPet(petId).map { photos -> photos.map { it.toDomain() } }
}

class SavePhotoUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(photoEntry: PhotoEntry): Long =
        repository.insertPhoto(photoEntry.toEntity())
}

class DeletePhotoUseCase(private val repository: PhotoRepository) {
    suspend operator fun invoke(photoEntry: PhotoEntry) =
        repository.deletePhoto(photoEntry.id)
}
