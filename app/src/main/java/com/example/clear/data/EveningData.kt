package com.example.clear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Art eines Abend-Eintrags. Wird als Text in der Datenbank gespeichert. */
object EveningType {
    const val SUCCESS = "success"   // Was habe ich heute geschafft?
    const val GOOD = "good"         // Drei gute Dinge
}

/**
 * Ein Eintrag aus der Abend-Eingabe: ein Erfolg oder ein gutes Ding.
 * `reason` (Warum ist das passiert?) gibt es nur bei guten Dingen und ist optional.
 * `day` ist das lokale Datum als Tageszahl (LocalDate.toEpochDay()).
 */
@Entity(tableName = "evening_entries")
data class EveningEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val text: String,
    val reason: String? = null,
    val day: Long,
    val createdAt: Long
)

@Dao
interface EveningDao {
    @Query("SELECT * FROM evening_entries ORDER BY day DESC, id DESC")
    fun observeAll(): Flow<List<EveningEntry>>

    @Query("SELECT * FROM evening_entries WHERE id = :id")
    suspend fun getById(id: Long): EveningEntry?

    @Query("SELECT COUNT(*) FROM evening_entries WHERE day = :day")
    suspend fun countForDay(day: Long): Int

    @Insert suspend fun insert(entry: EveningEntry): Long
    @Update suspend fun update(entry: EveningEntry)
    @Delete suspend fun delete(entry: EveningEntry)
}
