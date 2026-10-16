package com.example.clear.ui.akut

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.AnchorImage
import com.example.clear.data.ClearDatabase
import com.example.clear.data.Quote
import com.example.clear.model.EmotionTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Persoenliche Inhalte, die zur gewaehlten Situation passen. null = nichts vorhanden. */
data class SituationContent(
    val quote: Quote? = null,
    val image: AnchorImage? = null
)

/**
 * Waehlt pro Situation je ein Zitat und Bild aus:
 * 1. zufaellig unter den Eintraegen mit passender Emotion,
 * 2. sonst zufaellig unter den Eintraegen ohne Emotion (gelten fuer alle),
 * 3. sonst nichts (der Bereich bleibt weg).
 * "Ich weiß nicht genau" nimmt zufaellig aus allem. Musik laeuft ueber die Seite "Musik hören".
 */
class SituationContentViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val situationId: String = handle.get<String>("id") ?: ""

    private val _state = MutableStateFlow(SituationContent())
    val state: StateFlow<SituationContent> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val db = ClearDatabase.getInstance(getApplication())
            _state.value = SituationContent(
                quote = pick(db.quoteDao().observeAll().first()) { it.emotionTags },
                image = pick(db.imageDao().observeAll().first()) { it.emotionTags }
            )
        }
    }

    private fun <T> pick(items: List<T>, tagsOf: (T) -> String): T? {
        if (situationId == "unknown") return items.randomOrNull()
        val matching = items.filter { item ->
            EmotionTag.parse(tagsOf(item)).any { tag -> tag.id == situationId }
        }
        if (matching.isNotEmpty()) return matching.random()
        return items.filter { item -> EmotionTag.parse(tagsOf(item)).isEmpty() }.randomOrNull()
    }
}
