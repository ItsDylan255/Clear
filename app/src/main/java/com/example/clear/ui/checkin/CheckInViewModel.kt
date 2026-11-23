package com.example.clear.ui.checkin

import com.example.clear.data.EarlyWarningStore
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.example.clear.data.factAnswers
import androidx.lifecycle.viewModelScope
import com.example.clear.data.CheckInFactAnswers
import com.example.clear.data.CheckInRepository
import com.example.clear.data.ExtraChip
import com.example.clear.data.ExtraChipRepository
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.clear.data.ClearDatabase
import com.example.clear.model.CheckInCategory
import com.example.clear.watch.WatchSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Alles, was der Check-in-Bildschirm zum Anzeigen braucht. */
data class CheckInUiState(
    val index: Int = 0,
    // Nur beantwortete Bereiche stehen in der Map. Fehlend = nicht beantwortet (niemals 0).
    val answers: Map<CheckInCategory, Int> = emptyMap(),
    // Teil 2 (Faktenteil). Fehlende Felder = leer.
    val facts: CheckInFactAnswers = CheckInFactAnswers()
) {
    val categories: List<CheckInCategory> get() = CheckInCategory.entries
    val current: CheckInCategory get() = categories[index]
    val selected: Int? get() = answers[current]
    val isFirst: Boolean get() = index == 0
    val isLast: Boolean get() = index == categories.lastIndex
    val progress: Float get() = (index + 1f) / categories.size
}

/**
 * Teile eines Check-ins: 1 "Wie geht es mir?", 2 "Was habe ich gemacht?", 3 "Was war gut?".
 * In der Route stehen die Teile als Ziffern, z. B. "123" oder "2".
 */
fun parseCheckInParts(value: String?): List<Int> =
    value.orEmpty().mapNotNull { it.digitToIntOrNull() }.filter { it in 1..3 }.distinct().sorted()
        .ifEmpty { listOf(1, 2, 3) }

class CheckInViewModel(application: Application, handle: SavedStateHandle) : AndroidViewModel(application) {
    private val repository =
        CheckInRepository(ClearDatabase.getInstance(application).checkInDao())

    /** Welche Teile dieser Durchlauf zeigt, in Reihenfolge. */
    val parts: List<Int> = parseCheckInParts(handle.get<String>("parts"))
    private val chipRepository =
        ExtraChipRepository(ClearDatabase.getInstance(application).extraChipDao())

    /** Aktive Extras-Chips fuer die Auswahl. Deaktivierte erscheinen hier nicht. */
    val activeChips: StateFlow<List<ExtraChip>> = chipRepository.observeAll()
        .map { list -> list.filter { it.active } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(CheckInUiState())
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()

    private var saving = false

    init {
        // Was heute schon gespeichert ist, steht vorausgefuellt da und wird beim Speichern ersetzt
        viewModelScope.launch {
            val today = repository.getToday() ?: return@launch
            _state.update { s ->
                s.copy(
                    answers = if (1 in parts) today.ratings else s.answers,
                    facts = if (2 in parts) today.factAnswers() else s.facts
                )
            }
        }
    }

    /** Zahl antippen. Nochmal dieselbe Zahl antippen = Antwort wieder entfernen. */
    fun select(value: Int) = _state.update { s ->
        val newAnswers =
            if (s.answers[s.current] == value) s.answers - s.current
            else s.answers + (s.current to value)
        s.copy(answers = newAnswers)
    }

    /** Stufe eines Fakten-Feldes antippen. Nochmal dieselbe Stufe = wieder leer. */
    fun toggleFact(fact: CheckInFact, optionId: String) = _state.update { s ->
        val levels = s.facts.levels
        val newLevels = if (levels[fact] == optionId) levels - fact else levels + (fact to optionId)
        s.copy(facts = s.facts.copy(levels = newLevels))
    }

    fun toggleDayType(dayType: DayType) = _state.update { s ->
        val days = s.facts.dayTypes
        s.copy(facts = s.facts.copy(dayTypes = if (dayType in days) days - dayType else days + dayType))
    }

    fun toggleExtra(chipId: Long) = _state.update { s ->
        val ids = s.facts.extraChipIds
        s.copy(facts = s.facts.copy(extraChipIds = if (chipId in ids) ids - chipId else ids + chipId))
    }

    /** "+" beim Ausfuellen: neuen Chip anlegen und gleich auswaehlen. */
    fun addExtra(name: String) {
        viewModelScope.launch {
            val id = chipRepository.add(name) ?: return@launch
            _state.update { s -> s.copy(facts = s.facts.copy(extraChipIds = s.facts.extraChipIds + id)) }
        }
    }

    /** "Ueberspringen" im Faktenteil: alles dort wieder leeren. */
    fun clearFacts() = _state.update { it.copy(facts = CheckInFactAnswers()) }

    fun next() = _state.update { if (it.isLast) it else it.copy(index = it.index + 1) }

    fun previous() = _state.update { if (it.isFirst) it else it.copy(index = it.index - 1) }

    /** "Fertig": die gezeigten Teile in den heutigen Check-in speichern. Beenden mit X speichert nichts. */
    fun finish(onDone: () -> Unit) {
        if (saving) return
        saving = true
        viewModelScope.launch {
            repository.saveToday(
                answers = _state.value.answers.takeIf { 1 in parts },
                facts = _state.value.facts.takeIf { 2 in parts }
            )
            EarlyWarningStore.evaluate(getApplication())
            WatchSync.update(getApplication<Application>())
            onDone()
        }
    }
}
