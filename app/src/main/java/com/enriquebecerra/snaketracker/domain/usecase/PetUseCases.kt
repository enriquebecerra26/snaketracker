package com.enriquebecerra.snaketracker.domain.usecase

import com.enriquebecerra.snaketracker.data.repository.FeedingRepository
import com.enriquebecerra.snaketracker.data.repository.PetRepository
import com.enriquebecerra.snaketracker.domain.model.Pet
import com.enriquebecerra.snaketracker.domain.model.PetListItem
import com.enriquebecerra.snaketracker.domain.model.toDomain
import com.enriquebecerra.snaketracker.domain.model.toEntity
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetPetsUseCase(private val repository: PetRepository) {
    operator fun invoke(): Flow<List<Pet>> =
        repository.getAllPets().map { pets -> pets.map { it.toDomain() } }
}

class GetPetListItemsUseCase(
    private val petRepository: PetRepository,
    private val feedingRepository: FeedingRepository
) {
    operator fun invoke(): Flow<List<PetListItem>> =
        combine(petRepository.getAllPets(), feedingRepository.getAllFeedingLogs()) { pets, feedingLogs ->
            val lastFeedingByPet = feedingLogs
                .groupBy { it.petId }
                .mapValues { (_, logs) -> logs.maxOf { it.date } }
            val now = System.currentTimeMillis()
            pets.map { pet ->
                val lastFeedingDate = lastFeedingByPet[pet.id]
                val daysSinceLastFeeding = lastFeedingDate?.let {
                    TimeUnit.MILLISECONDS.toDays(now - it).toInt()
                }
                PetListItem(pet = pet.toDomain(), daysSinceLastFeeding = daysSinceLastFeeding)
            }
        }
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
