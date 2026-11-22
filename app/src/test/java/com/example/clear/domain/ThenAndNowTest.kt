package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import com.example.clear.model.CheckInCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset

class ThenAndNowTest {

    private val zone = ZoneOffset.UTC
    private val now: Instant = Instant.parse("2026-11-22T12:00:00Z")
    private fun daysAgo(n: Long) = now.minusSeconds(n * 24 * 3600).toEpochMilli()

    @Test fun hiddenBelow28CheckIns() {
        val entries = (0 until 27).map { CheckInEntry(timestamp = daysAgo(it.toLong()), mood = 5) }
        assertNull(ThenAndNowCalculator.calculate(entries, emptyList(), emptyList(), now, zone))
    }

    @Test fun startUsesFirst14AndNowUsesLast14Days() {
        // 40 Check-ins, einer pro Tag: die aeltesten 14 mit Stimmung 3, die letzten 14 Tage mit 7
        val entries = (0 until 40).map { i ->
            val ago = (39 - i).toLong()
            CheckInEntry(timestamp = daysAgo(ago), mood = if (i < 14) 3 else if (ago < 14) 7 else 5)
        }
        val result = ThenAndNowCalculator.calculate(
            entries,
            phq9 = listOf(daysAgo(30) to 15, daysAgo(2) to 8),
            who5 = listOf(daysAgo(2) to 60, daysAgo(30) to 40),
            now = now, zone = zone
        )!!
        val mood = result.categories.getValue(CheckInCategory.Mood)
        assertEquals(3.0, mood.then!!, 0.001)
        assertEquals(7.0, mood.now!!, 0.001)
        assertEquals(4.0, mood.difference!!, 0.001)
        assertEquals(-7.0, result.phq9.difference!!, 0.001)
        assertEquals(20.0, result.who5.difference!!, 0.001)
        assertNull(result.categories.getValue(CheckInCategory.Joy).difference)
    }
}
