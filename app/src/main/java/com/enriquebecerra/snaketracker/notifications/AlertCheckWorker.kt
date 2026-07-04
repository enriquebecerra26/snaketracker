package com.enriquebecerra.snaketracker.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.enriquebecerra.snaketracker.SnakeTrackerApplication
import com.enriquebecerra.snaketracker.domain.model.AlertType
import com.enriquebecerra.snaketracker.domain.usecase.AlertEngine
import kotlinx.coroutines.flow.first

/**
 * Worker diario (ver [NotificationScheduler]) que revisa las alertas activas de todas las
 * mascotas y notifica al usuario si hay alguna crítica.
 */
class AlertCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as SnakeTrackerApplication
        val alertEngine = AlertEngine(
            petRepository = app.petRepository,
            feedingRepository = app.feedingRepository,
            weightRepository = app.weightRepository,
            sheddingRepository = app.sheddingRepository,
            defecationRepository = app.defecationRepository,
            healthRepository = app.healthRepository,
            terrariumRepository = app.terrariumRepository
        )

        val alerts = alertEngine().first()
        val criticalAlerts = alerts.filter { it.type == AlertType.CRITICA }
        AlertNotificationHelper.showCriticalAlertsNotification(applicationContext, criticalAlerts)

        return Result.success()
    }
}
