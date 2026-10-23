package com.example.clear.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Ein Gedankenprotokoll: Was ist passiert, was ging mir durch den Kopf, welches Gefuehl
 * (Staerke 0 bis 10 davor und danach), welche Denkfallen, welche neue Sichtweise.
 * `emotion` ist ThoughtEmotion.id, `traps` eine Liste von ThinkingTrap.id, `helpQuestion` der Index.
 */
@Entity(tableName = "thought_records")
data class ThoughtRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val situation: String,
    val thought: String,
    val emotion: String,
    val intensityBefore: Int,
    val traps: String,
    val helpQuestion: Int,
    val newView: String,
    val intensityAfter: Int
)

@Dao
interface ThoughtRecordDao {
    @Query("SELECT * FROM thought_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ThoughtRecord>>

    @Query("SELECT * FROM thought_records WHERE id = :id")
    fun observeById(id: Long): Flow<ThoughtRecord?>

    @Insert suspend fun insert(record: ThoughtRecord): Long
    @Delete suspend fun delete(record: ThoughtRecord)
}
