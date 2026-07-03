package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.TerrariumLogDao
import com.enriquebecerra.snaketracker.data.local.entity.TerrariumLog
import kotlinx.coroutines.flow.Flow

class TerrariumRepository(private val terrariumLogDao: TerrariumLogDao) {

    fun getTerrariumLogsForPet(petId: Long): Flow<List<TerrariumLog>> =
        terrariumLogDao.getByPetId(petId)

    suspend fun insertTerrariumLog(terrariumLog: TerrariumLog): Long =
        terrariumLogDao.insert(terrariumLog)

    suspend fun deleteTerrariumLog(id: Long) =
        terrariumLogDao.deleteById(id)
}
