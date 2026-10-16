package com.example.clear.ui.anker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.MusicTrack
import com.example.clear.model.EmotionTag
import com.example.clear.model.MoodGoal
import com.example.clear.ui.akut.ClearWarningSoft
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.EmotionTagDialog
import com.example.clear.ui.components.MoodGoalDialog
import com.example.clear.ui.components.moodGoalsLabel
import com.example.clear.ui.components.EmptyHintCard
import com.example.clear.ui.components.emotionTagsLabel
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft

@Composable
fun MusicScreen(onBack: () -> Unit, viewModel: MusicViewModel = viewModel()) {
    val tracks by viewModel.tracks.collectAsState()
    val playback by viewModel.playback.collectAsState()
    val list = tracks
    var toDelete by remember { mutableStateOf<MusicTrack?>(null) }
    var tagTrack by remember { mutableStateOf<MusicTrack?>(null) }
    var goalTrack by remember { mutableStateOf<MusicTrack?>(null) }

    // Dateiauswahl des Systems (Storage Access Framework)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.addTrack(uri)
    }

    ClearDetailScreen(title = "Meine Musik", onBack = onBack) {
        Text(
            "Musik, die dich beruhigt.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        playback.message?.let { text ->
            ClearCard(containerColor = ClearWarningSoft) {
                Text(text, style = MaterialTheme.typography.bodyLarge)
            }
        }
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> EmptyHintCard()
            else -> list.forEach { track ->
                val isCurrent = playback.trackId == track.id
                val isPlaying = isCurrent && playback.isPlaying
                ClearCard(containerColor = if (isCurrent) ClearSurfaceSoft else null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                    ) {
                        IconButton(onClick = { viewModel.togglePlay(track) }) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription =
                                    if (isPlaying) "Pause: ${track.title}" else "Abspielen: ${track.title}"
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, style = MaterialTheme.typography.titleMedium)
                            TextButton(onClick = { tagTrack = track }) {
                                Text(
                                    emotionTagsLabel(track.emotionTags) ?: "Emotionen zuordnen",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            TextButton(onClick = { goalTrack = track }) {
                                Text(
                                    moodGoalsLabel(track.moodGoals)?.let { "Stimmung verändern: $it" }
                                        ?: "Stimmungsziel zuordnen",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        IconButton(onClick = { toDelete = track }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Löschen: ${track.title}")
                        }
                    }
                }
            }
        }
        AddButton("Musik hinzufügen") { picker.launch(arrayOf("audio/*")) }
        Text(
            "Die Datei wird in Clear gespeichert und bleibt nur auf deinem Handy. " +
                "Die Wiedergabe läuft, solange diese Seite offen ist.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    toDelete?.let { track ->
        ConfirmDeleteDialog(
            title = "Musik löschen?",
            message = "„${track.title}“ wird aus Clear entfernt. Dein Original bleibt unberührt.",
            onConfirm = { viewModel.delete(track); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }

    tagTrack?.let { track ->
        EmotionTagDialog(
            initial = EmotionTag.parse(track.emotionTags),
            onSave = { viewModel.updateTags(track, it); tagTrack = null },
            onDismiss = { tagTrack = null }
        )
    }
    goalTrack?.let { track ->
        MoodGoalDialog(
            initial = MoodGoal.parse(track.moodGoals),
            onSave = { viewModel.updateMoodGoals(track, it); goalTrack = null },
            onDismiss = { goalTrack = null }
        )
    }
}
