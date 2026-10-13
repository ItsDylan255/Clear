package com.example.clear.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Ein Bild. Die Datei liegt im privaten App-Ordner, hier steht nur ihr Name. */
@Entity(tableName = "anchor_images")
data class AnchorImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val caption: String?,
    val createdAt: Long
)

/** Ein Musikstueck (Kopie im privaten App-Ordner). */
@Entity(tableName = "music_tracks")
data class MusicTrack(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileName: String,
    val title: String,
    val createdAt: Long
)

/**
 * Einstellungen der Zitat-Erinnerung. Es gibt genau eine Zeile (id = 1).
 * daysMask: Bit 0 = Montag ... Bit 6 = Sonntag. 127 = jeden Tag.
 */
@Entity(tableName = "reminder_settings")
data class ReminderSettings(
    @PrimaryKey val id: Int = 1,
    val enabled: Boolean = false,
    val hour: Int = 9,
    val minute: Int = 0,
    val daysMask: Int = 127
)

/** Zitate, aus denen die Erinnerung zufaellig eines waehlt. */
@Entity(tableName = "reminder_pool")
data class ReminderPoolEntry(
    @PrimaryKey val quoteId: Long
)
