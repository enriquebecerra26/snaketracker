package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.HealthRecordDao
import com.enriquebecerra.snaketracker.data.local.entity.HealthRecord
import kotlinx.coroutines.flow.Flow

class HealthRepository(private val healthRecordDao: HealthRecordDao) {

    fun getHealthRecordsForPet(petId: Long): Flow<List<HealthRecord>> =
        healthRecordDao.getByPetId(petId)

    suspend fun insertHealthRecord(healthRecord: HealthRecord): Long =
        healthRecordDao.insert(healthRecord)

    suspend fun deleteHealthRecord(id: Long) =
        healthRecordDao.deleteById(id)
}
