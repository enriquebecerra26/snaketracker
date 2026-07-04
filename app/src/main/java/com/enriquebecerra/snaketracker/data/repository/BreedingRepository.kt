package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.BreedingRecordDao
import com.enriquebecerra.snaketracker.data.local.entity.BreedingRecord
import kotlinx.coroutines.flow.Flow

class BreedingRepository(private val breedingRecordDao: BreedingRecordDao) {

    fun getBreedingRecordsForPet(petId: Long): Flow<List<BreedingRecord>> =
        breedingRecordDao.getByPetId(petId)

    suspend fun insertBreedingRecord(breedingRecord: BreedingRecord): Long =
        breedingRecordDao.insert(breedingRecord)

    suspend fun deleteBreedingRecord(id: Long) =
        breedingRecordDao.deleteById(id)
}
