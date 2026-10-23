package com.example.clear.domain

import java.time.Instant
import java.time.ZoneId

/** Level eines Check-in-Bereichs: Niedrig, Mittel oder Hoch. */
enum class CategoryLevel { Low, Medium, High }

/** Level, Durchschnitt (eine Nachkommastelle) und Anzahl der Werte der letzten 14 Tage. */
data class CategoryLevelInfo(val level: CategoryLevel, val average: Double, val count: Int)

/**
 * Bestimmt das Level pro Bereich aus dem Durchschnitt der letzten 14 Tage (heute und die
 * 13 Tage davor). Reine Rechenlogik, ohne Android und ohne UI.
 *
 *  - 1,0 bis 3,9  -> Niedrig
 *  - 4,0 bis 6,9  -> Mittel
 *  - 7,0 bis 10,0 -> Hoch
 *
 * Der Durchschnitt wird auf eine Nachkommastelle gerundet. Was du siehst, entscheidet das Level.
 * Nicht beantwortete Fragen fehlen einfach. Gibt es in 14 Tagen keinen Wert, gibt es kein Level (null).
 */
object LevelCalculator {
    const val WINDOW_DAYS = 14L
    private const val LOW_BELOW = 4.0
    private const val MEDIUM_BELOW = 7.0

    fun round1(value: Double): Double = Math.round(value * 10.0) / 10.0

    fun levelOf(average: Double): CategoryLevel {
        val rounded = round1(average)
        return when {
            rounded < LOW_BELOW -> CategoryLevel.Low
            rounded < MEDIUM_BELOW -> CategoryLevel.Medium
            else -> CategoryLevel.High
        }
    }

    fun calculate(
        records: List<CheckInRecord>,
        categoryIds: List<String>,
        now: Instant,
        zone: ZoneId = ZoneId.systemDefault()
    ): Map<String, CategoryLevelInfo?> {
        val firstDay = now.atZone(zone).toLocalDate().minusDays(WINDOW_DAYS - 1)
        val inWindow = records.filter {
            !it.timestamp.isAfter(now) && !it.timestamp.atZone(zone).toLocalDate().isBefore(firstDay)
        }
        return categoryIds.associateWith { id ->
            val values = inWindow.mapNotNull { it.ratings[id] }
            if (values.isEmpty()) null
            else {
                val average = values.average()
                CategoryLevelInfo(levelOf(average), round1(average), values.size)
            }
        }
    }
}
