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

/** Version 7 -> 8: Emotion-Tags fuer Briefe. Bestehende Briefe bekommen keine Emotion. */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `letters` ADD COLUMN `emotionTags` TEXT NOT NULL DEFAULT ''")
    }
}

/**
 * Version 8 -> 9: Neue Check-in-Bereiche (Energie, Klarheit, Erholung, Freude, Stimmung,
 * Gedanken, Verbundenheit, Struktur). Alte Check-ins werden geloescht. Ideen aus Schlaf und
 * Alltag wandern nach Erholung und Struktur, Ideen aus "Allgemein" entfallen.
 */
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `check_ins`")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `check_ins` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`timestamp` INTEGER NOT NULL, `energy` INTEGER, `clarity` INTEGER, `recovery` INTEGER, " +
                "`joy` INTEGER, `mood` INTEGER, `thoughts` INTEGER, `connection` INTEGER, `structure` INTEGER)"
        )
        db.execSQL("UPDATE `personal_ideas` SET `categoryId` = 'recovery' WHERE `categoryId` = 'sleep'")
        db.execSQL("UPDATE `personal_ideas` SET `categoryId` = 'structure' WHERE `categoryId` = 'daily_life'")
        db.execSQL("DELETE FROM `personal_ideas` WHERE `categoryId` = 'general'")
    }
}

/**
 * Version 9 -> 10: Jede Idee bekommt drei Texte (Normal, Sinkend, Stark beeintraechtigt).
 * Bestehende Ideen behalten ihren Text fuer die Stufe, fuer die sie gedacht waren.
 */
val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `personal_ideas` ADD COLUMN `textNormal` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `personal_ideas` ADD COLUMN `textSinking` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `personal_ideas` ADD COLUMN `textSevere` TEXT NOT NULL DEFAULT ''")
        db.execSQL("UPDATE `personal_ideas` SET `textNormal` = COALESCE(`description`, '') WHERE `level` = 'Normal'")
        db.execSQL("UPDATE `personal_ideas` SET `textSinking` = COALESCE(`description`, '') WHERE `level` = 'Sinking'")
        db.execSQL("UPDATE `personal_ideas` SET `textSevere` = COALESCE(`description`, '') WHERE `level` = 'Severe'")
    }
}

/** Version 10 -> 11: Abend-Eintraege (Erfolge und drei gute Dinge). */
val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `evening_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`type` TEXT NOT NULL, `text` TEXT NOT NULL, `reason` TEXT, " +
                "`day` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)"
        )
    }
}

/** Version 11 -> 12: Ergebnisse des monatlichen PHQ-9. */
val MIGRATION_11_12 = object : Migration(11, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `phq9_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL, `yearMonth` INTEGER NOT NULL, " +
                "`q1` INTEGER NOT NULL, `q2` INTEGER NOT NULL, `q3` INTEGER NOT NULL, " +
                "`q4` INTEGER NOT NULL, `q5` INTEGER NOT NULL, `q6` INTEGER NOT NULL, " +
                "`q7` INTEGER NOT NULL, `q8` INTEGER NOT NULL, `q9` INTEGER NOT NULL, " +
                "`total` INTEGER NOT NULL)"
        )
    }
}

/** Version 12 -> 13: Durchlaeufe der Akut-Uebungen mit Stimmung davor und danach. */
val MIGRATION_12_13 = object : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `exercise_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`exerciseId` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `moodBefore` INTEGER NOT NULL, " +
                "`endedAt` INTEGER, `moodAfter` INTEGER)"
        )
    }
}

/** Version 13 -> 14: Gedankenprotokolle. */
val MIGRATION_13_14 = object : Migration(13, 14) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `thought_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL, `situation` TEXT NOT NULL, `thought` TEXT NOT NULL, " +
                "`emotion` TEXT NOT NULL, `intensityBefore` INTEGER NOT NULL, `traps` TEXT NOT NULL, " +
                "`helpQuestion` INTEGER NOT NULL, `newView` TEXT NOT NULL, `intensityAfter` INTEGER NOT NULL)"
        )
    }
}

/** Version 14 -> 15: Verhaltensexperimente. */
val MIGRATION_14_15 = object : Migration(14, 15) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `behavior_experiments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL, `thoughtRecordId` INTEGER, `belief` TEXT NOT NULL, " +
                "`certaintyBefore` INTEGER NOT NULL, `plan` TEXT NOT NULL, `outcome` TEXT, " +
                "`certaintyAfter` INTEGER, `completedAt` INTEGER)"
        )
    }
}

/** Version 15 -> 16: Verhaltensexperimente sind entfallen. */
val MIGRATION_15_16 = object : Migration(15, 16) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `behavior_experiments`")
    }
}

/** Version 16 -> 17: Ergebnisse des monatlichen WHO-5. */
val MIGRATION_16_17 = object : Migration(16, 17) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `who5_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL, `yearMonth` INTEGER NOT NULL, " +
                "`q1` INTEGER NOT NULL, `q2` INTEGER NOT NULL, `q3` INTEGER NOT NULL, " +
                "`q4` INTEGER NOT NULL, `q5` INTEGER NOT NULL, `total` INTEGER NOT NULL)"
        )
    }
}

/** Version 17 -> 18: Meine Werte (Anker) und Meine Warnzeichen (Notfallkarte). */
val MIGRATION_17_18 = object : Migration(17, 18) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `personal_values` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `warning_signs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`text` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)"
        )
    }
}

/** Version 18 -> 19: WSAS-Ergebnisse. Die Werte sind jetzt fest in der App, die Tabelle entfaellt. */
val MIGRATION_18_19 = object : Migration(18, 19) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `personal_values`")
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `wsas_results` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`createdAt` INTEGER NOT NULL, " +
                "`q1` INTEGER NOT NULL, `q2` INTEGER NOT NULL, `q3` INTEGER NOT NULL, " +
                "`q4` INTEGER NOT NULL, `q5` INTEGER NOT NULL, `total` INTEGER NOT NULL)"
        )
    }
}
