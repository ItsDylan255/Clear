package com.example.clear.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Ein eigenes Warnzeichen (Notfallkarte). */
@Entity(tableName = "warning_signs")
data class WarningSign(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val createdAt: Long
)

@Dao
interface WarningSignDao {
    @Query("SELECT * FROM warning_signs ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<WarningSign>>

    @Query("SELECT * FROM warning_signs WHERE id = :id")
    suspend fun getById(id: Long): WarningSign?

    @Insert suspend fun insert(sign: WarningSign): Long
    @Update suspend fun update(sign: WarningSign)

    @Query("DELETE FROM warning_signs WHERE id = :id")
    suspend fun deleteById(id: Long)
}
