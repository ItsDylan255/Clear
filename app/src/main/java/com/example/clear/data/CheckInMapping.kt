package com.example.clear.data

import com.example.clear.domain.CheckInRecord
import com.example.clear.model.CheckInCategory
import java.time.Instant

/** Wandelt einen gespeicherten Check-in in das Format der Warnlogik um. null-Werte fehlen einfach. */
fun CheckInEntry.toRecord(): CheckInRecord = CheckInRecord(
    timestamp = Instant.ofEpochMilli(timestamp),
    ratings = buildMap {
        energy?.let { put(CheckInCategory.Energy.id, it) }
        clarity?.let { put(CheckInCategory.Clarity.id, it) }
        recovery?.let { put(CheckInCategory.Recovery.id, it) }
        joy?.let { put(CheckInCategory.Joy.id, it) }
        mood?.let { put(CheckInCategory.Mood.id, it) }
        thoughts?.let { put(CheckInCategory.Thoughts.id, it) }
        connection?.let { put(CheckInCategory.Connection.id, it) }
        structure?.let { put(CheckInCategory.Structure.id, it) }
    }
)
