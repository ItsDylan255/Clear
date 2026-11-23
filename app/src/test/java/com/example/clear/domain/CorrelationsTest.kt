package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import com.example.clear.model.CheckInCategory
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class CorrelationsTest {

    private val zone = ZoneOffset.UTC
    private val start: LocalDate = LocalDate.of(2026, 10, 1)

    private fun entry(day: Long, mood: Int?, sleep: String? = null, days: String = "", extras: String = "") =
        CheckInEntry(
            timestamp = start.plusDays(day).atTime(20, 0).toInstant(ZoneOffset.UTC).toEpochMilli(),
            mood = mood, sleep = sleep, dayTypes = days, extraChipIds = extras
        )

    @Test fun availableFrom14CheckInsWithFacts() {
        val withFacts = (0L until 13L).map { entry(it, 5, sleep = "good") }
        assertFalse(CorrelationCalculator.isAvailable(withFacts + entry(20, 5)))
        assertTrue(CorrelationCalculator.isAvailable(withFacts + entry(20, 5, sleep = "bad")))
    }

    @Test fun sameDayGroupsByLevelAndSkipsRareLevels() {
        // 6 Tage gut geschlafen (Stimmung 8), 5 Tage schlecht (Stimmung 4), 2 Tage okay (zu selten)
        val entries = (0L until 6L).map { entry(it, 8, sleep = "good") } +
            (6L until 11L).map { entry(it, 4, sleep = "bad") } +
            (11L until 13L).map { entry(it, 6, sleep = "okay") }
        val sleep = CorrelationCalculator.calculate(entries, CheckInCategory.Mood, false, emptyList(), zone)
            .first { it.subject == CorrelationSubject.Fact(CheckInFact.Sleep) }
        assertEquals(listOf("bad", "good"), sleep.groups.map { it.key })
        assertEquals(4.0, sleep.spread, 0.001)
    }

    @Test fun nextDayUsesFollowingDay() {
        // Abwechselnd: Tag mit Extra 1 (Stimmung 5), danach Tag ohne Extra (Stimmung 9)
        val entries = (0L until 20L).map { d ->
            if (d % 2 == 0L) entry(d, 5, extras = "1", sleep = "okay") else entry(d, 9, sleep = "okay")
        }
        val same = CorrelationCalculator.calculate(entries, CheckInCategory.Mood, false, listOf(1L), zone)
            .first { it.subject == CorrelationSubject.Extra(1L) }
        val next = CorrelationCalculator.calculate(entries, CheckInCategory.Mood, true, listOf(1L), zone)
            .first { it.subject == CorrelationSubject.Extra(1L) }
        assertEquals(5.0, same.groups.first { it.key == CorrelationCalculator.WITH }.average, 0.001)
        assertEquals(9.0, next.groups.first { it.key == CorrelationCalculator.WITH }.average, 0.001)
    }

    @Test fun ideaCountsLikeExtraWithMinimumDays() {
        // Idee 7 an 5 Tagen beendet (Stimmung 8), 6 Tage ohne (Stimmung 4)
        val entries = (0L until 11L).map { d -> entry(d, if (d < 5) 8 else 4, sleep = "okay") }
        val ended = (0L until 5L).map { start.plusDays(it) }.toSet()
        val idea = CorrelationCalculator.calculate(
            entries, CheckInCategory.Mood, false, emptyList(), zone, ideaDays = mapOf(7L to ended)
        ).first { it.subject == CorrelationSubject.Idea(7L) }
        assertEquals(8.0, idea.groups.first { it.key == CorrelationCalculator.WITH }.average, 0.001)
        assertEquals(4.0, idea.groups.first { it.key == CorrelationCalculator.WITHOUT }.average, 0.001)

        // Nur an 4 Tagen beendet: zu selten, kein Eintrag
        val rare = CorrelationCalculator.calculate(
            entries, CheckInCategory.Mood, false, emptyList(), zone, ideaDays = mapOf(7L to ended.drop(1).toSet())
        )
        assertTrue(rare.none { it.subject == CorrelationSubject.Idea(7L) })
    }

    @Test fun sortedByLargestDifference() {
        val entries = (0L until 12L).map { d ->
            entry(d, if (d < 6) 9 else 3, sleep = if (d < 6) "good" else "bad", days = if (d % 2 == 0L) DayType.Work.id else "")
        }
        val list = CorrelationCalculator.calculate(entries, CheckInCategory.Mood, false, emptyList(), zone)
        assertEquals(CorrelationSubject.Fact(CheckInFact.Sleep), list.first().subject)
    }
}
