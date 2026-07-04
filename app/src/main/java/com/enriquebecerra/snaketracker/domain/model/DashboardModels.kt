package com.enriquebecerra.snaketracker.domain.model

data class PetDashboardSummary(
    val pet: Pet,
    val currentWeight: Double,
    val weightVariationGrams: Double?,
    val daysSinceLastFeeding: Int?,
    val feedingIsOverdue: Boolean,
    val nextFeedingEstimate: Long?,
    val daysSinceLastShedding: Int?,
    val daysSinceLastDefecation: Int?,
    val terrariumHasAlert: Boolean
)
