package com.example.clear.ui.anker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.AnchorImage
import com.example.clear.data.ClearDatabase
import com.example.clear.data.Letter
import com.example.clear.data.MusicTrack
import com.example.clear.data.Quote
import com.example.clear.model.EmotionTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Persoenliche Inhalte, die zu einer Emotion passen. Leere Listen = Typ wird nicht gezeigt. */
data class AnkerContent(
    val quotes: List<Quote> = emptyList(),
    val images: List<AnchorImage> = emptyList(),
    val tracks: List<MusicTrack> = emptyList(),
    val letters: List<Letter> = emptyList()
) {
    val isEmpty: Boolean
        get() = quotes.isEmpty() && images.isEmpty() && tracks.isEmpty() && letters.isEmpty()
}

/**
 * Sammelt pro Emotion nur die Zitate, Bilder und Musik mit genau dieser Emotion (gemischt).
 * Eintraege ohne Emotion erscheinen nur bei "Allgemein" (id "unknown"), das alles zeigt.
 * Das gilt auch fuer Briefe.
 * Die Daten werden einmal geladen, die Ansicht aendert nichts.
 */
class EmotionAnkerViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val situationId: String = handle.get<String>("id") ?: ""

    /** null = wird noch geladen. */
    private val _state = MutableStateFlow<AnkerContent?>(null)
    val state: StateFlow<AnkerContent?> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val db = ClearDatabase.getInstance(getApplication())
            _state.value = AnkerContent(
                quotes = select(db.quoteDao().observeAll().first()) { it.emotionTags },
                images = select(db.imageDao().observeAll().first()) { it.emotionTags },
                tracks = select(db.musicDao().observeAll().first()) { it.emotionTags },
                letters = select(db.letterDao().observeAll().first()) { it.emotionTags }
            )
        }
    }

    private fun <T> select(items: List<T>, tagsOf: (T) -> String): List<T> {
        if (situationId == "unknown") return items.shuffled()
        val matching = items.filter { item ->
            EmotionTag.parse(tagsOf(item)).any { tag -> tag.id == situationId }
        }
        return matching.shuffled()
    }
}
