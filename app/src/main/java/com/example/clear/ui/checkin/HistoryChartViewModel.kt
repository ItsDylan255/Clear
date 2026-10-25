package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.CheckInEntry
import com.example.clear.data.ClearDatabase
import com.example.clear.domain.LevelCalculator
import com.example.clear.model.CheckInCategory
import com.example.clear.model.Phq9
import com.example.clear.model.Who5
import com.example.clear.model.Wsas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Ein Wert im Diagramm. note = kurze Einordnung, zum Beispiel "Mittel". */
data class ChartPoint(val timeMillis: Long, val value: Float, val note: String)

data class ChartData(
    val title: String,
    val subtitle: String,
    val points: List<ChartPoint>,
    val yMin: Float,
    val yMax: Float,
    val ticks: List<Float>,
    /** true: Hintergrund in den Level-Farben (nur bei den Check-in-Bereichen). */
    val levelBands: Boolean,
    /** true: Auswahl 7, 30, 90 Tage oder Jahr. false: immer die Jahresansicht (Frageboegen). */
    val allowRange: Boolean
)

/**
 * Daten fuer das Diagramm einer Seite. "kind" kommt aus der Navigation: "phq9", "who5", "wsas"
 * oder die ID eines Check-in-Bereichs ("energy" ...).
 */
class HistoryChartViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val kind: String = handle.get<String>("kind").orEmpty()
    private val db = ClearDatabase.getInstance(app)

    val data: StateFlow<ChartData?> = when (kind) {
        "phq9" -> db.phq9Dao().observeAll().map { list ->
            ChartData(
                title = "PHQ-9",
                subtitle = "",
                points = list.sortedBy { it.createdAt }
                    .map { ChartPoint(it.createdAt, it.total.toFloat(), Phq9.band(it.total)) },
                yMin = 0f, yMax = 27f, ticks = listOf(0f, 5f, 10f, 15f, 20f, 27f), levelBands = false, allowRange = false
            )
        }
        "who5" -> db.who5Dao().observeAll().map { list ->
            ChartData(
                title = "WHO-5",
                subtitle = "",
                points = list.sortedBy { it.createdAt }
                    .map { ChartPoint(it.createdAt, it.percent.toFloat(), Who5.band(it.percent)) },
                yMin = 0f, yMax = 100f, ticks = listOf(0f, 25f, 50f, 75f, 100f), levelBands = false, allowRange = false
            )
        }
        "wsas" -> db.wsasDao().observeAll().map { list ->
            ChartData(
                title = "WSAS",
                subtitle = "",
                points = list.sortedBy { it.createdAt }
                    .map { ChartPoint(it.createdAt, it.total.toFloat(), Wsas.band(it.total)) },
                yMin = 0f, yMax = 40f, ticks = listOf(0f, 10f, 20f, 30f, 40f), levelBands = false, allowRange = false
            )
        }
        else -> {
            val category = CheckInCategory.fromId(kind)
            db.checkInDao().observeAll().map { entries ->
                ChartData(
                    title = category?.title ?: "Verlauf",
                    subtitle = "",
                    points = entries.sortedBy { it.timestamp }.mapNotNull { entry ->
                        valueOf(entry, category)?.let { value ->
                            ChartPoint(
                                entry.timestamp, value.toFloat(),
                                levelName(LevelCalculator.levelOf(value.toDouble()))
                            )
                        }
                    },
                    yMin = 1f, yMax = 10f, ticks = listOf(1f, 4f, 7f, 10f), levelBands = true, allowRange = true
                )
            }
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private fun valueOf(entry: CheckInEntry, category: CheckInCategory?): Int? = when (category) {
        CheckInCategory.Energy -> entry.energy
        CheckInCategory.Clarity -> entry.clarity
        CheckInCategory.Recovery -> entry.recovery
        CheckInCategory.Joy -> entry.joy
        CheckInCategory.Mood -> entry.mood
        CheckInCategory.Thoughts -> entry.thoughts
        CheckInCategory.Connection -> entry.connection
        CheckInCategory.Structure -> entry.structure
        null -> null
    }
}
