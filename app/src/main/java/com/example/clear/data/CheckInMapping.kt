package com.example.clear.data

import com.example.clear.domain.CheckInRecord
import java.time.Instant

/** Wandelt einen gespeicherten Check-in in das Format der Level-Logik um. null-Werte fehlen einfach. */
fun CheckInEntry.toRecord(): CheckInRecord = CheckInRecord(
    timestamp = Instant.ofEpochMilli(timestamp),
    ratings = ratings.mapKeys { (category, _) -> category.id }
)
