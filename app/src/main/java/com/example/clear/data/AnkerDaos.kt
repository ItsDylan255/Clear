package com.example.clear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<Quote>>

    @Query("SELECT * FROM quotes WHERE id = :id")
    suspend fun getById(id: Long): Quote?

    @Insert suspend fun insert(quote: Quote): Long
    @Update suspend fun update(quote: Quote)
    @Delete suspend fun delete(quote: Quote)
}

@Dao
interface LetterDao {
    @Query("SELECT * FROM letters ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Letter>>

    @Query("SELECT * FROM letters WHERE id = :id")
    fun observeById(id: Long): Flow<Letter?>

    @Query("SELECT * FROM letters WHERE id = :id")
    suspend fun getById(id: Long): Letter?

    @Insert suspend fun insert(letter: Letter): Long
    @Update suspend fun update(letter: Letter)
    @Delete suspend fun delete(letter: Letter)
}

@Dao
interface EmergencyCardDao {
    @Query("SELECT * FROM emergency_card WHERE id = 1")
    fun observe(): Flow<EmergencyCard?>

    @Query("SELECT * FROM emergency_card WHERE id = 1")
    suspend fun get(): EmergencyCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(card: EmergencyCard)
}
