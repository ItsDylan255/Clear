package com.example.clear.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Ein gespeicherter Check-in. null = nicht beantwortet (niemals 0). */
@Entity(tableName = "check_ins")
data class CheckInEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val energy: Int?,
    val clarity: Int?,
    val recovery: Int?,
    val joy: Int?,
    val mood: Int?,
    val thoughts: Int?,
    val connection: Int?,
    val structure: Int?
)
