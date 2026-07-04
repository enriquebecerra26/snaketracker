package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.WeightLogDao
import com.enriquebecerra.snaketracker.data.local.entity.WeightLog
import kotlinx.coroutines.flow.Flow

class WeightRepository(private val weightLogDao: WeightLogDao) {

    fun getWeightLogsForPet(petId: Long): Flow<List<WeightLog>> =
        weightLogDao.getWeightLogsForPet(petId)

    fun getAllWeightLogs(): Flow<List<WeightLog>> = weightLogDao.getAllWeightLogs()

    suspend fun insertWeightLog(weightLog: WeightLog): Long =
        weightLogDao.insertWeightLog(weightLog)

    suspend fun deleteWeightLog(weightLog: WeightLog) =
        weightLogDao.deleteWeightLog(weightLog)
}
