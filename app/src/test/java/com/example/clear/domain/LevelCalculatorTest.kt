package com.example.clear.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class LevelCalculatorTest {

    private val zone: ZoneId = ZoneOffset.UTC
    private val now: Instant = Instant.parse("2026-02-01T12:00:00Z")

    private fun daysAgo(n: Long): Instant = now.minusSeconds(n * 24 * 3600)
    private fun record(daysAgo: Long, value: Int?, id: String = "energy") =
        CheckInRecord(daysAgo(daysAgo), if (value == null) emptyMap() else mapOf(id to value))

    private fun info(records: List<CheckInRecord>, id: String = "energy") =
        LevelCalculator.calculate(records, listOf(id), now, zone)[id]

    // ---------- Grenzen ----------

    @Test fun lowestRangeIsLow() {
        assertEquals(CategoryLevel.Low, LevelCalculator.levelOf(1.0))
        assertEquals(CategoryLevel.Low, LevelCalculator.levelOf(3.9))
    }

    @Test fun mediumRange() {
        assertEquals(CategoryLevel.Medium, LevelCalculator.levelOf(4.0))
        assertEquals(CategoryLevel.Medium, LevelCalculator.levelOf(6.9))
    }

    @Test fun highRange() {
        assertEquals(CategoryLevel.High, LevelCalculator.levelOf(7.0))
        assertEquals(CategoryLevel.High, LevelCalculator.levelOf(10.0))
    }

    @Test fun averageIsRoundedToOneDecimalBeforeDeciding() {
        assertEquals(CategoryLevel.Medium, LevelCalculator.levelOf(3.96))
        assertEquals(CategoryLevel.High, LevelCalculator.levelOf(6.96))
        assertEquals(CategoryLevel.Low, LevelCalculator.levelOf(3.94))
    }

    // ---------- Berechnung ----------

    @Test fun averageOfSeveralValues() {
        val result = info(listOf(record(0, 3), record(1, 4), record(2, 6)))!!
        assertEquals(4.3, result.average, 0.0001)
        assertEquals(CategoryLevel.Medium, result.level)
        assertEquals(3, result.count)
    }

    @Test fun noValuesGiveNoLevel() {
        assertNull(info(emptyList()))
    }

    @Test fun valuesOlderThan14DaysAreIgnored() {
        // heute und die 13 Tage davor zaehlen, Tag 14 nicht mehr
        val result = info(listOf(record(0, 8), record(13, 8), record(14, 1)))!!
        assertEquals(8.0, result.average, 0.0001)
        assertEquals(2, result.count)
        assertEquals(CategoryLevel.High, result.level)
    }

    @Test fun onlyOldValuesGiveNoLevel() {
        assertNull(info(listOf(record(20, 2), record(30, 2))))
    }

    @Test fun futureValuesAreIgnored() {
        val future = CheckInRecord(now.plusSeconds(3600), mapOf("energy" to 1))
        val result = info(listOf(record(0, 8), future))!!
        assertEquals(8.0, result.average, 0.0001)
    }

    @Test fun missingAnswersAreNeutral() {
        val records = listOf(record(0, 2), record(1, null), record(2, 4))
        val result = info(records)!!
        assertEquals(3.0, result.average, 0.0001)
        assertEquals(2, result.count)
        assertEquals(CategoryLevel.Low, result.level)
    }

    @Test fun categoriesAreIndependent() {
        val records = listOf(
            CheckInRecord(daysAgo(0), mapOf("energy" to 2, "joy" to 9))
        )
        val all = LevelCalculator.calculate(records, listOf("energy", "joy", "mood"), now, zone)
        assertEquals(CategoryLevel.Low, all["energy"]!!.level)
        assertEquals(CategoryLevel.High, all["joy"]!!.level)
        assertNull(all["mood"])
        assertNotNull(all["joy"])
    }

    @Test fun inputOrderDoesNotMatter() {
        val records = listOf(record(0, 3), record(3, 5), record(6, 9))
        assertEquals(info(records), info(records.reversed()))
    }
}
