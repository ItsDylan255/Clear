package com.example.clear.ui.checkin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.ContentType
import com.example.clear.data.IdeaLevel
import com.example.clear.data.PersonalIdea
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.model.CheckInCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IdeaEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val title: String = "",
    val category: CheckInCategory = CheckInCategory.Energy,
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

    private val ideaId: Long = savedStateHandle.get<Long>("ideaId") ?: -1L
    private var createdAt: Long = 0L
    private var saving = false

    private val _state = MutableStateFlow(
        IdeaEditorState(
            isLoading = ideaId != -1L,
            isNew = ideaId == -1L,
            category = CheckInCategory.fromId(savedStateHandle.get<String>("categoryId"))
                ?: CheckInCategory.Energy
        )
    )
    val state: StateFlow<IdeaEditorState> = _state.asStateFlow()

    init {
        if (ideaId != -1L) {
            viewModelScope.launch {
                val idea = repository.get(ideaId)
                if (idea != null) {
                    createdAt = idea.createdAt
                    _state.update {
                        it.copy(
                            isLoading = false,
                            title = idea.title,
                            category = CheckInCategory.fromId(idea.categoryId)
                                ?: CheckInCategory.Energy,
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
    fun onCategoryChange(value: CheckInCategory) = _state.update { it.copy(category = value) }

    /** Speichert. Aendert man die Kategorie, wandert die Idee automatisch dorthin. */
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
            if (s.isNew) {
                repository.add(
                    PersonalIdea(
                        title = s.title.trim(),
                        categoryId = s.category.id, contentType = s.contentType,
                        createdAt = now, updatedAt = now,
                        textNormal = s.textNormal.trim(), textSinking = s.textSinking.trim(),
                        textSevere = s.textSevere.trim()
                    )
                )
            } else {
                repository.update(
                    PersonalIdea(
                        id = ideaId, title = s.title.trim(),
                        categoryId = s.category.id, contentType = s.contentType,
                        createdAt = createdAt, updatedAt = now,
                        textNormal = s.textNormal.trim(), textSinking = s.textSinking.trim(),
                        textSevere = s.textSevere.trim()
                    )
                )
            }
            onDone()
        }
    }
}
