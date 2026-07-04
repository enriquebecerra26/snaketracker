package com.enriquebecerra.snaketracker.domain.model

enum class AlertType { CRITICA, ADVERTENCIA, INFO }

enum class AlertIcon { FEEDING, WEIGHT, SHEDDING, DEFECATION, HEALTH, TERRARIUM }

data class Alert(
    val petId: Long,
    val petName: String,
    val type: AlertType,
    val message: String,
    val icon: AlertIcon
)
