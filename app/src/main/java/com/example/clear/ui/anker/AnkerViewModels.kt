package com.example.clear.ui.anker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.EmergencyCard
import com.example.clear.data.EmergencyCardRepository
import com.example.clear.data.Letter
import com.example.clear.data.LetterRepository
import com.example.clear.data.Quote
import com.example.clear.data.QuoteRepository
import com.example.clear.model.EmotionTag
import com.example.clear.model.Situations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------- Zitate ----------

class QuotesViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = QuoteRepository(ClearDatabase.getInstance(app).quoteDao())
    private val emotion: String? = handle.get<String>("emotion")

    /** Name der gewaehlten Emotion fuer die Ueberschrift. */
    val emotionName: String? = Situations.fromId(emotion)?.name

    /** Nur die Zitate der gewaehlten Emotion. null = wird noch geladen. */
    val quotes: StateFlow<List<Quote>?> = repo.observeAll()
        .map { list -> list.filter { EmotionTag.matches(it.emotionTags, emotion) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(quote: Quote) {
        viewModelScope.launch { repo.delete(quote) }
    }
}

data class QuoteEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val text: String = "",
    val author: String = "",
    val textError: Boolean = false
)

class QuoteEditorViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = QuoteRepository(ClearDatabase.getInstance(app).quoteDao())
    private val quoteId: Long = handle.get<Long>("quoteId") ?: -1L
    private val emotion: String? = handle.get<String>("emotion")
    private var existingTags = ""
    private var createdAt = 0L
    private var saving = false

    private val _state = MutableStateFlow(
        QuoteEditorState(isLoading = quoteId != -1L, isNew = quoteId == -1L)
    )
    val state: StateFlow<QuoteEditorState> = _state.asStateFlow()

    init {
        if (quoteId != -1L) {
            viewModelScope.launch {
                val q = repo.get(quoteId)
                if (q != null) {
                    createdAt = q.createdAt
                    existingTags = q.emotionTags
                    _state.update {
                        it.copy(isLoading = false, text = q.text, author = q.author.orEmpty())
                    }
                } else {
                    _state.update { it.copy(isLoading = false, isNew = true) }
                }
            }
        }
    }

    fun onTextChange(v: String) = _state.update { it.copy(text = v, textError = false) }
    fun onAuthorChange(v: String) = _state.update { it.copy(author = v) }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.text.isBlank()) {
            _state.update { it.copy(textError = true) }
            return
        }
        if (saving) return
        saving = true
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val author = s.author.trim().ifBlank { null }
            // Neue Eintraege bekommen die gewaehlte Emotion, bestehende behalten ihre
            val tags = if (s.isNew) EmotionTag.forNewEntry(emotion) else existingTags
            if (s.isNew) {
                repo.add(
                    Quote(text = s.text.trim(), author = author, createdAt = now,
                        updatedAt = now, emotionTags = tags)
                )
            } else {
                repo.update(
                    Quote(id = quoteId, text = s.text.trim(), author = author,
                        createdAt = createdAt, updatedAt = now, emotionTags = tags)
                )
            }
            onDone()
        }
    }
}

// ---------- Briefe ----------

class LettersViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = LetterRepository(ClearDatabase.getInstance(app).letterDao())
    private val emotion: String? = handle.get<String>("emotion")

    /** Name der gewaehlten Emotion fuer die Ueberschrift. */
    val emotionName: String? = Situations.fromId(emotion)?.name

    /** Nur die Briefe der gewaehlten Emotion. null = wird noch geladen. */
    val letters: StateFlow<List<Letter>?> = repo.observeAll()
        .map { list -> list.filter { EmotionTag.matches(it.emotionTags, emotion) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

data class LetterReaderState(val isLoading: Boolean = true, val letter: Letter? = null)

class LetterReaderViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = LetterRepository(ClearDatabase.getInstance(app).letterDao())
    private val letterId: Long = handle.get<Long>("letterId") ?: -1L

    val state: StateFlow<LetterReaderState> = repo.observe(letterId)
        .map { LetterReaderState(isLoading = false, letter = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LetterReaderState())

    fun delete(letter: Letter, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.delete(letter)
            onDone()
        }
    }
}

data class LetterEditorState(
    val isLoading: Boolean = true,
    val isNew: Boolean = true,
    val title: String = "",
    val text: String = "",
    val titleError: Boolean = false,
    val textError: Boolean = false
)

class LetterEditorViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val repo = LetterRepository(ClearDatabase.getInstance(app).letterDao())
    private val letterId: Long = handle.get<Long>("letterId") ?: -1L
    private val emotion: String? = handle.get<String>("emotion")
    private var existingTags = ""
    private var createdAt = 0L
    private var saving = false

    private val _state = MutableStateFlow(
        LetterEditorState(isLoading = letterId != -1L, isNew = letterId == -1L)
    )
    val state: StateFlow<LetterEditorState> = _state.asStateFlow()

    init {
        if (letterId != -1L) {
            viewModelScope.launch {
                val l = repo.get(letterId)
                if (l != null) {
                    createdAt = l.createdAt
                    existingTags = l.emotionTags
                    _state.update { it.copy(isLoading = false, title = l.title, text = l.text) }
                } else {
                    _state.update { it.copy(isLoading = false, isNew = true) }
                }
            }
        }
    }

    fun onTitleChange(v: String) = _state.update { it.copy(title = v, titleError = false) }
    fun onTextChange(v: String) = _state.update { it.copy(text = v, textError = false) }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.title.isBlank() || s.text.isBlank()) {
            _state.update { it.copy(titleError = s.title.isBlank(), textError = s.text.isBlank()) }
            return
        }
        if (saving) return
        saving = true
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            // Neue Briefe bekommen die gewaehlte Emotion, bestehende behalten ihre
            val tags = if (s.isNew) EmotionTag.forNewEntry(emotion) else existingTags
            if (s.isNew) {
                repo.add(
                    Letter(title = s.title.trim(), text = s.text.trim(), createdAt = now,
                        updatedAt = now, emotionTags = tags)
                )
            } else {
                repo.update(
                    Letter(id = letterId, title = s.title.trim(), text = s.text.trim(),
                        createdAt = createdAt, updatedAt = now, emotionTags = tags)
                )
            }
            onDone()
        }
    }
}

// ---------- Notfallkarte ----------

/** Zum Anzeigen der persoenlichen Angaben. Keine Zeile in der Datenbank = leere Karte. */
class EmergencyCardViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = EmergencyCardRepository(ClearDatabase.getInstance(app).emergencyCardDao())

    val card: StateFlow<EmergencyCard?> = repo.observe()
        .map { it ?: EmergencyCard() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

data class EmergencyEditState(
    val isLoading: Boolean = true,
    val name: String = "",
    val phone: String = "",
    val note: String = "",
    val plan: String = ""
)

class EmergencyEditViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = EmergencyCardRepository(ClearDatabase.getInstance(app).emergencyCardDao())
    private val _state = MutableStateFlow(EmergencyEditState())
    val state: StateFlow<EmergencyEditState> = _state.asStateFlow()
    private var saving = false

    init {
        viewModelScope.launch {
            val c = repo.get() ?: EmergencyCard()
            _state.value = EmergencyEditState(
                isLoading = false, name = c.contactName, phone = c.contactPhone,
                note = c.contactNote, plan = c.safetyPlan
            )
        }
    }

    fun onNameChange(v: String) = _state.update { it.copy(name = v) }
    fun onPhoneChange(v: String) = _state.update { it.copy(phone = v) }
    fun onNoteChange(v: String) = _state.update { it.copy(note = v) }
    fun onPlanChange(v: String) = _state.update { it.copy(plan = v) }

    fun save(onDone: () -> Unit) {
        if (saving) return
        saving = true
        val s = _state.value
        viewModelScope.launch {
            repo.save(
                EmergencyCard(
                    id = 1, contactName = s.name.trim(), contactPhone = s.phone.trim(),
                    contactNote = s.note.trim(), safetyPlan = s.plan.trim()
                )
            )
            onDone()
        }
    }
}
