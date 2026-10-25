package com.example.clear.ui.meditation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.clear.model.Meditation
import com.example.clear.playback.MeditationPlayer
import com.example.clear.ui.theme.ClearNoticeSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/**
 * Player-Ansicht. Die Aufnahme liegt in res/raw als "meditation_<id>". Fehlt sie, erklaert ein
 * Hinweis das und die Bedienelemente sind deaktiviert. Die Laenge kommt aus der Datei selbst.
 */
@Composable
fun MeditationPlayerScreen(meditation: Meditation, onBack: () -> Unit) {
    val context = LocalContext.current
    val audioAvailable = remember(meditation.id) {
        MeditationPlayer.resourceId(context, meditation) != 0
    }
    val player by MeditationPlayer.state.collectAsState()

    val isCurrent = player.meditationId == meditation.id
    val isPlaying = isCurrent && player.isPlaying
    val positionMs = if (isCurrent) player.positionMs else 0L
    val durationMs =
        if (isCurrent && player.durationMs > 0L) player.durationMs
        else meditation.durationMinutes * 60_000L          // bis die echte Laenge bekannt ist
    val progress = (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    var dragValue by remember { mutableStateOf<Float?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = ClearSpacing.large)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
        Text(meditation.title, style = MaterialTheme.typography.headlineMedium)
        // Ruhige Grafik statt Atemkreis-Animation
        // Die Grafik nimmt den freien Platz ein, so rutschen die Tasten nach unten
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(112.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(meditation.icon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(48.dp))
            }
        }

        if (!audioAvailable) {
            ClearCard {
                Text("Noch keine Audiodatei", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Für diese Meditation ist noch keine Aufnahme vorhanden. " +
                        "Sobald sie eingebunden ist, kannst du sie hier abspielen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (!isCurrent) {
            player.message?.let { text ->
                ClearCard(containerColor = ClearNoticeSoft) {
                    Text(text, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Kompakter Controller: Fortschritt, Zeiten und Tasten ohne grosse Abstaende
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            Slider(
                value = dragValue ?: progress,
                onValueChange = { dragValue = it },
                onValueChangeFinished = {
                    dragValue?.let { MeditationPlayer.seekTo((it * durationMs).toLong()) }
                    dragValue = null
                },
                enabled = audioAvailable && isCurrent,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(positionMs), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("-${formatTime((durationMs - positionMs).coerceAtLeast(0L))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // 15 Sekunden zurueck, Play/Pause, Stopp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { MeditationPlayer.seekBack(15) },
                    enabled = audioAvailable && isCurrent,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Outlined.Replay, contentDescription = "15 Sekunden zurück")
                }
                FilledIconButton(
                    onClick = {
                        if (isCurrent) MeditationPlayer.togglePause()
                        else MeditationPlayer.play(context, meditation)
                    },
                    enabled = audioAvailable,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Abspielen",
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(
                    onClick = { MeditationPlayer.stop() },
                    enabled = audioAvailable && isCurrent,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stopp")
                }
            }
        }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000L).toInt()
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
