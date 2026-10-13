package com.example.clear.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Ein eigenes Zitat oder ein hilfreicher Gedanke. */
@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val author: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/** Ein Brief an dich selbst. */
@Entity(tableName = "letters")
data class Letter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val text: String,
    val createdAt: Long,
    val updatedAt: Long
)

/** Deine persoenliche Notfallkarte. Es gibt genau eine Zeile (id = 1). */
@Entity(tableName = "emergency_card")
data class EmergencyCard(
    @PrimaryKey val id: Int = 1,
    val contactName: String = "",
    val contactPhone: String = "",
    val contactNote: String = "",
    val safetyPlan: String = ""
)
