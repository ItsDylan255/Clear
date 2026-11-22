package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import com.example.clear.model.CheckInCategory
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs

/** Richtung gegenueber der Vorwoche. */
enum class WeekTrend { Up, Down, Same }

/** Durchschnitt einer Kategorie in der Woche (null = kein Wert) und Richtung zur Vorwoche. */
data class CategoryWeek(
    val category: CheckInCategory,
    val average: Double?,
    val previousAverage: Double?,
    val trend: WeekTrend?
)

/** Ein Tag der Woche mit dem Durchschnitt aller Kategorien und seinen Check-ins. */
data class ReviewDay(
    val date: LocalDate,
    val average: Double,
    val entries: List<CheckInEntry>
)

data class WeeklyReview(
    val weekStart: LocalDate,
    val checkInCount: Int,
    val categories: List<CategoryWeek>,
    val bestDay: ReviewDay?,
    val worstDay: ReviewDay?
) {
    val weekEnd: LocalDate get() = weekStart.plusDays(6)
}

/**
 * Wochenrueckblick Montag bis Sonntag. Reine Rechenlogik, ohne Android.
 * Leere Antworten zaehlen weder gut noch schlecht, sie fehlen einfach im Durchschnitt.
 */
object WeeklyReviewCalculator {
    const val MIN_CHECK_INS = 3
    /** Unterschiede unter diesem Wert gelten als "gleich". */
    const val SAME_THRESHOLD = 0.5

    /**
     * Welche Woche gezeigt wird: am Sonntag die laufende Woche, an allen anderen Tagen
     * die zuletzt abgeschlossene Woche. Rueckgabe = Montag dieser Woche.
     */
    fun reviewWeekStart(today: LocalDate): LocalDate {
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return if (today.dayOfWeek == DayOfWeek.SUNDAY) monday else monday.minusWeeks(1)
    }

    /** Anzahl Check-ins in der Woche ab `weekStart`. */
    fun countInWeek(entries: List<CheckInEntry>, weekStart: LocalDate, zone: ZoneId): Int =
        entries.count { dateOf(it, zone) in weekStart..weekStart.plusDays(6) }

    /** Der Rueckblick, oder null wenn die Woche weniger als [MIN_CHECK_INS] Check-ins hat. */
    fun calculate(entries: List<CheckInEntry>, weekStart: LocalDate, zone: ZoneId): WeeklyReview? {
        val week = entries.filter { dateOf(it, zone) in weekStart..weekStart.plusDays(6) }
        if (week.size < MIN_CHECK_INS) return null
        val previousStart = weekStart.minusWeeks(1)
        val previous = entries.filter { dateOf(it, zone) in previousStart..previousStart.plusDays(6) }

        val categories = CheckInCategory.entries.map { category ->
            val avg = average(week.mapNotNull { it.rating(category) })
            val prev = average(previous.mapNotNull { it.rating(category) })
            val trend = if (avg == null || prev == null) null else when {
                abs(avg - prev) < SAME_THRESHOLD -> WeekTrend.Same
                avg > prev -> WeekTrend.Up
                else -> WeekTrend.Down
            }
            CategoryWeek(category, avg, prev, trend)
        }

        // Tage mit mindestens einem Kategorienwert, Durchschnitt ueber alle Werte des Tages
        val days = week.groupBy { dateOf(it, zone) }.mapNotNull { (date, dayEntries) ->
            average(dayEntries.flatMap { it.ratings.values })?.let { ReviewDay(date, it, dayEntries) }
        }
        // Bei gleichem Durchschnitt gewinnt der fruehere Tag
        val best = days.sortedBy { it.date }.maxByOrNull { it.average }
        val worst = days.sortedBy { it.date }.minByOrNull { it.average }

        return WeeklyReview(weekStart, week.size, categories, best, worst)
    }

    private fun average(values: List<Int>): Double? = if (values.isEmpty()) null else values.average()

    private fun dateOf(entry: CheckInEntry, zone: ZoneId): LocalDate =
        Instant.ofEpochMilli(entry.timestamp).atZone(zone).toLocalDate()
}
