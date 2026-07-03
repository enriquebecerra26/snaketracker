package com.enriquebecerra.snaketracker.data.repository

import com.enriquebecerra.snaketracker.data.local.dao.PetDao
import com.enriquebecerra.snaketracker.data.local.entity.Pet
import kotlinx.coroutines.flow.Flow

class PetRepository(private val petDao: PetDao) {

    fun getAllPets(): Flow<List<Pet>> = petDao.getAllPets()

    fun getPetById(petId: Long): Flow<Pet?> = petDao.getPetById(petId)

    suspend fun insertPet(pet: Pet): Long = petDao.insertPet(pet)

    suspend fun updatePet(pet: Pet) = petDao.updatePet(pet)

    suspend fun deletePet(pet: Pet) = petDao.deletePet(pet)
}
