package com.example.clear.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Ein ausgefuellter WHO-5. q1 bis q5 sind die Punkte (0 bis 5), total die Summe (0 bis 25).
 * yearMonth = Jahr * 100 + Monat (zum Beispiel 202610). Zwischen zwei Ergebnissen liegen mindestens 14 Tage.
 */
@Entity(tableName = "who5_results")
data class Who5Result(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val yearMonth: Int,
    val q1: Int, val q2: Int, val q3: Int, val q4: Int, val q5: Int,
    val total: Int
) {
    fun answers(): List<Int> = listOf(q1, q2, q3, q4, q5)
    val percent: Int get() = total * 4

    companion object {
        fun of(createdAt: Long, yearMonth: Int, a: List<Int>) = Who5Result(
            createdAt = createdAt, yearMonth = yearMonth,
            q1 = a[0], q2 = a[1], q3 = a[2], q4 = a[3], q5 = a[4], total = a.sum()
        )
    }
}

@Dao
interface Who5Dao {
    @Query("SELECT * FROM who5_results ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Who5Result>>

    @Query("SELECT * FROM who5_results ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): Who5Result?

    @Query("SELECT COUNT(*) FROM who5_results WHERE yearMonth = :yearMonth")
    suspend fun countForMonth(yearMonth: Int): Int

    @Insert suspend fun insert(result: Who5Result): Long
}
