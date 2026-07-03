package com.enriquebecerra.snaketracker.domain.model

import com.enriquebecerra.snaketracker.data.local.entity.DefecationLog as DefecationLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog as FeedingLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.LengthLog as LengthLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.Pet as PetEntity
import com.enriquebecerra.snaketracker.data.local.entity.SheddingLog as SheddingLogEntity
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
    time = time,
    preyType = preyType,
    preyCondition = preyCondition,
    preySize = preySize,
    preyWeightGrams = preyWeightGrams,
    accepted = accepted,
    durationMinutes = durationMinutes,
    notes = notes
)

fun FeedingLog.toEntity() = FeedingLogEntity(
    id = id,
    petId = petId,
    date = date,
    time = time,
    preyType = preyType,
    preyCondition = preyCondition,
    preySize = preySize,
    preyWeightGrams = preyWeightGrams,
    accepted = accepted,
    durationMinutes = durationMinutes,
    notes = notes
)

fun WeightLogEntity.toDomain() = WeightLog(
    id = id,
    petId = petId,
    date = date,
    weight = weight,
    notes = notes
)

fun WeightLog.toEntity() = WeightLogEntity(
    id = id,
    petId = petId,
    date = date,
    weight = weight,
    notes = notes
)

fun LengthLogEntity.toDomain() = LengthLog(
    id = id,
    petId = petId,
    date = date,
    lengthCm = lengthCm,
    notes = notes
)

fun LengthLog.toEntity() = LengthLogEntity(
    id = id,
    petId = petId,
    date = date,
    lengthCm = lengthCm,
    notes = notes
)

fun SheddingLogEntity.toDomain() = SheddingLog(
    id = id,
    petId = petId,
    bluePhaseStart = bluePhaseStart,
    sheddingStart = sheddingStart,
    completedDate = completedDate,
    wasComplete = wasComplete,
    problems = problems,
    humidityPercent = humidityPercent,
    notes = notes
)

fun SheddingLog.toEntity() = SheddingLogEntity(
    id = id,
    petId = petId,
    bluePhaseStart = bluePhaseStart,
    sheddingStart = sheddingStart,
    completedDate = completedDate,
    wasComplete = wasComplete,
    problems = problems,
    humidityPercent = humidityPercent,
    notes = notes
)

fun DefecationLogEntity.toDomain() = DefecationLog(
    id = id,
    petId = petId,
    date = date,
    type = type,
    notes = notes
)

fun DefecationLog.toEntity() = DefecationLogEntity(
    id = id,
    petId = petId,
    date = date,
    type = type,
    notes = notes
)
