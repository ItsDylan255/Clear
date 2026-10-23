package com.example.clear.ui.akut

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/** Durchschnittliche Veraenderung der Stimmung (danach minus davor) einer Uebung. */
data class ExerciseStat(val average: Double, val count: Int)

/** "Ø +1,5" fuer die Anzeige. */
fun formatAverage(value: Double): String = "Ø " + String.format(Locale.GERMANY, "%+.1f", value)

/** Durchschnitte aller Uebungen fuer die Auswahlliste. */
class ExerciseStatsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).exerciseSessionDao()

    val stats: StateFlow<Map<String, ExerciseStat>> = dao.observeAllCompleted()
        .map { list ->
            list.groupBy { it.exerciseId }.mapValues { (_, sessions) ->
                ExerciseStat(
                    average = sessions.map { (it.moodAfter ?: it.moodBefore) - it.moodBefore }.average(),
                    count = sessions.size
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())
}
