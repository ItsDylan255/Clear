package com.example.clear.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Eine ausgefuellte WSAS. q1 bis q5 sind die Punkte (0 bis 8), total die Summe (0 bis 40). */
@Entity(tableName = "wsas_results")
data class WsasResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val q1: Int, val q2: Int, val q3: Int, val q4: Int, val q5: Int,
    val total: Int
) {
    fun answers(): List<Int> = listOf(q1, q2, q3, q4, q5)

    companion object {
        fun of(createdAt: Long, a: List<Int>) = WsasResult(
            createdAt = createdAt,
            q1 = a[0], q2 = a[1], q3 = a[2], q4 = a[3], q5 = a[4], total = a.sum()
        )
    }
}

@Dao
interface WsasDao {
    @Query("SELECT * FROM wsas_results ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<WsasResult>>

    @Query("SELECT * FROM wsas_results ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatest(): WsasResult?

    @Insert suspend fun insert(result: WsasResult): Long
}
