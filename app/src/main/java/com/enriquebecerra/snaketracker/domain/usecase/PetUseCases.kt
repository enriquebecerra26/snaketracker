package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.PetRepository
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPetsUseCase(private val repository: PetRepository) {
    operator fun invoke(): Flow<List<Pet>> =
        repository.getAllPets().map { pets -> pets.map { it.toDomain() } }
}

class GetPetByIdUseCase(private val repository: PetRepository) {
    operator fun invoke(petId: Long): Flow<Pet?> =
        repository.getPetById(petId).map { it?.toDomain() }
}

class SavePetUseCase(private val repository: PetRepository) {
    suspend operator fun invoke(pet: Pet): Long =
        if (pet.id == 0L) repository.insertPet(pet.toEntity())
        else {
            repository.updatePet(pet.toEntity())
            pet.id
        }
}

class DeletePetUseCase(private val repository: PetRepository) {
    suspend operator fun invoke(pet: Pet) = repository.deletePet(pet.toEntity())
}
