package com.enriquebecerra.snaketracker.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.enriquebecerra.snaketracker.R
import com.enriquebecerra.snaketracker.domain.model.Alert

object AlertNotificationHelper {

    const val CHANNEL_ID = "snake_alerts"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alertas de SnakeTracker",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alertas críticas sobre la alimentación, salud y terrario de tus mascotas"
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    fun showCriticalAlertsNotification(context: Context, criticalAlerts: List<Alert>) {
        if (criticalAlerts.isEmpty()) return

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) return

        val title = if (criticalAlerts.size == 1) {
            "Alerta crítica: ${criticalAlerts.first().petName}"
        } else {
            "Tienes ${criticalAlerts.size} alertas críticas"
        }
        val petNames = criticalAlerts.map { it.petName }.distinct().joinToString(", ")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_snake)
            .setContentTitle(title)
            .setContentText("Revisa a: $petNames")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(criticalAlerts.joinToString("\n") { "${it.petName}: ${it.message}" })
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
