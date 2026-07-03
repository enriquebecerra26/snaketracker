package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.SheddingLogDao
import com.enriquebecerra.snaketracker.data.local.entity.SheddingLog
import kotlinx.coroutines.flow.Flow

class SheddingRepository(private val sheddingLogDao: SheddingLogDao) {

    fun getSheddingLogsForPet(petId: Long): Flow<List<SheddingLog>> =
        sheddingLogDao.getByPetId(petId)

    suspend fun insertSheddingLog(sheddingLog: SheddingLog): Long =
        sheddingLogDao.insert(sheddingLog)

    suspend fun deleteSheddingLog(id: Long) =
        sheddingLogDao.deleteById(id)
}
