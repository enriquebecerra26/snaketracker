package com.enriquebecerra.snaketracker.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.enriquebecerra.snaketracker.data.local.dao.BreedingRecordDao
import com.enriquebecerra.snaketracker.data.local.dao.DefecationLogDao
import com.enriquebecerra.snaketracker.data.local.dao.ExpenseRecordDao
import com.enriquebecerra.snaketracker.data.local.dao.FeedingLogDao
import com.enriquebecerra.snaketracker.data.local.dao.HealthRecordDao
import com.enriquebecerra.snaketracker.data.local.dao.LengthLogDao
import com.enriquebecerra.snaketracker.data.local.dao.PetDao
import com.enriquebecerra.snaketracker.data.local.dao.PhotoEntryDao
import com.enriquebecerra.snaketracker.data.local.dao.SheddingLogDao
import com.enriquebecerra.snaketracker.data.local.dao.TerrariumLogDao
import com.enriquebecerra.snaketracker.data.local.dao.WeightLogDao
import com.enriquebecerra.snaketracker.data.local.entity.BreedingRecord
import com.enriquebecerra.snaketracker.data.local.entity.DefecationLog
import com.enriquebecerra.snaketracker.data.local.entity.ExpenseRecord
import com.enriquebecerra.snaketracker.data.local.entity.FeedingLog
import com.enriquebecerra.snaketracker.data.local.entity.HealthRecord
import com.enriquebecerra.snaketracker.data.local.entity.LengthLog
import com.enriquebecerra.snaketracker.data.local.entity.Pet
import com.enriquebecerra.snaketracker.data.local.entity.PhotoEntry
import com.enriquebecerra.snaketracker.data.local.entity.SheddingLog
import com.enriquebecerra.snaketracker.data.local.entity.TerrariumLog
import com.enriquebecerra.snaketracker.data.local.entity.WeightLog

@Database(
    entities = [
        Pet::class,
        FeedingLog::class,
        WeightLog::class,
        LengthLog::class,
        SheddingLog::class,
        DefecationLog::class,
        HealthRecord::class,
        TerrariumLog::class,
        ExpenseRecord::class,
        PhotoEntry::class,
        BreedingRecord::class
    ],
    version = 6,
    exportSchema = false
)
abstract class SnakeTrackerDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun feedingLogDao(): FeedingLogDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun lengthLogDao(): LengthLogDao
    abstract fun sheddingLogDao(): SheddingLogDao
    abstract fun defecationLogDao(): DefecationLogDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun terrariumLogDao(): TerrariumLogDao
    abstract fun expenseRecordDao(): ExpenseRecordDao
    abstract fun photoEntryDao(): PhotoEntryDao
    abstract fun breedingRecordDao(): BreedingRecordDao

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

        // Etapa 6: alimentación avanzada, mudas y defecaciones.
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // feeding_logs se reconstruye porque preyWeight (Double) se reemplaza por
                // preyWeightGrams (Float?) y se agregan varias columnas nuevas.
                db.execSQL(
                    """
                    CREATE TABLE feeding_logs_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        time TEXT NOT NULL DEFAULT '',
                        preyType TEXT NOT NULL,
                        preyCondition TEXT NOT NULL DEFAULT 'Fresca',
                        preySize TEXT NOT NULL DEFAULT 'Mediana',
                        preyWeightGrams REAL,
                        accepted INTEGER NOT NULL,
                        durationMinutes INTEGER,
                        notes TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO feeding_logs_new
                        (id, petId, date, time, preyType, preyCondition, preySize, preyWeightGrams, accepted, durationMinutes, notes)
                    SELECT id, petId, date, '', preyType, 'Fresca', 'Mediana', preyWeight, accepted, NULL, notes
                    FROM feeding_logs
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE feeding_logs")
                db.execSQL("ALTER TABLE feeding_logs_new RENAME TO feeding_logs")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_feeding_logs_petId ON feeding_logs(petId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS shedding_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        bluePhaseStart INTEGER,
                        sheddingStart INTEGER,
                        completedDate INTEGER NOT NULL,
                        wasComplete INTEGER NOT NULL,
                        problems TEXT,
                        humidityPercent INTEGER,
                        notes TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_shedding_logs_petId ON shedding_logs(petId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS defecation_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        notes TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_defecation_logs_petId ON defecation_logs(petId)")
            }
        }

        // Etapa 7: historial médico, condiciones del terrario y registro de gastos.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS health_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        type TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        vetName TEXT,
                        medication TEXT,
                        dosage TEXT,
                        nextVisitDate INTEGER,
                        resolved INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_health_records_petId ON health_records(petId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS terrarium_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        hotSpotTemp REAL,
                        coldSideTemp REAL,
                        humidityPercent INTEGER,
                        substrateType TEXT,
                        substrateChangedDate INTEGER,
                        heatSource TEXT,
                        notes TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_terrarium_logs_petId ON terrarium_logs(petId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS expense_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER,
                        date INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        description TEXT NOT NULL,
                        amountMXN REAL NOT NULL,
                        notes TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE SET NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_expense_records_petId ON expense_records(petId)")
            }
        }

        // Etapa 9: galería de fotos y registros de reproducción.
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS photo_entries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        date INTEGER NOT NULL,
                        photoUri TEXT NOT NULL,
                        caption TEXT,
                        eventType TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_photo_entries_petId ON photo_entries(petId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS breeding_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        petId INTEGER NOT NULL,
                        maleId INTEGER,
                        pairingDate INTEGER,
                        ovulationDate INTEGER,
                        layingDate INTEGER,
                        totalEggs INTEGER,
                        fertileEggs INTEGER,
                        incubationStartDate INTEGER,
                        hatchDate INTEGER,
                        hatchlings INTEGER,
                        incubationTempC REAL,
                        incubationHumidity INTEGER,
                        notes TEXT,
                        FOREIGN KEY(petId) REFERENCES pets(id) ON DELETE CASCADE,
                        FOREIGN KEY(maleId) REFERENCES pets(id) ON DELETE SET NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_breeding_records_petId ON breeding_records(petId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_breeding_records_maleId ON breeding_records(maleId)")
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
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6
                    )
                    .build().also { INSTANCE = it }
            }
        }
    }
}
