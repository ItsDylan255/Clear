package com.example.clear.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PersonalIdea::class, CheckInEntry::class,
        Quote::class, Letter::class, EmergencyCard::class,
        AnchorImage::class, MusicTrack::class, ReminderSettings::class, ReminderPoolEntry::class,
        EveningEntry::class, Phq9Result::class, ExerciseSession::class,
        ThoughtRecord::class, Who5Result::class,
        WarningSign::class, WsasResult::class
    ],
    version = 19,
    exportSchema = false
)
abstract class ClearDatabase : RoomDatabase() {
    abstract fun personalIdeaDao(): PersonalIdeaDao
    abstract fun checkInDao(): CheckInDao
    abstract fun quoteDao(): QuoteDao
    abstract fun letterDao(): LetterDao
    abstract fun emergencyCardDao(): EmergencyCardDao
    abstract fun imageDao(): ImageDao
    abstract fun musicDao(): MusicDao
    abstract fun reminderDao(): ReminderDao
    abstract fun eveningDao(): EveningDao
    abstract fun phq9Dao(): Phq9Dao
    abstract fun exerciseSessionDao(): ExerciseSessionDao
    abstract fun thoughtRecordDao(): ThoughtRecordDao
    abstract fun who5Dao(): Who5Dao
    abstract fun wsasDao(): WsasDao
    abstract fun warningSignDao(): WarningSignDao

    companion object {
        @Volatile
        private var instance: ClearDatabase? = null

        /** Es gibt genau eine Datenbank in der ganzen App. */
        fun getInstance(context: Context): ClearDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ClearDatabase::class.java,
                    "clear.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13, MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16, MIGRATION_16_17, MIGRATION_17_18, MIGRATION_18_19).build().also { instance = it }
            }
    }
}
