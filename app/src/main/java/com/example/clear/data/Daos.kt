package com.example.clear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalIdeaDao {
    @Query("SELECT * FROM personal_ideas WHERE categoryId = :categoryId ORDER BY createdAt ASC")
    fun observeByCategory(categoryId: String): Flow<List<PersonalIdea>>

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
}
