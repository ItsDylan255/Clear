package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.WsasResult
import com.example.clear.model.Wsas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Status der WSAS. available: seit dem letzten Ausfuellen sind mindestens 14 Tage vergangen. */
class WsasHubViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).wsasDao()

    /** Neueste zuerst. null = wird noch geladen. */
    val results: StateFlow<List<WsasResult>?> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val available: StateFlow<Boolean> = dao.observeAll()
        .map { list -> isQuestionnaireAvailable(list.firstOrNull()?.createdAt) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
}

data class WsasChange(val question: Int, val previous: Int, val current: Int)

/** Ergebnis direkt nach dem Ausfuellen. changes == null: es gibt noch keine fruehere WSAS. */
data class WsasSummary(val total: Int, val band: String, val changes: List<WsasChange>?)

data class WsasTestState(
    val index: Int = 0,
    val answers: List<Int?> = List(Wsas.COUNT) { null },
    val summary: WsasSummary? = null
) {
    val selected: Int? get() = answers[index]
    val isFirst: Boolean get() = index == 0
    val isLast: Boolean get() = index == Wsas.COUNT - 1
    val progress: Float get() = (index + 1f) / Wsas.COUNT
}

class WsasTestViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).wsasDao()
    private var saving = false

    private val _state = MutableStateFlow(WsasTestState())
    val state: StateFlow<WsasTestState> = _state.asStateFlow()

    fun select(value: Int) = _state.update { s ->
        s.copy(answers = s.answers.toMutableList().also { it[s.index] = value })
    }

    fun next() = _state.update { if (it.isLast || it.selected == null) it else it.copy(index = it.index + 1) }

    fun previous() = _state.update { if (it.isFirst) it else it.copy(index = it.index - 1) }

    /** Speichert die WSAS (nur wenn alle fuenf Fragen beantwortet sind) und zeigt das Ergebnis. */
    fun finish() {
        val answers = _state.value.answers
        if (saving || answers.any { it == null }) return
        saving = true
        viewModelScope.launch {
            val values = answers.map { it ?: 0 }
            val previous = dao.getLatest()
            // Zwischen zwei Frageboegen mindestens 14 Tage
            if (isQuestionnaireAvailable(previous?.createdAt)) {
                dao.insert(WsasResult.of(System.currentTimeMillis(), values))
            }
            val changes = previous?.answers()?.mapIndexed { i, old -> WsasChange(i, old, values[i]) }
            val total = values.sum()
            _state.update { it.copy(summary = WsasSummary(total, Wsas.band(total), changes)) }
        }
    }
}

/** Alle Ergebnisse, aelteste zuerst (fuer die Verlaufsgrafiken). */
class WsasHistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).wsasDao()

    val results: StateFlow<List<WsasResult>?> = dao.observeAll()
        .map { it.reversed() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
