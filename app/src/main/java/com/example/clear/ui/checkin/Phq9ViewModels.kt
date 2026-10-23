package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.Phq9Result
import com.example.clear.model.Phq9
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.ZoneId

/** Aktueller Monat als Zahl, zum Beispiel 202610. */
internal fun currentYearMonth(): Int {
    val month = YearMonth.now(ZoneId.systemDefault())
    return month.year * 100 + month.monthValue
}

/** Status des monatlichen Fragebogens. available: fuer diesen Monat noch nicht ausgefuellt. */
class Phq9HubViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).phq9Dao()

    /** Neueste zuerst. null = wird noch geladen. */
    val results: StateFlow<List<Phq9Result>?> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val available: StateFlow<Boolean> = dao.observeAll()
        .map { list -> isQuestionnaireAvailable(list.firstOrNull()?.createdAt) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
}

/** Aenderung einer Frage gegenueber dem letzten Fragebogen. */
data class Phq9Change(val question: Int, val previous: Int, val current: Int)

/** Ergebnis direkt nach dem Ausfuellen. changes == null: es gibt noch keinen frueheren Fragebogen. */
data class Phq9Summary(val total: Int, val band: String, val changes: List<Phq9Change>?)

data class Phq9TestState(
    val index: Int = 0,
    val answers: List<Int?> = List(Phq9.COUNT) { null },
    val summary: Phq9Summary? = null
) {
    val selected: Int? get() = answers[index]
    val isFirst: Boolean get() = index == 0
    val isLast: Boolean get() = index == Phq9.COUNT - 1
    val progress: Float get() = (index + 1f) / Phq9.COUNT
}

class Phq9TestViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).phq9Dao()
    private var saving = false

    private val _state = MutableStateFlow(Phq9TestState())
    val state: StateFlow<Phq9TestState> = _state.asStateFlow()

    fun select(value: Int) = _state.update { s ->
        s.copy(answers = s.answers.toMutableList().also { it[s.index] = value })
    }

    fun next() = _state.update { if (it.isLast || it.selected == null) it else it.copy(index = it.index + 1) }

    fun previous() = _state.update { if (it.isFirst) it else it.copy(index = it.index - 1) }

    /** Speichert den Fragebogen (nur wenn alle neun Fragen beantwortet sind) und zeigt das Ergebnis. */
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
                dao.insert(Phq9Result.of(System.currentTimeMillis(), ym, values))
            }
            val changes = previous?.answers()?.mapIndexed { i, old -> Phq9Change(i, old, values[i]) }
            _state.update { it.copy(summary = Phq9Summary(values.sum(), Phq9.band(values.sum()), changes)) }
        }
    }
}

/** Alle Ergebnisse, aelteste zuerst (fuer die Verlaufsgrafiken). */
class Phq9HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).phq9Dao()

    val results: StateFlow<List<Phq9Result>?> = dao.observeAll()
        .map { it.reversed() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
