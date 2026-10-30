package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.Who5Result
import com.example.clear.model.Who5
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Status des WHO-5. available: seit dem letzten Ausfuellen sind mindestens 14 Tage vergangen. */
class Who5HubViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).who5Dao()

    /** Neueste zuerst. null = wird noch geladen. */
    val results: StateFlow<List<Who5Result>?> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val available: StateFlow<Boolean> = dao.observeAll()
        .map { list -> isQuestionnaireAvailable(list.firstOrNull()?.createdAt) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
}

/** Aenderung einer Frage gegenueber dem letzten WHO-5. */
data class Who5Change(val question: Int, val previous: Int, val current: Int)

/** Ergebnis direkt nach dem Ausfuellen. changes == null: es gibt noch keinen frueheren WHO-5. */
data class Who5Summary(val percent: Int, val band: String, val changes: List<Who5Change>?)

data class Who5TestState(
    val index: Int = 0,
    val answers: List<Int?> = List(Who5.COUNT) { null },
    val summary: Who5Summary? = null
) {
    val selected: Int? get() = answers[index]
    val isFirst: Boolean get() = index == 0
    val isLast: Boolean get() = index == Who5.COUNT - 1
    val progress: Float get() = (index + 1f) / Who5.COUNT
}

class Who5TestViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).who5Dao()
    private var saving = false

    private val _state = MutableStateFlow(Who5TestState())
    val state: StateFlow<Who5TestState> = _state.asStateFlow()

    fun select(value: Int) = _state.update { s ->
        s.copy(answers = s.answers.toMutableList().also { it[s.index] = value })
    }

    fun next() = _state.update { if (it.isLast || it.selected == null) it else it.copy(index = it.index + 1) }

    fun previous() = _state.update { if (it.isFirst) it else it.copy(index = it.index - 1) }

    /** Speichert den WHO-5 (nur wenn alle fuenf Fragen beantwortet sind) und zeigt das Ergebnis. */
    fun finish() {
        val answers = _state.value.answers
        if (saving || answers.any { it == null }) return
        saving = true
        viewModelScope.launch {
            val values = answers.map { it ?: 0 }
            val ym = currentYearMonth()
            val previous = dao.getLatest()
            // Zwischen zwei Frageboegen mindestens 14 Tage
            if (isQuestionnaireAvailable(previous?.createdAt)) {
                dao.insert(Who5Result.of(System.currentTimeMillis(), ym, values))
            }
            val changes = previous?.answers()?.mapIndexed { i, old -> Who5Change(i, old, values[i]) }
            val percent = Who5.percent(values.sum())
            _state.update { it.copy(summary = Who5Summary(percent, Who5.band(percent), changes)) }
        }
    }
}

/** Alle Ergebnisse, aelteste zuerst (fuer die Verlaufsgrafiken). */
class Who5HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).who5Dao()

    val results: StateFlow<List<Who5Result>?> = dao.observeAll()
        .map { it.reversed() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
