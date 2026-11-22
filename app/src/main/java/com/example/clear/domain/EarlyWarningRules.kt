package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Regeln der Fruehwarnung. Reine Rechenlogik, ohne Android. Keine Diagnose: verglichen wird nur
 * mit DEINEN eigenen frueheren Werten.
 *
 * - Grundwert: Durchschnitt aller Kategorien ueber die 60 Tage vor den letzten 14 Tagen.
 *   Erst gueltig, wenn in diesem Zeitraum an mindestens 30 Tagen Werte vorliegen.
 * - Ausloeser A: Durchschnitt der letzten 14 Tage mindestens 1,0 Punkt unter dem Grundwert,
 *   bei mindestens 7 Check-ins in diesen 14 Tagen.
 * - Ausloeser B: der neueste PHQ-9 ist mindestens 5 Punkte hoeher als der vorherige.
 * - Nach einer Warnung 14 Tage Pause.
 */
object EarlyWarningRules {
    const val RECENT_DAYS = 14L
    const val BASELINE_DAYS = 60L
    const val MIN_BASELINE_DAYS = 30
    const val MIN_RECENT_CHECK_INS = 7
    const val DROP_POINTS = 1.0
    const val PHQ_RISE = 5
    const val PAUSE_DAYS = 14L

    /** Grundwert oder null, wenn noch keine 30 Tage Daten im Grundwert-Zeitraum liegen. */
    fun baseline(entries: List<CheckInEntry>, today: LocalDate, zone: ZoneId): Double? {
        val recentStart = today.minusDays(RECENT_DAYS - 1)
        val baseStart = recentStart.minusDays(BASELINE_DAYS)
        val base = entries.filter { dateOf(it, zone).let { d -> !d.isBefore(baseStart) && d.isBefore(recentStart) } }
            .filter { it.ratings.isNotEmpty() }
        val days = base.map { dateOf(it, zone) }.toSet().size
        if (days < MIN_BASELINE_DAYS) return null
        return base.flatMap { it.ratings.values }.average()
    }

    /** Ausloeser A: Werte der letzten 14 Tage deutlich unter dem Grundwert. */
    fun valuesDropped(entries: List<CheckInEntry>, today: LocalDate, zone: ZoneId): Boolean {
        val baseline = baseline(entries, today, zone) ?: return false
        val recentStart = today.minusDays(RECENT_DAYS - 1)
        val recent = entries.filter { dateOf(it, zone).let { d -> !d.isBefore(recentStart) && !d.isAfter(today) } }
        if (recent.size < MIN_RECENT_CHECK_INS) return false
        val values = recent.flatMap { it.ratings.values }
        if (values.isEmpty()) return false
        return values.average() <= baseline - DROP_POINTS
    }

    /**
     * Ausloeser B: der neueste PHQ-9 gegen den vorherigen.
     * @param phq9 (id, Zeitpunkt, Gesamtwert) in beliebiger Reihenfolge
     * @return id des neuesten PHQ-9, wenn er mindestens 5 Punkte hoeher ist, sonst null
     */
    fun phqRiseId(phq9: List<Triple<Long, Long, Int>>): Long? {
        val sorted = phq9.sortedBy { it.second }
        if (sorted.size < 2) return null
        val latest = sorted.last()
        val previous = sorted[sorted.size - 2]
        return if (latest.third - previous.third >= PHQ_RISE) latest.first else null
    }

    /** Liegt die letzte Warnung weniger als 14 Tage zurueck? */
    fun inPause(lastWarningAt: Long, now: Instant): Boolean =
        lastWarningAt > 0 && now.toEpochMilli() - lastWarningAt < PAUSE_DAYS * 24 * 3600 * 1000

    private fun dateOf(entry: CheckInEntry, zone: ZoneId): LocalDate =
        Instant.ofEpochMilli(entry.timestamp).atZone(zone).toLocalDate()
}
