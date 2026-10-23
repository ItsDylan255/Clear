package com.example.clear.ui.akut

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.EveningEntry
import com.example.clear.data.ThoughtRecord
import com.example.clear.model.HelpQuestions
import com.example.clear.model.ThinkingTrap
import com.example.clear.model.ThoughtEmotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------- Liste ----------

class ThoughtListViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).thoughtRecordDao()

    /** Neueste zuerst. null = wird noch geladen. */
    val records: StateFlow<List<ThoughtRecord>?> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(record: ThoughtRecord) {
        viewModelScope.launch { dao.delete(record) }
    }
}

// ---------- Einzelnes Protokoll ----------

class ThoughtDetailViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).thoughtRecordDao()
    private val recordId: Long = handle.get<Long>("recordId") ?: -1L

    val record: StateFlow<ThoughtRecord?> = dao.observeById(recordId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(record: ThoughtRecord, onDone: () -> Unit) {
        viewModelScope.launch {
            dao.delete(record)
            onDone()
        }
    }
}

// ---------- Neues Protokoll ----------

const val THOUGHT_STEPS = 6

data class ThoughtDraft(
    val step: Int = 0,
    val situation: String = "",
    val thought: String = "",
    val emotion: ThoughtEmotion? = null,
    val before: Int = 5,
    val traps: Set<ThinkingTrap> = emptySet(),
    val helpQuestion: Int? = null,
    val newView: String = "",
    /** null = noch nicht bewegt, gilt dann wie "davor". */
    val after: Int? = null,
    /** Nach dem Speichern: Staerke davor und danach. */
    val result: Pair<Int, Int>? = null
) {
    val isFirst: Boolean get() = step == 0
    val isLast: Boolean get() = step == THOUGHT_STEPS - 1
    val progress: Float get() = (step + 1f) / THOUGHT_STEPS
    val afterValue: Int get() = after ?: before

    /** Pflichtangaben je Schritt. Die Denkfallen (Schritt 4) sind freiwillig. */
    val canContinue: Boolean
        get() = when (step) {
            0 -> situation.isNotBlank()
            1 -> thought.isNotBlank()
            2 -> emotion != null
            3 -> true
            4 -> helpQuestion != null && newView.isNotBlank()
            else -> true
        }
}

class ThoughtFlowViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ClearDatabase.getInstance(app)
    private var saving = false

    private val _state = MutableStateFlow(ThoughtDraft())
    val state: StateFlow<ThoughtDraft> = _state.asStateFlow()

    /** Die letzten Eintraege aus "Erfolge und gute Dinge" fuer die Frage "Was spricht dagegen?". */
    val recentEntries: StateFlow<List<EveningEntry>> = db.eveningDao().observeAll()
        .map { it.take(8) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSituation(v: String) = _state.update { it.copy(situation = v) }
    fun onThought(v: String) = _state.update { it.copy(thought = v) }
    fun onEmotion(v: ThoughtEmotion) = _state.update { it.copy(emotion = v) }
    fun onBefore(v: Int) = _state.update { it.copy(before = v) }
    fun onAfter(v: Int) = _state.update { it.copy(after = v) }
    fun onHelpQuestion(v: Int) = _state.update { it.copy(helpQuestion = v) }
    fun onNewView(v: String) = _state.update { it.copy(newView = v) }
    fun toggleTrap(trap: ThinkingTrap) = _state.update {
        it.copy(traps = if (trap in it.traps) it.traps - trap else it.traps + trap)
    }

    fun next() = _state.update { if (it.isLast || !it.canContinue) it else it.copy(step = it.step + 1) }
    fun previous() = _state.update { if (it.isFirst) it else it.copy(step = it.step - 1) }

    /** Speichert das Protokoll und zeigt "Von 8 auf 5". */
    fun finish() {
        val s = _state.value
        val emotion = s.emotion ?: return
        val question = s.helpQuestion ?: return
        if (saving) return
        saving = true
        viewModelScope.launch {
            db.thoughtRecordDao().insert(
                ThoughtRecord(
                    createdAt = System.currentTimeMillis(),
                    situation = s.situation.trim(),
                    thought = s.thought.trim(),
                    emotion = emotion.id,
                    intensityBefore = s.before,
                    traps = ThinkingTrap.serialize(s.traps),
                    helpQuestion = question,
                    newView = s.newView.trim(),
                    intensityAfter = s.afterValue
                )
            )
            _state.update { it.copy(result = s.before to s.afterValue) }
        }
    }

    val helpQuestions: List<String> get() = HelpQuestions.all
}
