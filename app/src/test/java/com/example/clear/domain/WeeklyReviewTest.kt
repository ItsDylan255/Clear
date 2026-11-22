package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import com.example.clear.model.CheckInCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class WeeklyReviewTest {

    private val zone: ZoneId = ZoneOffset.UTC
    // Montag, 16.11.2026
    private val monday: LocalDate = LocalDate.of(2026, 11, 16)

    private fun entry(date: LocalDate, mood: Int?, joy: Int? = null) = CheckInEntry(
        timestamp = date.atTime(20, 0).toInstant(ZoneOffset.UTC).toEpochMilli(),
        mood = mood,
        joy = joy
    )

    @Test fun sundayShowsCurrentWeekOtherDaysPreviousWeek() {
        assertEquals(monday, WeeklyReviewCalculator.reviewWeekStart(monday.plusDays(6)))
        assertEquals(monday, WeeklyReviewCalculator.reviewWeekStart(monday.plusDays(7)))
        assertEquals(monday.minusWeeks(1), WeeklyReviewCalculator.reviewWeekStart(monday.plusDays(5)))
    }

    @Test fun lessThanThreeCheckInsGivesNoReview() {
        val entries = listOf(entry(monday, 5), entry(monday.plusDays(1), 6))
        assertNull(WeeklyReviewCalculator.calculate(entries, monday, zone))
    }

    @Test fun trendComparesWithPreviousWeek() {
        val previous = monday.minusWeeks(1)
        val entries = listOf(
            entry(previous, 4, joy = 6), entry(previous.plusDays(1), 4, joy = 6),
            entry(monday, 6, joy = 6), entry(monday.plusDays(1), 6, joy = 6), entry(monday.plusDays(2), 6, joy = 7)
        )
        val review = WeeklyReviewCalculator.calculate(entries, monday, zone)!!
        val mood = review.categories.first { it.category == CheckInCategory.Mood }
        val joy = review.categories.first { it.category == CheckInCategory.Joy }
        val energy = review.categories.first { it.category == CheckInCategory.Energy }
        assertEquals(WeekTrend.Up, mood.trend)
        assertEquals(WeekTrend.Same, joy.trend)          // 6,33 gegen 6,0: Unterschied unter 0,5
        assertNull(energy.trend)                          // keine Werte
        assertNull(energy.average)
    }

    @Test fun bestAndWorstDayUseAverageOfAllCategories() {
        val entries = listOf(
            entry(monday, 3, joy = 3),
            entry(monday.plusDays(2), 8, joy = 9),
            entry(monday.plusDays(4), 5, joy = null),
            entry(monday.plusDays(5), null, joy = null)   // nur Faktenteil, zaehlt nicht als Tag
        )
        val review = WeeklyReviewCalculator.calculate(entries, monday, zone)!!
        assertEquals(monday.plusDays(2), review.bestDay?.date)
        assertEquals(monday, review.worstDay?.date)
        assertEquals(4, review.checkInCount)
    }
}
