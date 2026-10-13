package com.example.clear.data

import androidx.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                room.Entity
import androidx.room.PrimaryKey

/** Ein gespeicherter Check-in. null = nicht beantwortet (niemals 0). */
@Entity(tableName = "check_ins")
data class CheckInEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val general: Int?,
    val energy: Int?,
    val joy: Int?,
    val sleep: Int?,
    val dailyLife: Int?,
    val connection: Int?
)
