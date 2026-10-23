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

/** Fuer welche Zustandsstufe einer Kategorie eine Idee gedacht ist. Room speichert den Namen. */
enum class IdeaLevel(val label: String) {
    Normal("Normal"),
    Sinking("Sinkend"),
    Severe("Stark beeinträchtigt")
}

/**
 * Eine persönliche Idee. Kein "erledigt"-Feld, denn Ideen sind keine Aufgaben.
 * Jede Idee hat drei Texte, einen je Zustand (zum Beispiel "Bewegung": was gilt bei Normal,
 * bei Sinkend, bei Stark beeintraechtigt). `description` und `level` sind alt und werden
 * nicht mehr benutzt.
 */
@Entity(tableName = "personal_ideas")
data class PersonalIdea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val categoryId: String,      // stabile Kennung, z. B. "energy"
    val contentType: ContentType,
    val createdAt: Long,
    val updatedAt: Long,
    val level: IdeaLevel = IdeaLevel.Normal,
    val textNormal: String = "",
    val textSinking: String = "",
    val textSevere: String = ""
) {
    /** Der Text, der zur gewaehlten Zustandsstufe passt. */
    fun textFor(level: IdeaLevel): String = when (level) {
        IdeaLevel.Normal -> textNormal
        IdeaLevel.Sinking -> textSinking
        IdeaLevel.Severe -> textSevere
    }
}
