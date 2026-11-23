package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.ContentType
import com.example.clear.data.IdeaLevel
import com.example.clear.data.PersonalIdea
import com.example.clear.data.IdeaNeeds
import com.example.clear.data.IdeaTopic
import com.example.clear.data.IdeaTopicRepository
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.model.CheckInCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IdeaEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val title: String = "",
    // null = "Ohne Thema"
    val topicId: Long? = null,
    val needs: Set<CheckInCategory> = emptySet(),
    val contentType: ContentType = ContentType.Exercise,
    // Je ein Text fuer Normal, Sinkend und Stark beeintraechtigt. Alle drei sind Pflicht.
    val textNormal: String = "",
    val textSinking: String = "",
    val textSevere: String = "",
    val titleError: Boolean = false,
    val normalError: Boolean = false,
    val sinkingError: Boolean = false,
    val severeError: Boolean = false
)

/** Erstellen und Bearbeiten einer Idee. ideaId = -1 bedeutet: neue Idee. */
class IdeaEditorViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository =
        PersonalIdeaRepository(ClearDatabase.getInstance(application).personalIdeaDao())

    private val topicRepository =
        IdeaTopicRepository(ClearDatabase.getInstance(application).ideaTopicDao())

    private val ideaId: Long = savedStateHandle.get<Long>("ideaId") ?: -1L
    private var original: PersonalIdea? = null
    private var saving = false

    /** Alle eigenen Themen fuer die Auswahl. */
    val topics: StateFlow<List<IdeaTopic>> = topicRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _state = MutableStateFlow(
        IdeaEditorState(
            isLoading = ideaId != -1L,
            isNew = ideaId == -1L,
            // Neue Idee aus einem Thema heraus: dieses Thema ist vorausgewaehlt
            topicId = savedStateHandle.get<Long>("topicId")?.takeIf { it > 0L }
        )
    )
    val state: StateFlow<IdeaEditorState> = _state.asStateFlow()

    init {
        if (ideaId != -1L) {
            viewModelScope.launch {
                val idea = repository.get(ideaId)
                if (idea != null) {
                    original = idea
                    _state.update {
                        it.copy(
                            isLoading = false,
                            title = idea.title,
                            topicId = idea.topicId,
                            needs = idea.needSet,
                            contentType = idea.contentType,
                            textNormal = idea.textNormal,
                            textSinking = idea.textSinking,
                            textSevere = idea.textSevere
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, isNew = true) }
                }
            }
        }
    }

    fun onTitleChange(value: String) = _state.update { it.copy(title = value, titleError = false) }
    fun onNormalChange(value: String) = _state.update { it.copy(textNormal = value, normalError = false) }
    fun onSinkingChange(value: String) = _state.update { it.copy(textSinking = value, sinkingError = false) }
    fun onSevereChange(value: String) = _state.update { it.copy(textSevere = value, severeError = false) }
    fun onTopicChange(value: Long?) = _state.update { it.copy(topicId = value) }

    fun toggleNeed(value: CheckInCategory) = _state.update {
        it.copy(needs = if (value in it.needs) it.needs - value else it.needs + value)
    }

    /** "+" bei den Themen: neues Thema anlegen und gleich auswaehlen. */
    fun addTopic(name: String) {
        viewModelScope.launch {
            val id = topicRepository.add(name) ?: return@launch
            _state.update { it.copy(topicId = id) }
        }
    }

    /** Speichert. Aendert man das Thema, wandert die Idee automatisch dorthin. */
    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.title.isBlank() || s.textNormal.isBlank() || s.textSinking.isBlank() || s.textSevere.isBlank()) {
            _state.update {
                it.copy(
                    titleError = s.title.isBlank(),
                    normalError = s.textNormal.isBlank(),
                    sinkingError = s.textSinking.isBlank(),
                    severeError = s.textSevere.isBlank()
                )
            }
            return
        }
        if (saving) return
        saving = true
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val old = original
            if (s.isNew || old == null) {
                repository.add(
                    PersonalIdea(
                        title = s.title.trim(), contentType = s.contentType,
                        createdAt = now, updatedAt = now,
                        textNormal = s.textNormal.trim(), textSinking = s.textSinking.trim(),
                        textSevere = s.textSevere.trim(),
                        topicId = s.topicId, needs = IdeaNeeds.serialize(s.needs)
                    )
                )
            } else {
                repository.update(
                    old.copy(
                        title = s.title.trim(), contentType = s.contentType, updatedAt = now,
                        textNormal = s.textNormal.trim(), textSinking = s.textSinking.trim(),
                        textSevere = s.textSevere.trim(),
                        topicId = s.topicId, needs = IdeaNeeds.serialize(s.needs)
                    )
                )
            }
            onDone()
        }
    }
}
