package com.example.clear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.clear.model.CheckInCategory
import kotlinx.coroutines.flow.Flow

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
 * Jede Idee hat drei Fassungen, eine je Zustand ("wenn es dir normal geht", "wenn es sinkt",
 * "wenn es dir stark beeinträchtigt geht").
 * `topicId` = eigenes Thema, null = "Ohne Thema". `needs` = was die Idee braucht, Kennungen der
 * Kategorien durch Komma getrennt (siehe [IdeaNeeds]), leer = nichts gewählt.
 * `categoryId`, `description` und `level` sind alt und werden nicht mehr benutzt.
 */
@Entity(tableName = "personal_ideas")
data class PersonalIdea(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val categoryId: String = "",
    val contentType: ContentType,
    val createdAt: Long,
    val updatedAt: Long,
    val level: IdeaLevel = IdeaLevel.Normal,
    val textNormal: String = "",
    val textSinking: String = "",
    val textSevere: String = "",
    val topicId: Long? = null,
    val needs: String = ""
) {
    /** Der Text, der zur gewaehlten Fassung passt. */
    fun textFor(level: IdeaLevel): String = when (level) {
        IdeaLevel.Normal -> textNormal
        IdeaLevel.Sinking -> textSinking
        IdeaLevel.Severe -> textSevere
    }

    /** Was die Idee braucht, in fester Reihenfolge. */
    val needSet: Set<CheckInCategory>
        get() = IdeaNeeds.parse(needs)
}

/** Was eine Idee braucht: eine oder mehrere der acht Check-in-Kategorien. */
object IdeaNeeds {
    val all: List<CheckInCategory> get() = CheckInCategory.entries

    fun parse(stored: String): Set<CheckInCategory> =
        stored.split(',').mapNotNull { id -> all.firstOrNull { it.id == id.trim() } }.toSet()

    fun serialize(needs: Set<CheckInCategory>): String =
        all.filter { it in needs }.joinToString(",") { it.id }
}

/** Ein eigenes Thema fuer Ideen. Wird selbst angelegt, umbenannt und geloescht. */
@Entity(tableName = "idea_topics")
data class IdeaTopic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long
)

@Dao
interface IdeaTopicDao {
    @Query("SELECT * FROM idea_topics ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<IdeaTopic>>

    @Insert suspend fun insert(topic: IdeaTopic): Long
    @Update suspend fun update(topic: IdeaTopic)
    @Delete suspend fun delete(topic: IdeaTopic)

    /** Ideen eines Themas in ein anderes Thema verschieben (null = "Ohne Thema"). */
    @Query("UPDATE personal_ideas SET topicId = :to WHERE topicId = :from")
    suspend fun moveIdeas(from: Long, to: Long?)
}
