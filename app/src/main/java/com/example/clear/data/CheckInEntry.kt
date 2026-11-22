package com.example.clear.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.clear.model.CheckInCategory
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import kotlinx.coroutines.flow.Flow

/**
 * Ein gespeicherter Check-in. `timestamp` = Datum und Uhrzeit.
 * Teil 1 (Kategorien): Wert 1 bis 10, null = nicht beantwortet (niemals 0).
 * Teil 2 (Faktenteil): Stufen-Kennung aus [CheckInFact], null = leer.
 * Tag und Extras: Kennungen durch Komma getrennt, leer = nichts gewaehlt.
 * Teil 3 ("Was war gut?") liegt wie bisher in den Abend-Eintraegen.
 */
@Entity(tableName = "check_ins")
data class CheckInEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    // Teil 1
    val mood: Int? = null,
    val joy: Int? = null,
    val energy: Int? = null,
    val clarity: Int? = null,
    val thoughts: Int? = null,
    val connection: Int? = null,
    val calm: Int? = null,
    val satisfaction: Int? = null,
    // Teil 2
    val sleep: String? = null,
    val alcohol: String? = null,
    val movement: String? = null,
    val routine: String? = null,
    val contact: String? = null,
    val rest: String? = null,
    val dayTypes: String = "",
    val extraChipIds: String = ""
) {
    /** Wert einer Kategorie oder null. */
    fun rating(category: CheckInCategory): Int? = when (category) {
        CheckInCategory.Mood -> mood
        CheckInCategory.Joy -> joy
        CheckInCategory.Energy -> energy
        CheckInCategory.Clarity -> clarity
        CheckInCategory.Thoughts -> thoughts
        CheckInCategory.Connection -> connection
        CheckInCategory.Calm -> calm
        CheckInCategory.Satisfaction -> satisfaction
    }

    /** Alle beantworteten Kategorien. */
    val ratings: Map<CheckInCategory, Int>
        get() = CheckInCategory.entries.mapNotNull { c -> rating(c)?.let { c to it } }.toMap()

    /** Gewaehlte Stufen-Kennung eines Fakten-Feldes oder null. */
    fun fact(fact: CheckInFact): String? = when (fact) {
        CheckInFact.Sleep -> sleep
        CheckInFact.Alcohol -> alcohol
        CheckInFact.Movement -> movement
        CheckInFact.Routine -> routine
        CheckInFact.Contact -> contact
        CheckInFact.Rest -> rest
    }

    val dayTypeSet: Set<DayType>
        get() = dayTypes.split(',').mapNotNull { DayType.fromId(it.trim()) }.toSet()

    val extraIdSet: Set<Long>
        get() = extraChipIds.split(',').mapNotNull { it.trim().toLongOrNull() }.toSet()

    /** Wurde im Faktenteil irgendetwas ausgefuellt? */
    val hasFacts: Boolean
        get() = CheckInFact.entries.any { fact(it) != null } || dayTypes.isNotBlank() || extraChipIds.isNotBlank()
}

/** Antworten aus Teil 2, wie sie der Check-in sammelt. */
data class CheckInFactAnswers(
    val levels: Map<CheckInFact, String> = emptyMap(),
    val dayTypes: Set<DayType> = emptySet(),
    val extraChipIds: Set<Long> = emptySet()
) {
    val isEmpty: Boolean get() = levels.isEmpty() && dayTypes.isEmpty() && extraChipIds.isEmpty()
}

/**
 * Ein eigener Extras-Chip fuer den Faktenteil. `active = false`: verschwindet aus der Auswahl,
 * bleibt aber in alten Eintraegen erhalten (dort wird er weiter mit Namen angezeigt).
 */
@Entity(tableName = "extra_chips")
data class ExtraChip(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val active: Boolean = true,
    val createdAt: Long
)

@Dao
interface ExtraChipDao {
    @Query("SELECT * FROM extra_chips ORDER BY createdAt ASC, id ASC")
    fun observeAll(): Flow<List<ExtraChip>>

    @Insert suspend fun insert(chip: ExtraChip): Long
    @Update suspend fun update(chip: ExtraChip)
}
