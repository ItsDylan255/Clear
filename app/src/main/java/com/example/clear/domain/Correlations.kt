package com.example.clear.domain

import com.example.clear.data.CheckInEntry
import com.example.clear.model.CheckInCategory
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Was verglichen wird: ein Fakten-Feld (mehrere Stufen), eine Tag-Option, ein Extras-Chip oder
 * eine Idee (je mit/ohne).
 */
sealed class CorrelationSubject {
    data class Fact(val fact: CheckInFact) : CorrelationSubject()
    data class Day(val dayType: DayType) : CorrelationSubject()
    data class Extra(val chipId: Long) : CorrelationSubject()
    data class Idea(val ideaId: Long) : CorrelationSubject()
}

/** Eine Gruppe: Stufen-Kennung (Fakten-Feld) oder "with"/"without" (Tag, Extra). */
data class CorrelationGroup(val key: String, val average: Double, val days: Int)

data class Correlation(
    val subject: CorrelationSubject,
    val groups: List<CorrelationGroup>
) {
    /** Groesster Unterschied zwischen den angezeigten Gruppen. */
    val spread: Double get() = groups.maxOf { it.average } - groups.minOf { it.average }
}

/**
 * Zusammenhaenge zwischen Faktenteil und einer Kategorie. Reine Rechenlogik, ohne Android.
 *
 * - Pro Kalendertag wird zusammengefasst: Kategorienwert = Durchschnitt des Tages, Fakten =
 *   neuester Eintrag pro Feld, Tag und Extras = alles, was an dem Tag gewaehlt wurde.
 * - "gleicher Tag": Kategorie am selben Tag wie der Faktenteil. Das gilt auch fuer Schlaf und
 *   Alkohol: Sie zaehlen zu dem Check-in, in dem sie eingetragen wurden.
 * - "naechster Tag": Kategorie am Tag danach.
 * - Ideen zaehlen wie ein Extras-Chip: "mit" = an diesem Tag wurde die Idee beendet
 *   (Durchlauf mit Bewertung), "ohne" = Tag mit Faktenteil ohne beendete Idee.
 * - Eine Gruppe erscheint nur, wenn sie an mindestens [MIN_DAYS] Tagen vorkommt. Ein Eintrag
 *   erscheint nur mit mindestens zwei solchen Gruppen. Sortiert nach groesstem Unterschied.
 */
object CorrelationCalculator {
    const val MIN_FACT_CHECK_INS = 14
    const val MIN_DAYS = 5
    const val WITH = "with"
    const val WITHOUT = "without"

    /** Genug Check-ins mit Faktenteil fuer die Ansicht? */
    fun isAvailable(entries: List<CheckInEntry>): Boolean =
        entries.count { it.hasFacts } >= MIN_FACT_CHECK_INS

    private class DaySummary(
        val ratings: Map<CheckInCategory, Double>,
        val facts: Map<CheckInFact, String>,
        val dayTypes: Set<DayType>,
        val extras: Set<Long>,
        val hasFacts: Boolean
    )

    fun calculate(
        entries: List<CheckInEntry>,
        category: CheckInCategory,
        nextDay: Boolean,
        chipIds: List<Long>,
        zone: ZoneId,
        ideaDays: Map<Long, Set<LocalDate>> = emptyMap()
    ): List<Correlation> {
        val days = entries.groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() }
            .mapValues { (_, list) -> summarize(list) }

        fun target(date: LocalDate): Double? = days[if (nextDay) date.plusDays(1) else date]?.ratings?.get(category)

        val result = mutableListOf<Correlation>()

        CheckInFact.entries.forEach { fact ->
            val groups = fact.options.mapNotNull { option ->
                val values = days.filter { (_, d) -> d.facts[fact] == option.id }.keys.mapNotNull { target(it) }
                group(option.id, values)
            }
            if (groups.size >= 2) result += Correlation(CorrelationSubject.Fact(fact), groups)
        }

        fun withWithout(subject: CorrelationSubject, has: (DaySummary) -> Boolean) {
            val factDays = days.filter { (_, d) -> d.hasFacts }
            val with = factDays.filter { (_, d) -> has(d) }.keys.mapNotNull { target(it) }
            val without = factDays.filter { (_, d) -> !has(d) }.keys.mapNotNull { target(it) }
            val groups = listOfNotNull(group(WITH, with), group(WITHOUT, without))
            if (groups.size == 2) result += Correlation(subject, groups)
        }
        DayType.entries.forEach { day -> withWithout(CorrelationSubject.Day(day)) { day in it.dayTypes } }
        chipIds.forEach { id -> withWithout(CorrelationSubject.Extra(id)) { id in it.extras } }
        ideaDays.forEach { (ideaId, ended) ->
            val factDays = days.filter { (_, d) -> d.hasFacts }
            val with = factDays.keys.filter { it in ended }.mapNotNull { target(it) }
            val without = factDays.keys.filter { it !in ended }.mapNotNull { target(it) }
            val groups = listOfNotNull(group(WITH, with), group(WITHOUT, without))
            if (groups.size == 2) result += Correlation(CorrelationSubject.Idea(ideaId), groups)
        }

        return result.sortedByDescending { it.spread }
    }

    private fun group(key: String, values: List<Double>): CorrelationGroup? =
        if (values.size < MIN_DAYS) null else CorrelationGroup(key, values.average(), values.size)

    private fun summarize(list: List<CheckInEntry>): DaySummary {
        val newestFirst = list.sortedByDescending { it.timestamp }
        val ratings = CheckInCategory.entries.mapNotNull { c ->
            val values = list.mapNotNull { it.rating(c) }
            if (values.isEmpty()) null else c to values.average()
        }.toMap()
        val facts = CheckInFact.entries.mapNotNull { f ->
            newestFirst.firstNotNullOfOrNull { it.fact(f) }?.let { f to it }
        }.toMap()
        return DaySummary(
            ratings = ratings,
            facts = facts,
            dayTypes = list.flatMap { it.dayTypeSet }.toSet(),
            extras = list.flatMap { it.extraIdSet }.toSet(),
            hasFacts = list.any { it.hasFacts }
        )
    }
}
