package com.enriquebecerra.snaketracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.enriquebecerra.snaketracker.data.local.dao.FeedingLogDao
import com.enriquebecerra.snaketracker.data.local.dao.LengthLogDao
import com.enriquebecerra.snaketracker.data.local.dao.PetDao
import com.enriquebecerra.snaketracker.data.local.dao.WeightLogDao
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog
import com.enriquebecerra.snaketracker.data.local.entity.LengthLog
import com.enriquebecerra.snaketracker.data.local.entity.Pet
import com.enriquebecerra.snaketracker.data.local.entity.WeightLog

@Database(
    entities = [Pet::class, FeedingLog::class, WeightLog::class, LengthLog::class],
    version = 3,
    exportSchema = false
)
abstract class SnakeTrackerDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun feedingLogDao(): FeedingLogDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun lengthLogDao(): LengthLogDao

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

        // Etapa 5: biometría completa (notas de peso y nueva entidad de longitud).
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE weight_logs ADD COLUMN notes TEXT")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS length_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        lengthCm REAL NOT NULL,
                        notes TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_length_logs_petId ON length_logs(petId)")
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { INSTANCE = it }
            }
        }
    }
}
