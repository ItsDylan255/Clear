package com.example.clear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalIdeaDao {
    @Query("SELECT * FROM personal_ideas ORDER BY title COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<PersonalIdea>>

    @Query("SELECT * FROM personal_ideas WHERE id = :id")
    suspend fun getById(id: Long): PersonalIdea?

    @Insert
    suspend fun insert(idea: PersonalIdea): Long

    @Update
    suspend fun update(idea: PersonalIdea)

    @Delete
    suspend fun delete(idea: PersonalIdea)
}

@Dao
interface CheckInDao {
    @Insert
    suspend fun insert(entry: CheckInEntry): Long

    // Wird spaeter fuer die Auswertung gebraucht
    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<CheckInEntry>>

    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): CheckInEntry?

    /** Neuester Check-in im Zeitraum [start, end), z. B. heute. */
    @Query("SELECT * FROM check_ins WHERE timestamp >= :start AND timestamp < :end ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestBetween(start: Long, end: Long): CheckInEntry?

    @Update
    suspend fun update(entry: CheckInEntry)

    @Delete
    suspend fun delete(entry: CheckInEntry)
}
