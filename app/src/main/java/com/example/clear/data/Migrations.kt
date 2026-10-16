package com.example.clear.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Version 1 -> 2: Zitate, Briefe und Notfallkarte. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `quotes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`text` TEXT NOT NULL, `author` TEXT, `createdAt` INTEGER NOT NULL, " +
                "`updatedAt` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `letters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`title` TEXT NOT NULL, `text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, " +
                "`updatedAt` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `emergency_card` (`id` INTEGER NOT NULL, " +
                "`contactName` TEXT NOT NULL, `contactPhone` TEXT NOT NULL, " +
                "`contactNote` TEXT NOT NULL, `safetyPlan` TEXT NOT NULL, PRIMARY KEY(`id`))"
        )
    }
}

/** Version 2 -> 3: Bilder, Musik und Erinnerungen. */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `anchor_images` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`fileName` TEXT NOT NULL, `caption` TEXT, `createdAt` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `music_tracks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`fileName` TEXT NOT NULL, `title` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `reminder_settings` (`id` INTEGER NOT NULL, " +
                "`enabled` INTEGER NOT NULL, `hour` INTEGER NOT NULL, `minute` INTEGER NOT NULL, " +
                "`daysMask` INTEGER NOT NULL, PRIMARY KEY(`id`))"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `reminder_pool` (`quoteId` INTEGER NOT NULL, PRIMARY KEY(`quoteId`))"
        )
    }
}

/** Version 3 -> 4: Emotion-Tags fuer Zitate, Bilder und Musik. */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `quotes` ADD COLUMN `emotionTags` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `anchor_images` ADD COLUMN `emotionTags` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `music_tracks` ADD COLUMN `emotionTags` TEXT NOT NULL DEFAULT ''")
    }
}

/** Version 4 -> 5: Zeitfenster mit zufaelligen Zeiten fuer Erinnerungen. */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `reminder_settings` ADD COLUMN `endHour` INTEGER NOT NULL DEFAULT 21")
        db.execSQL("ALTER TABLE `reminder_settings` ADD COLUMN `endMinute` INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE `reminder_settings` ADD COLUMN `timesPerDay` INTEGER NOT NULL DEFAULT 3")
        db.execSQL("ALTER TABLE `reminder_settings` ADD COLUMN `randomWindow` INTEGER NOT NULL DEFAULT 0")
    }
}

/** Version 5 -> 6: Zustandsstufe fuer persoenliche Ideen. Bestehende Ideen werden "Normal". */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `personal_ideas` ADD COLUMN `level` TEXT NOT NULL DEFAULT 'Normal'")
    }
}

/** Version 6 -> 7: Stimmungsziele (Ruhe, Hoffnung, Energie) fuer Musik. */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `music_tracks` ADD COLUMN `moodGoals` TEXT NOT NULL DEFAULT ''")
    }
}
