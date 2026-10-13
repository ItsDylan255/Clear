package com.example.clear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    @Query("SELECT * FROM anchor_images ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AnchorImage>>

    @Query("SELECT * FROM anchor_images WHERE id = :id")
    fun observeById(id: Long): Flow<AnchorImage?>

    @Insert suspend fun insert(image: AnchorImage): Long

    @Query("SELECT * FROM reminder_settings WHERE id = :id")
    fun observeSettingsById(id: Int): Flow<ReminderSettings?>

    @Query("SELECT * FROM reminder_settings WHERE id = :id")
    suspend fun getSettingsById(id: Int): ReminderSettings?

    @Query("UPDATE anchor_images SET caption = :caption WHERE id = :id")
    suspend fun updateCaption(id: Long, caption: String?)

    @Delete suspend fun delete(image: AnchorImage)
}

@Dao
interface MusicDao {
    @Query("SELECT * FROM music_tracks ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<MusicTrack>>

    @Insert suspend fun insert(track: MusicTrack): Long
    @Delete suspend fun delete(track: MusicTrack)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder_settings WHERE id = 1")
    fun observeSettings(): Flow<ReminderSettings?>

    @Query("SELECT * FROM reminder_settings WHERE id = 1")
    suspend fun getSettings(): ReminderSettings?

    @Query("SELECT * FROM reminder_settings WHERE id = :id")
    fun observeSettingsById(id: Int): Flow<ReminderSettings?>

    @Query("SELECT * FROM reminder_settings WHERE id = :id")
    suspend fun getSettingsById(id: Int): ReminderSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ReminderSettings)

    @Query("SELECT quoteId FROM reminder_pool")
    fun observePoolIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToPool(entry: ReminderPoolEntry)

    @Query("DELETE FROM reminder_pool WHERE quoteId = :quoteId")
    suspend fun removeFromPool(quoteId: Long)

    /** Nur Zitate, die es noch gibt. */
    @Query("SELECT q.* FROM quotes q INNER JOIN reminder_pool p ON p.quoteId = q.id")
    suspend fun getPoolQuotes(): List<Quote>
}