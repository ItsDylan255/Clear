package com.example.clear.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Ein Durchlauf einer Akut-Uebung mit Stimmung davor und danach (je 1 bis 10).
 * endedAt == null: Die Uebung laeuft noch. moodAfter == null: Stimmung danach wurde nicht angegeben.
 * Nur Durchlaeufe mit beiden Stimmungen zaehlen fuer den Durchschnitt.
 */
@Entity(tableName = "exercise_sessions")
data class ExerciseSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val startedAt: Long,
    val moodBefore: Int,
    val endedAt: Long? = null,
    val moodAfter: Int? = null
)

@Dao
interface ExerciseSessionDao {
    @Insert suspend fun insert(session: ExerciseSession): Long
    @Update suspend fun update(session: ExerciseSession)

    /** Der neueste noch nicht beendete Durchlauf dieser Uebung. */
    @Query(
        "SELECT * FROM exercise_sessions WHERE exerciseId = :exerciseId AND endedAt IS NULL " +
            "ORDER BY startedAt DESC LIMIT 1"
    )
    suspend fun getOpen(exerciseId: String): ExerciseSession?

    /** Abgeschlossene Durchlaeufe mit beiden Stimmungen, neueste zuerst. */
    @Query(
        "SELECT * FROM exercise_sessions WHERE exerciseId = :exerciseId AND moodAfter IS NOT NULL " +
            "ORDER BY startedAt DESC"
    )
    fun observeCompleted(exerciseId: String): Flow<List<ExerciseSession>>

    @Query("SELECT * FROM exercise_sessions WHERE moodAfter IS NOT NULL")
    fun observeAllCompleted(): Flow<List<ExerciseSession>>
}
