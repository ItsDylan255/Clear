package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import com.example.clear.model.CheckInCategory
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/** Ein Wert damals und heute. null = kein Wert vorhanden. */
data class ThenNowValue(val then: Double?, val now: Double?) {
    val difference: Double? get() = if (then != null && now != null) now - then else null
}

data class ThenAndNow(
    val categories: Map<CheckInCategory, ThenNowValue>,
    val phq9: ThenNowValue,
    val who5: ThenNowValue
)

/**
 * "Damals und heute". Reine Rechenlogik, ohne Android.
 * Damals: Durchschnitt pro Kategorie ueber die ersten 14 Check-ins, erster PHQ-9 und WHO-5.
 * Heute: Durchschnitt der letzten 14 Tage, letzter PHQ-9 und WHO-5.
 * Erst ab [MIN_CHECK_INS] Check-ins sichtbar.
 */
object ThenAndNowCalculator {
    const val MIN_CHECK_INS = 28
    const val START_CHECK_INS = 14
    const val RECENT_DAYS = 14L

    /**
     * @param phq9 Gesamtwerte mit Zeitpunkt, beliebige Reihenfolge
     * @param who5 Prozentwerte mit Zeitpunkt, beliebige Reihenfolge
     */
    fun calculate(
        entries: List<CheckInEntry>,
        phq9: List<Pair<Long, Int>>,
        who5: List<Pair<Long, Int>>,
        now: Instant,
        zone: ZoneId
    ): ThenAndNow? {
        if (entries.size < MIN_CHECK_INS) return null
        val sorted = entries.sortedBy { it.timestamp }
        val start = sorted.take(START_CHECK_INS)
        val from = ZonedDateTime.ofInstant(now, zone).toLocalDate().minusDays(RECENT_DAYS - 1)
            .atStartOfDay(zone).toInstant().toEpochMilli()
        val recent = sorted.filter { it.timestamp >= from && it.timestamp <= now.toEpochMilli() }

        val categories = CheckInCategory.entries.associateWith { category ->
            ThenNowValue(
                then = average(start.mapNotNull { it.rating(category) }),
                now = average(recent.mapNotNull { it.rating(category) })
            )
        }
        return ThenAndNow(categories, firstAndLast(phq9), firstAndLast(who5))
    }

    private fun firstAndLast(values: List<Pair<Long, Int>>): ThenNowValue {
        val sorted = values.sortedBy { it.first }
        return ThenNowValue(sorted.firstOrNull()?.second?.toDouble(), sorted.lastOrNull()?.second?.toDouble())
    }

    private fun average(values: List<Int>): Double? = if (values.isEmpty()) null else values.average()
}
