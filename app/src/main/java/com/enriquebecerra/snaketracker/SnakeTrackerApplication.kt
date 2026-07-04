package com.enriquebecerra.snaketracker

import android.app.Application
import com.enriquebecerra.snaketracker.data.local.database.SnakeTrackerDatabase
import com.enriquebecerra.snaketracker.data.repository.DefecationRepository
import com.enriquebecerra.snaketracker.data.repository.ExpenseRepository
import com.enriquebecerra.snaketracker.data.repository.FeedingRepository
import com.enriquebecerra.snaketracker.data.repository.HealthRepository
import com.enriquebecerra.snaketracker.data.repository.LengthRepository
import com.enriquebecerra.snaketracker.data.repository.PetRepository
import com.enriquebecerra.snaketracker.data.repository.SheddingRepository
import com.enriquebecerra.snaketracker.data.repository.TerrariumRepository
import com.enriquebecerra.snaketracker.data.repository.WeightRepository
import com.enriquebecerra.snaketracker.notifications.AlertNotificationHelper
import com.enriquebecerra.snaketracker.notifications.NotificationScheduler

class SnakeTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AlertNotificationHelper.createNotificationChannel(this)
        NotificationScheduler.scheduleDailyAlertCheck(this)
    }

    val database: SnakeTrackerDatabase by lazy {
        SnakeTrackerDatabase.getInstance(this)
    }

    val petRepository: PetRepository by lazy {
        PetRepository(database.petDao())
    }

    val feedingRepository: FeedingRepository by lazy {
        FeedingRepository(database.feedingLogDao())
    }

    val weightRepository: WeightRepository by lazy {
        WeightRepository(database.weightLogDao())
    }

    val lengthRepository: LengthRepository by lazy {
        LengthRepository(database.lengthLogDao())
    }

    val sheddingRepository: SheddingRepository by lazy {
        SheddingRepository(database.sheddingLogDao())
    }

    val defecationRepository: DefecationRepository by lazy {
        DefecationRepository(database.defecationLogDao())
    }

    val healthRepository: HealthRepository by lazy {
        HealthRepository(database.healthRecordDao())
    }

    val terrariumRepository: TerrariumRepository by lazy {
        TerrariumRepository(database.terrariumLogDao())
    }

    val expenseRepository: ExpenseRepository by lazy {
        ExpenseRepository(database.expenseRecordDao())
    }
}
