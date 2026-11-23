package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.toRecord
import com.example.clear.domain.CategoryLevelInfo
import com.example.clear.domain.LevelCalculator
import com.example.clear.model.CheckInCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Welche Teile des Check-ins heute schon ausgefuellt sind. */
data class TodayParts(val feeling: Boolean = false, val facts: Boolean = false, val good: Boolean = false) {
    fun isDone(part: Int): Boolean = when (part) {
        1 -> feeling
        2 -> facts
        else -> good
    }

    /** Die noch offenen Teile als Ziffern fuer die Route, z. B. "23". Leer = alles erledigt. */
    val missing: String get() = (1..3).filterNot { isDone(it) }.joinToString("")
}

/** Verlauf eines Bereichs in den letzten 14 Tagen: Punkte fuer die Mini-Linie und der Trend. */
data class AreaTrend(val points: List<TrendPoint>, val trend: Trend)

/**
 * Verbindet die gespeicherten Check-ins mit dem 14-Tage-Level.
 * levels: null = wird noch berechnet. Pro Bereich null = kein Wert in den letzten 14 Tage.
 */
class CheckInWarningsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).checkInDao()
    private val categoryIds = CheckInCategory.entries.map { it.id }

    val levels: StateFlow<Map<String, CategoryLevelInfo?>?> = dao.observeAll()
        .map { entries ->
            LevelCalculator.calculate(
                records = entries.map { it.toRecord() },
                categoryIds = categoryIds,
                now = Instant.now(),
                zone = ZoneId.systemDefault()
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val eveningDao = ClearDatabase.getInstance(app).eveningDao()

    /** Teil 1 und 2 stehen im heutigen Check-in, Teil 3 in den Abend-Eintraegen von heute. */
    val todayParts: StateFlow<TodayParts> = combine(dao.observeAll(), eveningDao.observeAll()) { entries, evening ->
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val todays = entries.filter { Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() == today }
        TodayParts(
            feeling = todays.any { it.ratings.isNotEmpty() },
            facts = todays.any { it.hasFacts },
            good = evening.any { it.day == today.toEpochDay() }
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayParts())

    /**
     * Mini-Linie und Trend je Bereich ueber dieselben 14 Tage wie das Level. Der Schluessel
     * [OVERALL] steht fuer alle Bereiche zusammen (Tagesdurchschnitt aller Werte).
     */
    val areaTrends: StateFlow<Map<String, AreaTrend>> = dao.observeAll()
        .map { entries ->
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val startMs = today.minusDays(LevelCalculator.WINDOW_DAYS - 1).atStartOfDay(zone).toInstant().toEpochMilli()
            val endMs = today.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val span = (endMs - startMs).toFloat()
            val inRange = entries.filter { it.timestamp in startMs until endMs }.sortedBy { it.timestamp }
            val perArea = CheckInCategory.entries.associate { category ->
                val points = inRange.mapNotNull { entry ->
                    entry.rating(category)?.let {
                        TrendPoint(((entry.timestamp - startMs) / span).coerceIn(0f, 1f), it)
                    }
                }
                category.id to AreaTrend(points, trendOf(points.map { it.value.toDouble() }))
            }
            val daily = inRange
                .groupBy { Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() }
                .toSortedMap()
                .values
                .mapNotNull { day -> day.flatMap { it.ratings.values }.takeIf { it.isNotEmpty() }?.average() }
            perArea + (OVERALL to AreaTrend(emptyList(), trendOf(daily)))
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    /** true, wenn heute (lokales Datum) schon ein Check-in gespeichert wurde. */
    val checkedInToday: StateFlow<Boolean> = dao.observeAll()
        .map { entries ->
            val zone = ZoneId.systemDefault()
            val latest = entries.firstOrNull() ?: return@map false
            Instant.ofEpochMilli(latest.timestamp).atZone(zone).toLocalDate() ==
                LocalDate.now(zone)
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    /**
     * Wie viele Tage in Folge eingecheckt wurde. Zaehlt ab heute; ist heute noch offen,
     * ab gestern (die Serie ist dann noch nicht gerissen). 0 = keine laufende Serie.
     */
    val streakDays: StateFlow<Int> = dao.observeAll()
        .map { entries ->
            val zone = ZoneId.systemDefault()
            val days = entries.map { Instant.ofEpochMilli(it.timestamp).atZone(zone).toLocalDate() }.toSet()
            val today = LocalDate.now(zone)
            var day = if (today in days) today else today.minusDays(1)
            var count = 0
            while (day in days) {
                count++
                day = day.minusDays(1)
            }
            count
        }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    companion object {
        const val OVERALL = "overall"
    }
}
