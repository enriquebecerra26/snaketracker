package com.enriquebecerra.snaketracker

import android.app.Application
import com.enriquebecerra.snaketracker.data.local.database.SnakeTrackerDatabase
import com.enriquebecerra.snaketracker.data.repository.FeedingRepository
import com.enriquebecerra.snaketracker.data.repository.LengthRepository
import com.enriquebecerra.snaketracker.data.repository.PetRepository
import com.enriquebecerra.snaketracker.data.repository.WeightRepository

class SnakeTrackerApplication : Application() {

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
}
