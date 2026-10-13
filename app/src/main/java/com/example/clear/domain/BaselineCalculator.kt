package com.example.clear.domain

import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Berechnet die persoenliche Vergleichsbasis EINER Kategorie: den Median der bisherigen Bewertungen.
 *
 * Wichtig: Sobald die Basis steht, zaehlen auffaellig niedrige Werte nicht mehr mit.
 * So sinkt die Basis nicht langsam ab, wenn es dir laenger schlechter geht. Sonst wuerde ein
 * dauerhaft schlechter Zustand irgendwann als "normal" gelten.
 *
 * Reine Rechenlogik, ohne Android und ohne UI.
 */
class BaselineCalculator(private val config: WarningConfig = WarningConfig()) {

    /** Median der Vergleichswerte, oder null wenn noch zu wenig Daten vorliegen. */
    fun calculate(ratings: List<Rating>, zone: ZoneId): Double? {
        val sample = mutableListOf<Rating>()
        for (rating in ratings.sortedBy { it.timestamp }) {
            if (isEstablished(sample, zone)) {
                val limit = median(sample.map { it.value }) - config.lowThresholdPoints
                if (rating.value <= limit) continue   // auffaellig niedrig: nicht in die Basis
            }
            sample += rating
        }
        return if (isEstablished(sample, zone)) median(sample.map { it.value }) else null
    }

    /** Genug Bewertungen UND genug Kalendertage? */
    private fun isEstablished(sample: List<Rating>, zone: ZoneId): Boolean {
        if (sample.size < config.minRatingsForBaseline) return false
        val first = sample.first().timestamp.atZone(zone).toLocalDate()
        val last = sample.last().timestamp.atZone(zone).toLocalDate()
        return ChronoUnit.DAYS.between(first, last) + 1 >= config.minDaysForBaseline
    }
}
