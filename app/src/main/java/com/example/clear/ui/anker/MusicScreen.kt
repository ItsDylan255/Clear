package com.example.clear.ui.anker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.MusicTrack
import com.example.clear.model.MusicMode
import com.example.clear.ui.akut.ClearWarningSoft
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.EmptyHintCard
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft

@Composable
fun MusicScreen(onBack: () -> Unit, viewModel: MusicViewModel = viewModel()) {
    val tracks by viewModel.tracks.collectAsState()
    val playback by viewModel.playback.collectAsState()
    val list = tracks
    var toDelete by remember { mutableStateOf<MusicTrack?>(null) }
    var modeFor by remember { mutableStateOf<MusicTrack?>(null) }
    var showAddMode by remember { mutableStateOf(false) }
    // Die gewaehlte Art merkt sich die Seite, waehrend die Dateiauswahl des Systems offen ist
    var pendingModeId by rememberSaveable { mutableStateOf<String?>(null) }

    // Dateiauswahl des Systems (Storage Access Framework)
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val mode = MusicMode.parse(pendingModeId.orEmpty())
        if (uri != null && mode != null) viewModel.addTrack(uri, mode)
        pendingModeId = null
    }

    ClearDetailScreen(
        title = viewModel.emotionName?.let { "Musik · $it" } ?: "Meine Musik",
        onBack = onBack
    ) {
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
                            TextButton(onClick = { modeFor = track }) {
                                Text(
                                    MusicMode.parse(track.moodGoals)?.title ?: "Art der Musik wählen",
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
        AddButton("Musik hinzufügen") { showAddMode = true }
        Text(
            "Die Datei wird in Clear gespeichert und bleibt nur auf deinem Handy. " +
                "Die Wiedergabe läuft, solange diese Seite offen ist.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showAddMode) {
        MusicModeDialog(
            title = "Wofür ist diese Musik?",
            onPick = { mode ->
                showAddMode = false
                pendingModeId = mode.id
                picker.launch(arrayOf("audio/*"))
            },
            onDismiss = { showAddMode = false }
        )
    }
    modeFor?.let { track ->
        MusicModeDialog(
            title = "Wofür ist diese Musik?",
            onPick = { mode -> viewModel.updateMode(track, mode); modeFor = null },
            onDismiss = { modeFor = null }
        )
    }
    toDelete?.let { track ->
        ConfirmDeleteDialog(
            title = "Musik löschen?",
            message = "„${track.title}“ wird aus Clear entfernt. Dein Original bleibt unberührt." +
                deleteNote(track.emotionTags),
            onConfirm = { viewModel.delete(track); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}

/** Genau eine der beiden Arten waehlen. */
@Composable
private fun MusicModeDialog(title: String, onPick: (MusicMode) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MusicMode.entries.forEach { mode ->
                    OutlinedButton(
                        onClick = { onPick(mode) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text(mode.title) }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}
