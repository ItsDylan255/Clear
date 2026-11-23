package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.ExerciseSession
import com.example.clear.data.IdeaLevel
import com.example.clear.data.PersonalIdea
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.reminder.DoNotDisturb
import com.example.clear.reminder.ExerciseNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** `runningVariant` = Fassung des laufenden Durchlaufs (null = laeuft nicht oder unbekannt). */
data class IdeaDetailState(
    val isLoading: Boolean = true,
    val idea: PersonalIdea? = null,
    val runningVariant: IdeaLevel? = null
)

/** Beschreibung einer Idee. Die ID kommt aus der Navigation ("ideaId"). */
class IdeaDetailViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val ideaId: Long = handle.get<Long>("ideaId") ?: -1L

    private val _state = MutableStateFlow(IdeaDetailState())
    val state: StateFlow<IdeaDetailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val db = ClearDatabase.getInstance(app)
            val repository = PersonalIdeaRepository(db.personalIdeaDao())
            val open = db.exerciseSessionDao().getOpen(ActiveIdeas.key(ideaId))
            _state.value = IdeaDetailState(
                isLoading = false,
                idea = repository.get(ideaId),
                runningVariant = IdeaLevel.entries.firstOrNull { it.name == open?.variant }
            )
        }
    }

    /** Bricht die laufende Idee ohne Bewertung ab. */
    fun cancel() {
        viewModelScope.launch { IdeaSessions.cancel(getApplication(), ideaId) }
    }
}

data class IdeaRateState(
    val isLoading: Boolean = true,
    val idea: PersonalIdea? = null,
    val mood: Int? = null,
    /** Nach dem Beenden: Stimmung davor und danach. */
    val result: Pair<Int, Int>? = null,
    val average: Double? = null
)

/**
 * Bewertung "Wie ist deine Stimmung?". mode = "before": startet die Idee. mode = "after": beendet sie.
 * ideaId, mode und beim Starten die gewaehlte Fassung (variant) kommen aus der Navigation.
 */
class IdeaRateViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val ideaId: Long = handle.get<Long>("ideaId") ?: -1L
    val isBefore: Boolean = handle.get<String>("mode") != "after"
    private val variant: IdeaLevel? = IdeaLevel.entries.firstOrNull { it.name == handle.get<String>("variant") }
    private val db = ClearDatabase.getInstance(app)
    private var saving = false

    private val _state = MutableStateFlow(IdeaRateState())
    val state: StateFlow<IdeaRateState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val repository = PersonalIdeaRepository(db.personalIdeaDao())
            _state.update { it.copy(isLoading = false, idea = repository.get(ideaId)) }
        }
    }

    /** Nochmal dieselbe Zahl antippen = Auswahl entfernen. */
    fun select(value: Int) = _state.update { it.copy(mood = if (it.mood == value) null else value) }

    /** Bestaetigen. Beim Starten ruft es onStarted auf, beim Beenden zeigt es das Ergebnis. */
    fun confirm(onStarted: () -> Unit) {
        val mood = _state.value.mood ?: return
        val idea = _state.value.idea ?: return
        if (saving) return
        saving = true
        val app = getApplication<Application>()
        val key = ActiveIdeas.key(ideaId)
        val dao = db.exerciseSessionDao()
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (isBefore) {
                if (!ActiveIdeas.isRunning(ideaId)) {
                    dao.insert(
                        ExerciseSession(exerciseId = key, startedAt = now, moodBefore = mood, variant = variant?.name)
                    )
                    ActiveIdeas.start(app, ideaId, now)
                    ExerciseNotifier.show(app, ideaId, idea.title)
                    DoNotDisturb.enter(app)   // nur, wenn in den Einstellungen eingeschaltet
                }
                onStarted()
            } else {
                val result = IdeaSessions.end(app, ideaId, mood)
                val completed = dao.observeCompleted(key).first()
                val average = if (completed.isEmpty()) null
                else completed.map { (it.moodAfter ?: it.moodBefore) - it.moodBefore }.average()
                _state.update { it.copy(result = result, average = average) }
            }
        }
    }
}
