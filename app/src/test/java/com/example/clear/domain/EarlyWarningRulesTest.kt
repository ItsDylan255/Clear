package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class EarlyWarningRulesTest {

    private val zone = ZoneOffset.UTC
    private val today: LocalDate = LocalDate.of(2026, 11, 22)

    private fun entry(daysAgo: Long, mood: Int) = CheckInEntry(
        timestamp = today.minusDays(daysAgo).atTime(20, 0).toInstant(ZoneOffset.UTC).toEpochMilli(),
        mood = mood
    )

    /** Grundwert-Zeitraum: Tage 14 bis 73 vor heute. */
    private fun baselineDays(count: Int, mood: Int) = (14L until 14L + count).map { entry(it, mood) }

    @Test fun noBaselineWithFewerThan30Days() {
        assertNull(EarlyWarningRules.baseline(baselineDays(29, 7), today, zone))
        assertEquals(7.0, EarlyWarningRules.baseline(baselineDays(30, 7), today, zone)!!, 0.001)
    }

    @Test fun triggerAWhenOnePointBelowWithSevenCheckIns() {
        val recent = (0L until 7L).map { entry(it, 6) }
        assertTrue(EarlyWarningRules.valuesDropped(baselineDays(40, 7) + recent, today, zone))
    }

    @Test fun noTriggerAWithOnlySixRecentCheckIns() {
        val recent = (0L until 6L).map { entry(it, 3) }
        assertFalse(EarlyWarningRules.valuesDropped(baselineDays(40, 7) + recent, today, zone))
    }

    @Test fun noTriggerAWhenDropIsSmall() {
        val recent = (0L until 10L).map { entry(it, 7) } + entry(11, 5)
        assertFalse(EarlyWarningRules.valuesDropped(baselineDays(40, 7) + recent, today, zone))
    }

    @Test fun triggerBWhenPhqRisesByFive() {
        val t = Instant.parse("2026-11-01T10:00:00Z").toEpochMilli()
        assertEquals(2L, EarlyWarningRules.phqRiseId(listOf(Triple(2L, t + 1000, 14), Triple(1L, t, 9))))
        assertNull(EarlyWarningRules.phqRiseId(listOf(Triple(1L, t, 9), Triple(2L, t + 1000, 13))))
        assertNull(EarlyWarningRules.phqRiseId(listOf(Triple(1L, t, 20))))
    }

    @Test fun pauseLasts14Days() {
        val now = Instant.parse("2026-11-22T10:00:00Z")
        assertTrue(EarlyWarningRules.inPause(now.minusSeconds(13 * 24 * 3600).toEpochMilli(), now))
        assertFalse(EarlyWarningRules.inPause(now.minusSeconds(14 * 24 * 3600).toEpochMilli(), now))
        assertFalse(EarlyWarningRules.inPause(0L, now))
    }
}
