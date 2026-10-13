package com.example.clear.ui.meditation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.clear.model.Meditation
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/**
 * Player-Ansicht. Es sind noch keine Audiodateien eingebunden.
 * Darum sind die Bedienelemente deaktiviert und ein Hinweis erklaert es.
 * In einem spaeteren Schritt wird hier Media3 angebunden.
 */
@Composable
fun MeditationPlayerScreen(meditation: Meditation, onBack: () -> Unit) {
    val durationSeconds = meditation.durationMinutes * 60
    val audioAvailable = false  // wird mit der Audio-Integration echt berechnet

    ClearDetailScreen(title = meditation.title, onBack = onBack) {
        Text(
            meditation.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Ruhige Grafik statt Atemkreis-Animation
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = ClearSpacing.medium),
            contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier.size(160.dp).clip(CircleShape).background(ClearSurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(meditation.icon, contentDescription = null,
                    tint = ClearTextDark, modifier = Modifier.size(64.dp))
            }
        }

        if (!audioAvailable) {
            ClearCard(containerColor = ClearCardColor) {
                Text("Noch keine Audiodatei", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Für diese Meditation ist noch keine Aufnahme vorhanden. " +
                        "Sobald sie eingebunden ist, kannst du sie hier abspielen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Fortschritt und Zeiten
        Slider(
            value = 0f,
            onValueChange = {},
            enabled = audioAvailable,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0:00", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("-${formatTime(durationSeconds)}", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Steuerung: 15 Sekunden zurueck, Play, (Platzhalter fuer Symmetrie)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}, enabled = audioAvailable, modifier = Modifier.size(56.dp)) {
                Icon(Icons.Outlined.Replay, contentDescription = "15 Sekunden zurück")
            }
            FilledIconButton(onClick = {}, enabled = audioAvailable, modifier = Modifier.size(72.dp)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Abspielen",
                    modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.size(56.dp))
        }
    }
}

private fun formatTime(totalSeconds: Int): String =
    "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
