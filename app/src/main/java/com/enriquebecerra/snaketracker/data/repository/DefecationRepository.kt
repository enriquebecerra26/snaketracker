package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.DefecationLogDao
import com.enriquebecerra.snaketracker.data.local.entity.DefecationLog
import kotlinx.coroutines.flow.Flow

class DefecationRepository(private val defecationLogDao: DefecationLogDao) {

    fun getDefecationLogsForPet(petId: Long): Flow<List<DefecationLog>> =
        defecationLogDao.getByPetId(petId)

    fun getAllDefecationLogs(): Flow<List<DefecationLog>> = defecationLogDao.getAll()

    suspend fun insertDefecationLog(defecationLog: DefecationLog): Long =
        defecationLogDao.insert(defecationLog)

    suspend fun deleteDefecationLog(id: Long) =
        defecationLogDao.deleteById(id)
}
