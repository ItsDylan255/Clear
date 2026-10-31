package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.CheckInRepository
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
    val answers: Map<CheckInCategory, Int> = emptyMap()
) {
    val categories: List<CheckInCategory> get() = CheckInCategory.entries
    val current: CheckInCategory get() = categories[index]
    val selected: Int? get() = answers[current]
    val isFirst: Boolean get() = index == 0
    val isLast: Boolean get() = index == categories.lastIndex
    val progress: Float get() = (index + 1f) / categories.size
}

class CheckInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        CheckInRepository(ClearDatabase.getInstance(application).checkInDao())

    private val _state = MutableStateFlow(CheckInUiState())
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()

    private var saving = false

    /** Zahl antippen. Nochmal dieselbe Zahl antippen = Antwort wieder entfernen. */
    fun select(value: Int) = _state.update { s ->
        val newAnswers =
            if (s.answers[s.current] == value) s.answers - s.current
            else s.answers + (s.current to value)
        s.copy(answers = newAnswers)
    }

    fun next() = _state.update { if (it.isLast) it else it.copy(index = it.index + 1) }

    fun previous() = _state.update { if (it.isFirst) it else it.copy(index = it.index - 1) }

    /** "Fertig": speichern und danach zurueck. Beenden mit X speichert nichts. */
    fun finish(onDone: () -> Unit) {
        if (saving) return
        saving = true
        viewModelScope.launch {
            repository.save(_state.value.answers)
            WatchSync.update(getApplication<Application>())
            onDone()
        }
    }
}
