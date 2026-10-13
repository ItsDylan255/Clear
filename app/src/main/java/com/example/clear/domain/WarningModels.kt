package com.example.clear.domain

import java.time.Instant

/** Die drei Warnzustaende einer Kategorie. */
enum class WarningState { Normal, Sinking, ContinuedLow }

/**
 * Alle Schwellenwerte an einer Stelle. Das sind technische STARTWERTE, keine medizinischen Kriterien.
 * Sie koennen hier spaeter angepasst werden, ohne die Logik zu aendern.
 */
data class WarningConfig(
    // Persoenliche Vergleichsbasis (Baseline)
    val minRatingsForBaseline: Int = 10,
    val minDaysForBaseline: Int = 21,       // Kalendertage, Start- und Endtag zaehlen mit
    // Ab wann ein Wert "auffaellig niedrig" ist: mindestens so viele Punkte unter dem Median
    val lowThresholdPoints: Double = 2.0,
    // "Sinkend": mindestens 2 der letzten 3 Werte sind niedrig und liegen innerhalb von 14 Tagen
    val sinkingWindowRatings: Int = 3,
    val sinkingMinLow: Int = 2,
    val sinkingMaxSpanDays: Long = 14,
    // "Weiterhin niedrig": mindestens 3 der letzten 4 Werte sind niedrig, der neueste auch,
    // alle auffaelligen innerhalb von 21 Tagen
    val continuedWindowRatings: Int = 4,
    val continuedMinLow: Int = 3,
    val continuedMaxSpanDays: Long = 21,
    // Veraltete Daten: der neueste auffaellige Wert darf hoechstens so alt sein
    val maxAgeDays: Long = 14,
    // Allgemeiner Hinweis ab so vielen gleichzeitig auffaelligen Kategorien
    val generalHintMinCategories: Int = 2
)

/** Eine einzelne Bewertung einer Kategorie. Nicht beantwortete Fragen gibt es hier gar nicht. */
data class Rating(val timestamp: Instant, val value: Int)

/** Ein Check-in. Die Map enthaelt nur beantwortete Kategorien (Schluessel = Kategorie-ID). */
data class CheckInRecord(val timestamp: Instant, val ratings: Map<String, Int>)

/** baseline == null bedeutet: noch nicht genug Daten. Dann gibt es nie eine Warnung. */
data class CategoryAnalysis(val state: WarningState, val baseline: Double?)

data class OverallAnalysis(
    val categories: Map<String, CategoryAnalysis>,
    val showGeneralHint: Boolean
)

/** Median einer nicht leeren Liste. */
fun median(values: List<Int>): Double {
    require(values.isNotEmpty()) { "Median einer leeren Liste ist nicht definiert" }
    val sorted = values.sorted()
    val middle = sorted.size / 2
    return if (sorted.size % 2 == 1) sorted[middle].toDouble()
    else (sorted[middle - 1] + sorted[middle]) / 2.0
}
