package com.enriquebecerra.snaketracker.domain.model

import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog as FeedingLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.Pet as PetEntity
import com.enriquebecerra.snaketracker.data.local.entity.WeightLog as WeightLogEntity

fun PetEntity.toDomain() = Pet(
    id = id,
    name = name,
    species = species,
    sex = sex,
    morph = morph,
    birthDate = birthDate,
    acquisitionDate = acquisitionDate,
    weight = weight,
    photoUri = photoUri,
    breeder = breeder,
    chipNumber = chipNumber,
    notes = notes
)

fun Pet.toEntity() = PetEntity(
    id = id,
    name = name,
    species = species,
    sex = sex,
    morph = morph,
    birthDate = birthDate,
    acquisitionDate = acquisitionDate,
    weight = weight,
    photoUri = photoUri,
    breeder = breeder,
    chipNumber = chipNumber,
    notes = notes
)

fun FeedingLogEntity.toDomain() = FeedingLog(
    id = id,
    petId = petId,
    date = date,
    preyType = preyType,
    preyWeight = preyWeight,
    accepted = accepted,
    notes = notes
)

fun FeedingLog.toEntity() = FeedingLogEntity(
    id = id,
    petId = petId,
    date = date,
    preyType = preyType,
    preyWeight = preyWeight,
    accepted = accepted,
    notes = notes
)

fun WeightLogEntity.toDomain() = WeightLog(
    id = id,
    petId = petId,
    date = date,
    weight = weight
)

fun WeightLog.toEntity() = WeightLogEntity(
    id = id,
    petId = petId,
    date = date,
    weight = weight
)
