package com.example.clear.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.clear.tr

/** Art einer Idee. Room speichert den Namen (z. B. "Exercise") in der Datenbank. */
enum class ContentType(private val labelDe: String, private val labelEn: String) {
    Exercise("Übung oder Strategie", "Exercise or strategy"),
    Reminder("Persönliche Erinnerung", "Personal reminder"),
    Quote("Zitat oder hilfreicher Gedanke", "Quote or helpful thought"),
    Other("Sonstiges", "Other");

    val label: String get() = tr(labelDe, labelEn)
}

/** Fuer welche Zustandsstufe einer Kategorie eine Idee gedacht ist. Room speichert den Namen. */
enum class IdeaLevel(private val labelDe: String, private val labelEn: String) {
    Normal("Normal", "Normal"),
    Sinking("Sinkend", "Sinking"),
    Severe("Stark beeinträchtigt", "Severely affected");

    val label: String get() = tr(labelDe, labelEn)
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
