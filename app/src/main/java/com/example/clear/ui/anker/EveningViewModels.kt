package com.example.clear.ui.anker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.EveningEntry
import com.example.clear.data.EveningType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

// ---------- Archiv ----------

/** Durchsuchbares Archiv. Suche und Filter wirken sofort. null = wird noch geladen. */
class EveningArchiveViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).eveningDao()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    /** null = alle, sonst EveningType.SUCCESS oder EveningType.GOOD. */
    private val _filter = MutableStateFlow<String?>(null)
    val filter: StateFlow<String?> = _filter.asStateFlow()

    fun onQueryChange(value: String) { _query.value = value }
    fun onFilterChange(value: String?) { _filter.value = value }

    val entries: StateFlow<List<EveningEntry>?> =
        combine(dao.observeAll(), _query, _filter) { all, query, filter ->
            val needle = query.trim()
            all.filter { entry ->
                (filter == null || entry.type == filter) &&
                    (needle.isEmpty() ||
                        entry.text.contains(needle, ignoreCase = true) ||
                        entry.reason?.contains(needle, ignoreCase = true) == true)
            }
        }
            .flowOn(Dispatchers.Default)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(entry: EveningEntry) {
        viewModelScope.launch { dao.delete(entry) }
    }
}

// ---------- Neue Eintraege (Abend-Eingabe) ----------

data class EveningDraft(
    val success: String = "",
    val goodTexts: List<String> = List(3) { "" },
    val goodReasons: List<String> = List(3) { "" }
)

class EveningEntryViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).eveningDao()
    private var saving = false
    private var loaded = false

    // Beim Bearbeiten von heute: die Eintraege hinter den Feldern (null = Feld ist neu)
    private var successEntry: EveningEntry? = null
    private var goodEntries: List<EveningEntry?> = List(3) { null }

    private val _draft = MutableStateFlow(EveningDraft())
    val draft: StateFlow<EveningDraft> = _draft.asStateFlow()

    /**
     * Fuellt die Felder mit den heutigen Eintraegen (Check-in Teil 3). Beim Speichern werden
     * diese Eintraege dann geaendert statt neue angelegt. Weitere Eintraege von heute bleiben.
     */
    fun loadToday() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val day = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
            val today = dao.getForDay(day)
            successEntry = today.firstOrNull { it.type == EveningType.SUCCESS }
            val goods = today.filter { it.type == EveningType.GOOD }.take(3)
            goodEntries = List(3) { goods.getOrNull(it) }
            _draft.value = EveningDraft(
                success = successEntry?.text.orEmpty(),
                goodTexts = List(3) { goodEntries[it]?.text.orEmpty() },
                goodReasons = List(3) { goodEntries[it]?.reason.orEmpty() }
            )
        }
    }

    fun onSuccessChange(value: String) = _draft.update { it.copy(success = value) }

    fun onGoodTextChange(index: Int, value: String) = _draft.update {
        it.copy(goodTexts = it.goodTexts.toMutableList().also { list -> list[index] = value })
    }

    fun onGoodReasonChange(index: Int, value: String) = _draft.update {
        it.copy(goodReasons = it.goodReasons.toMutableList().also { list -> list[index] = value })
    }

    /**
     * Speichert nur, was ausgefuellt ist. Ganz leer: es wird nichts gespeichert.
     * Ein vorausgefuelltes Feld, das jetzt leer ist, loescht seinen Eintrag.
     */
    fun save(onDone: () -> Unit) {
        if (saving) return
        saving = true
        val d = _draft.value
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val day = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
            write(successEntry, EveningEntry(type = EveningType.SUCCESS, text = d.success.trim(), day = day, createdAt = now))
            d.goodTexts.forEachIndexed { index, text ->
                write(
                    goodEntries[index],
                    EveningEntry(
                        type = EveningType.GOOD, text = text.trim(),
                        reason = d.goodReasons[index].trim().ifBlank { null },
                        day = day, createdAt = now
                    )
                )
            }
            onDone()
        }
    }

    private suspend fun write(existing: EveningEntry?, entry: EveningEntry) {
        when {
            existing == null && entry.text.isBlank() -> Unit
            existing == null -> dao.insert(entry)
            entry.text.isBlank() -> dao.delete(existing)
            else -> dao.update(existing.copy(text = entry.text, reason = entry.reason))
        }
    }
}

// ---------- Eintrag bearbeiten ----------

data class EveningEditState(
    val isLoading: Boolean = true,
    val type: String = EveningType.GOOD,
    val text: String = "",
    val reason: String = "",
    val textError: Boolean = false
)

class EveningEditViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).eveningDao()
    private val entryId: Long = handle.get<Long>("entryId") ?: -1L
    private var original: EveningEntry? = null
    private var saving = false

    private val _state = MutableStateFlow(EveningEditState())
    val state: StateFlow<EveningEditState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val entry = dao.getById(entryId)
            original = entry
            _state.value = if (entry == null) EveningEditState(isLoading = false)
            else EveningEditState(
                isLoading = false, type = entry.type, text = entry.text, reason = entry.reason.orEmpty()
            )
        }
    }

    fun onTextChange(value: String) = _state.update { it.copy(text = value, textError = false) }
    fun onReasonChange(value: String) = _state.update { it.copy(reason = value) }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        val entry = original
        if (entry == null) {
            onDone()
            return
        }
        if (s.text.isBlank()) {
            _state.update { it.copy(textError = true) }
            return
        }
        if (saving) return
        saving = true
        viewModelScope.launch {
            dao.update(
                entry.copy(
                    text = s.text.trim(),
                    reason = if (entry.type == EveningType.GOOD) s.reason.trim().ifBlank { null } else null
                )
            )
            onDone()
        }
    }
}
