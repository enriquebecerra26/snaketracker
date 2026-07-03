package com.enriquebecerra.snaketracker.domain.model

import com.enriquebecerra.snaketracker.data.local.entity.DefecationLog as DefecationLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.ExpenseRecord as ExpenseRecordEntity
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog as FeedingLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.HealthRecord as HealthRecordEntity
import com.enriquebecerra.snaketracker.data.local.entity.LengthLog as LengthLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.Pet as PetEntity
import com.enriquebecerra.snaketracker.data.local.entity.SheddingLog as SheddingLogEntity
import com.enriquebecerra.snaketracker.data.local.entity.TerrariumLog as TerrariumLogEntity
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

fun HealthRecordEntity.toDomain() = HealthRecord(
    id = id,
    petId = petId,
    date = date,
    type = type,
    title = title,
    description = description,
    vetName = vetName,
    medication = medication,
    dosage = dosage,
    nextVisitDate = nextVisitDate,
    resolved = resolved
)

fun HealthRecord.toEntity() = HealthRecordEntity(
    id = id,
    petId = petId,
    date = date,
    type = type,
    title = title,
    description = description,
    vetName = vetName,
    medication = medication,
    dosage = dosage,
    nextVisitDate = nextVisitDate,
    resolved = resolved
)

fun TerrariumLogEntity.toDomain() = TerrariumLog(
    id = id,
    petId = petId,
    date = date,
    hotSpotTemp = hotSpotTemp,
    coldSideTemp = coldSideTemp,
    humidityPercent = humidityPercent,
    substrateType = substrateType,
    substrateChangedDate = substrateChangedDate,
    heatSource = heatSource,
    notes = notes
)

fun TerrariumLog.toEntity() = TerrariumLogEntity(
    id = id,
    petId = petId,
    date = date,
    hotSpotTemp = hotSpotTemp,
    coldSideTemp = coldSideTemp,
    humidityPercent = humidityPercent,
    substrateType = substrateType,
    substrateChangedDate = substrateChangedDate,
    heatSource = heatSource,
    notes = notes
)

fun ExpenseRecordEntity.toDomain() = ExpenseRecord(
    id = id,
    petId = petId,
    date = date,
    category = category,
    description = description,
    amountMXN = amountMXN,
    notes = notes
)

fun ExpenseRecord.toEntity() = ExpenseRecordEntity(
    id = id,
    petId = petId,
    date = date,
    category = category,
    description = description,
    amountMXN = amountMXN,
    notes = notes
)
