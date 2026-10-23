package com.example.clear.ui.anker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.WarningSign
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Ein Listeneintrag fuer die Anzeige. */
data class TextItem(val id: Long, val text: String)

/** Meine Warnzeichen (Notfallkarte). items: null = wird noch geladen. */
class WarningSignsViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = ClearDatabase.getInstance(app).warningSignDao()

    val items: StateFlow<List<TextItem>?> = dao.observeAll()
        .map { list -> list.map { TextItem(it.id, it.text) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun add(text: String) {
        viewModelScope.launch {
            dao.insert(WarningSign(text = text.trim(), createdAt = System.currentTimeMillis()))
        }
    }

    fun update(id: Long, text: String) {
        viewModelScope.launch { dao.getById(id)?.let { dao.update(it.copy(text = text.trim())) } }
    }

    fun delete(id: Long) {
        viewModelScope.launch { dao.deleteById(id) }
    }
}
