package com.example.clear.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PersonalIdea::class, CheckInEntry::class,
        Quote::class, Letter::class, EmergencyCard::class,
        AnchorImage::class, MusicTrack::class, ReminderSettings::class, ReminderPoolEntry::class
    ],
    version = 7,
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
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7).build().also { instance = it }
            }
    }
}
