package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.LengthLogDao
import com.enriquebecerra.snaketracker.data.local.entity.LengthLog
import kotlinx.coroutines.flow.Flow

class LengthRepository(private val lengthLogDao: LengthLogDao) {

    fun getLengthLogsForPet(petId: Long): Flow<List<LengthLog>> =
        lengthLogDao.getByPetId(petId)

    suspend fun insertLengthLog(lengthLog: LengthLog): Long =
        lengthLogDao.insert(lengthLog)

    suspend fun deleteLengthLog(id: Long) =
        lengthLogDao.deleteById(id)
}
