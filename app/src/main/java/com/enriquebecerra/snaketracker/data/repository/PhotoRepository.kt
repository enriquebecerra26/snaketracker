package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.PhotoEntryDao
import com.enriquebecerra.snaketracker.data.local.entity.PhotoEntry
import kotlinx.coroutines.flow.Flow

class PhotoRepository(private val photoEntryDao: PhotoEntryDao) {

    fun getPhotosForPet(petId: Long): Flow<List<PhotoEntry>> =
        photoEntryDao.getByPetId(petId)

    suspend fun insertPhoto(photoEntry: PhotoEntry): Long =
        photoEntryDao.insert(photoEntry)

    suspend fun deletePhoto(id: Long) =
        photoEntryDao.deleteById(id)
}
