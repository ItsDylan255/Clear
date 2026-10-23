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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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
}
