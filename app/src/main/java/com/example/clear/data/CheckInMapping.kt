package com.example.clear.data

import com.example.clear.domain.CheckInRecord
import com.example.clear.model.CheckInCategory
import java.time.Instant

/** Wandelt einen gespeicherten Check-in in das Format der Warnlogik um. null-Werte fehlen einfach. */
fun CheckInEntry.toRecord(): CheckInRecord = CheckInRecord(
    timestamp = Instant.ofEpochMilli(timestamp),
    ratings = buildMap {
        general?.let { put(CheckInCategory.General.id, it) }
        energy?.let { put(CheckInCategory.Energy.id, it) }
        joy?.let { put(CheckInCategory.Joy.id, it) }
        sleep?.let { put(CheckInCategory.Sleep.id, it) }
        dailyLife?.let { put(CheckInCategory.DailyLife.id, it) }
        connection?.let { put(CheckInCategory.Connection.id, it) }
    }
)
