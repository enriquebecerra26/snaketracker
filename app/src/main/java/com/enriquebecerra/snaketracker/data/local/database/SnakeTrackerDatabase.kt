package com.enriquebecerra.snaketracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.enriquebecerra.snaketracker.data.local.dao.FeedingLogDao
import com.enriquebecerra.snaketracker.data.local.dao.PetDao
import com.enriquebecerra.snaketracker.data.local.dao.WeightLogDao
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog
import com.enriquebecerra.snaketracker.data.local.entity.Pet
import com.enriquebecerra.snaketracker.data.local.entity.WeightLog

@Database(
    entities = [Pet::class, FeedingLog::class, WeightLog::class],
    version = 2,
    exportSchema = false
)
abstract class SnakeTrackerDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun feedingLogDao(): FeedingLogDao
    abstract fun weightLogDao(): WeightLogDao

    companion object {
        private const val DATABASE_NAME = "snaketracker.db"

        // Etapa 4: perfil completo de la mascota (morfo, sexo, adquisición, criador, chip).
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE pets ADD COLUMN sex TEXT NOT NULL DEFAULT 'Desconocido'")
                db.execSQL("ALTER TABLE pets ADD COLUMN morph TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE pets ADD COLUMN acquisitionDate INTEGER")
                db.execSQL("ALTER TABLE pets ADD COLUMN breeder TEXT")
                db.execSQL("ALTER TABLE pets ADD COLUMN chipNumber TEXT")
            }
        }

        @Volatile
        private var INSTANCE: SnakeTrackerDatabase? = null

        fun getInstance(context: Context): SnakeTrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SnakeTrackerDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_1_2).build().also { INSTANCE = it }
            }
        }
    }
}
