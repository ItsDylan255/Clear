package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clear.model.Situations
import com.example.clear.ui.components.ClearDetailScreen

/**
 * Inhalte verwalten, Schritt 2: fuer welche Emotion? "Allgemein" = Eintraege ohne Emotion.
 * Neue Eintraege bekommen anschliessend genau diese Emotion.
 */
@Composable
fun AnkerEmotionPickScreen(
    type: AnkerType,
    onBack: () -> Unit,
    onPick: (String) -> Unit
) {
    val general = Situations.all.firstOrNull { it.id == "unknown" }
    val others = Situations.all.filter { it.id != "unknown" }

    ClearDetailScreen(title = type.title, onBack = onBack) {
        Text(
            "Für welche Emotion?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (general != null) {
            EmotionTile(general, { onPick(general.id) }, Modifier.fillMaxWidth())
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            others.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { situation ->
                        EmotionTile(situation, { onPick(situation.id) }, Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
