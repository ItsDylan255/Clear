package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.CheckInEntry
import com.example.clear.data.ClearDatabase
import com.example.clear.model.CheckInCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId

/** Ein Messpunkt im Verlauf. fraction = Lage im gewaehlten Zeitraum (0 = Anfang, 1 = Ende). */
data class TrendPoint(val fraction: Float, val value: Int)

enum class Trend { None, Single, Up, Down, Flat }

data class CategoryTrend(
    val category: CheckInCategory,
    val points: List<TrendPoint>,
    val trend: Trend
)

/** Verlauf der acht Bereiche ueber einen waehlbaren Zeitraum. rows == null: wird berechnet. */
class CheckInHistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).checkInDao()

    private val _days = MutableStateFlow(30)
    val days: StateFlow<Int> = _days.asStateFlow()

    fun selectDays(days: Int) {
        _days.value = days
    }

    val rows: StateFlow<List<CategoryTrend>?> =
        combine(dao.observeAll(), _days) { entries, days -> compute(entries, days) }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private fun compute(entries: List<CheckInEntry>, days: Int): List<CategoryTrend> {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val startMs = today.minusDays(days - 1L).atStartOfDay(zone).toInstant().toEpochMilli()
        val endMs = today.plusDays(1L).atStartOfDay(zone).toInstant().toEpochMilli()
        val span = (endMs - startMs).toFloat()
        val inRange = entries
            .filter { it.timestamp in startMs until endMs }
            .sortedBy { it.timestamp }

        return CheckInCategory.entries.map { category ->
            val points = inRange.mapNotNull { entry ->
                valueOf(entry, category)?.let { value ->
                    TrendPoint(((entry.timestamp - startMs) / span).coerceIn(0f, 1f), value)
                }
            }
            CategoryTrend(category, points, trendOf(points.map { it.value }))
        }
    }

    private fun valueOf(entry: CheckInEntry, category: CheckInCategory): Int? = when (category) {
        CheckInCategory.Energy -> entry.energy
        CheckInCategory.Clarity -> entry.clarity
        CheckInCategory.Recovery -> entry.recovery
        CheckInCategory.Joy -> entry.joy
        CheckInCategory.Mood -> entry.mood
        CheckInCategory.Thoughts -> entry.thoughts
        CheckInCategory.Connection -> entry.connection
        CheckInCategory.Structure -> entry.structure
    }

    /** Vergleicht den Durchschnitt der ersten Haelfte mit dem der zweiten Haelfte. */
    private fun trendOf(values: List<Int>): Trend {
        if (values.isEmpty()) return Trend.None
        if (values.size == 1) return Trend.Single
        val half = values.size / 2
        val first = values.take(half).average()
        val last = values.takeLast(half).average()
        val diff = last - first
        return when {
            diff > 0.75 -> Trend.Up
            diff < -0.75 -> Trend.Down
            else -> Trend.Flat
        }
    }
}
