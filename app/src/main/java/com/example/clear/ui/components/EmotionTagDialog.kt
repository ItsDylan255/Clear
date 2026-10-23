package com.example.clear.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.clear.model.EmotionTag
import com.example.clear.ui.theme.ClearSpacing

/** Dialog zum Zuordnen von Emotionen. Ohne Auswahl passt der Eintrag zu jeder Situation. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmotionTagDialog(
    initial: Set<EmotionTag>,
    onSave: (Set<EmotionTag>) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wann passt das zu dir?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                Text(
                    "Wähle Emotionen aus. Ohne Auswahl passt der Eintrag zu jeder Situation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                    EmotionTag.entries.forEach { tag ->
                        FilterChip(
                            selected = tag in selected,
                            onClick = {
                                selected = if (tag in selected) selected - tag else selected + tag
                            },
                            label = { Text(tag.title) }
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(selected) }) { Text("Speichern") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}

/** "Angst, Panik" oder null, wenn nichts zugeordnet ist. */
fun emotionTagsLabel(stored: String): String? {
    val tags = EmotionTag.parse(stored)
    return if (tags.isEmpty()) null else EmotionTag.entries.filter { it in tags }.joinToString(", ") { it.title }
}
