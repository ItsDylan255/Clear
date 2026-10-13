package com.example.clear.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Art einer Idee. Room speichert den Namen (z. B. "Exercise") in der Datenbank. */
enum class ContentType(val label: String) {
    Exercise("Übung oder Strategie"),
    Reminder("Persönliche Erinnerung"),
    Quote("Zitat oder hilfreicher Gedanke"),
    Other("Sonstiges")
}

/** Eine persönliche Idee. Kein "erledigt"-Feld, denn Ideen sind keine Aufgaben. */
@Entity(tableName = "personal_ideas")
data class PersonalIdea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val categoryId: String,      // stabile Kennung, z. B. "energy"
    val contentType: ContentType,
    val createdAt: Long,
    val updatedAt: Long
)
