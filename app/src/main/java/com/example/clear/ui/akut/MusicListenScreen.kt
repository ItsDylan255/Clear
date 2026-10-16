package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.MusicTrack
import com.example.clear.model.EmotionTag
import com.example.clear.model.MoodGoal
import com.example.clear.playback.MusicPlayer
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft

private const val MODE_ALLOW = "allow"
private const val MODE_CHANGE = "change"
private val timerOptions = listOf(0, 5, 10, 15, 20, 30)   // 0 = ohne Timer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusicListenScreen(
    onBack: () -> Unit,
    viewModel: MusicListenViewModel = viewModel()
) {
    val tracks by viewModel.tracks.collectAsState()
    val player by MusicPlayer.state.collectAsState()
    val context = LocalContext.current

    var mode by rememberSaveable { mutableStateOf<String?>(null) }
    var goalId by rememberSaveable { mutableStateOf<String?>(null) }
    var timer by rememberSaveable { mutableIntStateOf(0) }

    val all = tracks.orEmpty()
    val ready = mode == MODE_ALLOW || (mode == MODE_CHANGE && goalId != null)
    val suggestions: List<MusicTrack> = when {
        mode == MODE_ALLOW && viewModel.situationId != "unknown" -> all.filter { track ->
            EmotionTag.parse(track.emotionTags).any { tag -> tag.id == viewModel.situationId }
        }
        mode == MODE_CHANGE -> all.filter { track ->
            MoodGoal.parse(track.moodGoals).any { goal -> goal.id == goalId }
        }
        else -> emptyList()
    }
    val others = all.filter { track -> track !in suggestions }

    ClearDetailScreen(title = "Musik hören", onBack = onBack) {
        Text(
            "Du entscheidest, was dir gerade guttut. " +
                "Du musst dich danach nicht besser fühlen.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ChoiceCard(
            title = "Meine Gefühle zulassen",
            description = "Musik, die zu dem passt, was du gerade fühlst.",
            selected = mode == MODE_ALLOW,
            onClick = { mode = MODE_ALLOW }
        )
        ChoiceCard(
            title = "Meine Stimmung verändern",
            description = "Musik, die dich in Richtung Ruhe, Hoffnung oder Energie begleitet.",
            selected = mode == MODE_CHANGE,
            onClick = { mode = MODE_CHANGE }
        )

        if (mode == MODE_CHANGE) {
            Text("Wohin möchtest du?", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                MoodGoal.entries.forEach { goal ->
                    FilterChip(
                        selected = goalId == goal.id,
                        onClick = { goalId = goal.id },
                        label = { Text(goal.title) }
                    )
                }
            }
        }

        Text("Timer (optional)", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
            timerOptions.forEach { minutes ->
                FilterChip(
                    selected = timer == minutes,
                    onClick = { timer = minutes },
                    label = { Text(if (minutes == 0) "Ohne Timer" else "$minutes Min.") }
                )
            }
        }
        Text(
            "Nach Ablauf blendet die Musik sanft aus. Mit Timer wird das Stück wiederholt.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Laufende Wiedergabe
        player.message?.let { text ->
            ClearCard(containerColor = ClearWarningSoft) {
                Text(text, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (player.trackId != null) {
            ClearCard(containerColor = ClearSurfaceSoft) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                ) {
                    IconButton(onClick = { MusicPlayer.togglePause() }) {
                        Icon(
                            if (player.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (player.isPlaying) "Pause" else "Weiter"
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(player.title.orEmpty(), style = MaterialTheme.typography.titleMedium)
                        Text(
                            player.timerMinutes?.let { "Timer: $it Min." } ?: "Ohne Timer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { MusicPlayer.stop() }) { Text("Stoppen") }
                }
            }
        }

        // Titelauswahl
        if (ready) {
            when {
                tracks == null -> Unit
                all.isEmpty() -> Text(
                    "Hier ist noch keine Musik gespeichert. Du kannst sie unter " +
                        "Mein Anker → Meine Musik hinzufügen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> {
                    if (suggestions.isNotEmpty()) {
                        Text("Vorschläge", style = MaterialTheme.typography.titleMedium)
                        suggestions.forEach { track ->
                            TrackRow(track, player.trackId == track.id) {
                                MusicPlayer.play(context, track, timer.takeIf { it > 0 })
                            }
                        }
                        if (others.isNotEmpty()) {
                            Text("Alle deine Titel", style = MaterialTheme.typography.titleMedium)
                        }
                    } else if (viewModel.situationId != "unknown") {
                        Text(
                            "Du hast noch keine Titel für diese Auswahl markiert. " +
                                "Unter Mein Anker → Meine Musik kannst du das tun. " +
                                "Du kannst trotzdem jeden Titel wählen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    others.forEach { track ->
                        TrackRow(track, player.trackId == track.id) {
                            MusicPlayer.play(context, track, timer.takeIf { it > 0 })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceCard(title: String, description: String, selected: Boolean, onClick: () -> Unit) {
    ClearCard(onClick = onClick, containerColor = if (selected) ClearSurfaceSoft else null) {
        Column(modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TrackRow(track: MusicTrack, isCurrent: Boolean, onPlay: () -> Unit) {
    ClearCard(onClick = onPlay, containerColor = if (isCurrent) ClearSurfaceSoft else null) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Abspielen: ${track.title}")
            Text(track.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
