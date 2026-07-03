package com.enriquebecerra.snaketracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.enriquebecerra.snaketracker.data.local.dao.FeedingLogDao
import com.enriquebecerra.snaketracker.data.local.dao.PetDao
import com.enriquebecerra.snaketracker.data.local.dao.WeightLogDao
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog
import com.enriquebecerra.snaketracker.data.local.entity.Pet
import com.enriquebecerra.snaketracker.data.local.entity.WeightLog

@Database(
    entities = [Pet::class, FeedingLog::class, WeightLog::class],
    version = 1,
    exportSchema = false
)
abstract class SnakeTrackerDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun feedingLogDao(): FeedingLogDao
    abstract fun weightLogDao(): WeightLogDao

    companion object {
        private const val DATABASE_NAME = "snaketracker.db"

        @Volatile
        private var INSTANCE: SnakeTrackerDatabase? = null

        fun getInstance(context: Context): SnakeTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SnakeTrackerDatabase::class.java,
                    DATABASE_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}
