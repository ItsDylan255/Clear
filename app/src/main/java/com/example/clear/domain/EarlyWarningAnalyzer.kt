package com.example.clear.domain

import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/**
 * Bestimmt den Warnzustand pro Kategorie. Reine Rechenlogik, ohne Android und ohne UI.
 * Keine Diagnose: Es wird nur verglichen, ob Werte unter DEINEM ueblichen Bereich liegen.
 *
 * Regeln:
 *  - Zu wenig Daten (Baseline fehlt): immer Normal.
 *  - Ein einzelner niedriger Wert loest nie eine Warnung aus.
 *  - Nicht beantwortete Fragen fehlen einfach und zaehlen weder gut noch schlecht.
 *  - Sinkend:        2 von den letzten 3 Werten niedrig, innerhalb von 14 Tagen.
 *  - Weiterhin niedrig: 3 von den letzten 4 niedrig, der neueste auch, innerhalb von 21 Tagen.
 *  - Erholung: ergibt sich von selbst. Eine gute Bewertung senkt "Weiterhin niedrig" nur auf
 *    "Sinkend". Erst eine zweite gute Bewertung bringt "Normal" zurueck.
 *  - Veraltet: Ist der neueste auffaellige Wert aelter als 14 Tage, gilt wieder Normal.
 */
class EarlyWarningAnalyzer(
    private val config: WarningConfig = WarningConfig(),
    private val baselineCalculator: BaselineCalculator = BaselineCalculator(config)
) {

    fun analyzeCategory(
        ratings: List<Rating>,
        now: Instant,
        zone: ZoneId = ZoneId.systemDefault()
    ): CategoryAnalysis {
        val sorted = ratings.filter { !it.timestamp.isAfter(now) }.sortedBy { it.timestamp }
        val baseline = baselineCalculator.calculate(sorted, zone)
            ?: return CategoryAnalysis(WarningState.Normal, null)

        val lowLimit = baseline - config.lowThresholdPoints
        val isLow = { value: Int -> value <= lowLimit }

        val state = when {
            meetsRule(
                sorted, config.continuedWindowRatings, config.continuedMinLow,
                config.continuedMaxSpanDays, requireLatestLow = true, now = now, isLow = isLow
            ) -> WarningState.ContinuedLow

            meetsRule(
                sorted, config.sinkingWindowRatings, config.sinkingMinLow,
                config.sinkingMaxSpanDays, requireLatestLow = false, now = now, isLow = isLow
            ) -> WarningState.Sinking

            else -> WarningState.Normal
        }
        return CategoryAnalysis(state, baseline)
    }

    /** Alle Kategorien auf einmal, plus der allgemeine Hinweis bei mehreren auffaelligen. */
    fun analyzeAll(
        records: List<CheckInRecord>,
        categoryIds: List<String>,
        now: Instant,
        zone: ZoneId = ZoneId.systemDefault()
    ): OverallAnalysis {
        val categories = categoryIds.associateWith { id ->
            val ratings = records.mapNotNull { record ->
                record.ratings[id]?.let { Rating(record.timestamp, it) }
            }
            analyzeCategory(ratings, now, zone)
        }
        val affected = categories.values.count { it.state != WarningState.Normal }
        return OverallAnalysis(categories, showGeneralHint = affected >= config.generalHintMinCategories)
    }

    private fun meetsRule(
        sorted: List<Rating>,
        window: Int,
        minLow: Int,
        maxSpanDays: Long,
        requireLatestLow: Boolean,
        now: Instant,
        isLow: (Int) -> Boolean
    ): Boolean {
        val recent = sorted.takeLast(window)
        val lows = recent.filter { isLow(it.value) }
        if (lows.size < minLow) return false
        if (requireLatestLow && !isLow(recent.last().value)) return false

        val span = Duration.between(lows.first().timestamp, lows.last().timestamp)
        if (span > Duration.ofDays(maxSpanDays)) return false

        val age = Duration.between(lows.last().timestamp, now)
        return age <= Duration.ofDays(config.maxAgeDays)
    }
}
