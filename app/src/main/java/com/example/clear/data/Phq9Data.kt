package com.example.clear.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Ein ausgefuellter PHQ-9. q1 bis q9 sind die Punkte (0 bis 3), total die Summe.
 * yearMonth = Jahr * 100 + Monat (zum Beispiel 202610). Zwischen zwei Ergebnissen liegen mindestens 14 Tage.
 */
@Entity(tableName = "phq9_results")
data class Phq9Result(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val yearMonth: Int,
    val q1: Int, val q2: Int, val q3: Int, val q4: Int, val q5: Int,
    val q6: Int, val q7: Int, val q8: Int, val q9: Int,
    val total: Int
) {
    fun answers(): List<Int> = listOf(q1, q2, q3, q4, q5, q6, q7, q8, q9)

    companion object {
        fun of(createdAt: Long, yearMonth: Int, a: List<Int>) = Phq9Result(
            createdAt = createdAt, yearMonth = yearMonth,
            q1 = a[0], q2 = a[1], q3 = a[2], q4 = a[3], q5 = a[4],
            q6 = a[5], q7 = a[6], q8 = a[7], q9 = a[8], total = a.sum()
        )
    }
}

@Dao
interface Phq9Dao {
    @Query("SELECT * FROM phq9_results ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Phq9Result>>

    @Query("SELECT * FROM phq9_results ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): Phq9Result?

    @Query("SELECT COUNT(*) FROM phq9_results WHERE yearMonth = :yearMonth")
    suspend fun countForMonth(yearMonth: Int): Int

    @Insert suspend fun insert(result: Phq9Result): Long
}
