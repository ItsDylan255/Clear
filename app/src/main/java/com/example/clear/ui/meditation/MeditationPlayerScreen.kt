package com.example.clear.ui.meditation

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.clear.model.Meditation
import com.example.clear.playback.MeditationPlayer
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.theme.ClearNoticeSoft
import com.example.clear.ui.theme.ClearSpacing

private const val PLAYER_PREFS = "clear_player"
private const val KEY_BREATH = "breath_circle"

/**
 * Player-Ansicht mit ruhigem Farbverlauf und grosser runder Play-Taste. Die Aufnahme liegt in
 * res/raw als "meditation_<id>". Fehlt sie, erklaert ein Hinweis das und die Tasten sind aus.
 * Optional zeigt ein sehr langsamer Atemkreis beim Abspielen den Rhythmus (oben rechts
 * ein- und ausschaltbar, standardmaessig aus).
 */
@Composable
fun MeditationPlayerScreen(meditation: Meditation, onBack: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val audioAvailable = remember(meditation.id) {
        MeditationPlayer.resourceId(context, meditation) != 0
    }
    val player by MeditationPlayer.state.collectAsState()
    val prefs = remember { context.getSharedPreferences(PLAYER_PREFS, Context.MODE_PRIVATE) }
    var breathCircle by remember { mutableStateOf(prefs.getBoolean(KEY_BREATH, false)) }
    // "Animationen entfernen" in den Bedienungshilfen: dann gibt es keinen Atemkreis
    val reduceMotion = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }

    val isCurrent = player.meditationId == meditation.id
    val isPlaying = isCurrent && player.isPlaying
    val positionMs = if (isCurrent) player.positionMs else 0L
    val durationMs =
        if (isCurrent && player.durationMs > 0L) player.durationMs
        else meditation.durationMinutes * 60_000L          // bis die echte Laenge bekannt ist
    val progress = (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    var dragValue by remember { mutableStateOf<Float?>(null) }

    // Ruhiger Verlauf von der Akzentflaeche oben in den Hintergrund unten
    val gradient = Brush.verticalGradient(
        listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.background)
    )

    Column(modifier = Modifier.fillMaxSize().background(gradient)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Zurück", "Back"))
            }
            Spacer(Modifier.weight(1f))
            if (!reduceMotion) {
                IconButton(onClick = {
                    breathCircle = !breathCircle
                    prefs.edit().putBoolean(KEY_BREATH, breathCircle).apply()
                }) {
                    Icon(
                        Icons.Outlined.Air,
                        contentDescription = if (breathCircle) tr("Atemkreis ausschalten", "Turn breathing circle off")
                        else tr("Atemkreis einschalten", "Turn breathing circle on"),
                        tint = if (breathCircle) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(meditation.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${meditation.durationMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Ruhige Grafik in der Mitte. Mit Atemkreis waechst sie beim Abspielen langsam
            // (4 s ein, 6 s aus). Ohne Atemkreis bleibt sie still.
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BreathingCircle(
                    icon = meditation.icon,
                    animate = breathCircle && isPlaying && !reduceMotion
                )
            }

            if (!audioAvailable) {
                ClearCard {
                    Text(tr("Noch keine Audiodatei", "No audio file yet"), style = MaterialTheme.typography.titleMedium)
                    Text(
                        tr(
                            "Für diese Meditation ist noch keine Aufnahme vorhanden. " +
                                "Sobald sie eingebunden ist, kannst du sie hier abspielen.",
                            "There is no recording for this meditation yet. " +
                                "As soon as it is added, you can play it here."
                        ),
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

            // Fortschritt, Zeiten und Tasten
            Column {
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
                    // Ziffern gleich breit, damit die Zeiten beim Laufen nicht springen
                    val timeStyle = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum")
                    Text(formatTime(positionMs), style = timeStyle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "-${formatTime((durationMs - positionMs).coerceAtLeast(0L))}",
                        style = timeStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(ClearSpacing.small))
                // 15 Sekunden zurueck, grosse Play/Pause-Taste, Stopp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { MeditationPlayer.seekBack(15) },
                        enabled = audioAvailable && isCurrent,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Outlined.Replay, contentDescription = tr("15 Sekunden zurück", "Back 15 seconds"))
                    }
                    FilledIconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            if (isCurrent) MeditationPlayer.togglePause()
                            else MeditationPlayer.play(context, meditation)
                        },
                        enabled = audioAvailable,
                        shape = CircleShape,
                        modifier = Modifier.size(84.dp)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) tr("Pause", "Pause") else tr("Abspielen", "Play"),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(
                        onClick = { MeditationPlayer.stop() },
                        enabled = audioAvailable && isCurrent,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = tr("Stopp", "Stop"))
                    }
                }
            }
        }
    }
}

/** Kreis mit dem Symbol der Meditation. Mit `animate` atmet er langsam (4 s ein, 6 s aus). */
@Composable
private fun BreathingCircle(icon: ImageVector, animate: Boolean) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(animate) {
        if (animate) {
            while (true) {
                scale.animateTo(1.25f, tween(4_000, easing = FastOutSlowInEasing))
                scale.animateTo(1f, tween(6_000, easing = FastOutSlowInEasing))
            }
        } else {
            scale.animateTo(1f, tween(600))
        }
    }
    Box(contentAlignment = Alignment.Center) {
        // weicher Hof um den Kreis
        Box(
            modifier = Modifier
                .size(176.dp)
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.06f))
        )
        Box(
            modifier = Modifier
                .size(128.dp)
                .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000L).toInt()
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
