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
