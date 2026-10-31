package com.example.clear.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Farben wie in der Handy-App (dunkles Design)
private val ClearColors = Colors(
    primary = Color(0xFF8FB5DD),
    onPrimary = Color(0xFF12171C),
    surface = Color(0xFF26303A),
    onSurface = Color(0xFFE6EBF0),
    onSurfaceVariant = Color(0xFFAAB4BF)
)

private const val ROUTE_LIST = "ideen"
private const val ROUTE_RATE = "bewertung"
private const val ROUTE_DONE = "fertig"

/**
 * Laufende Ideen ansehen, beenden oder abbrechen. Beenden fragt wie auf dem Handy:
 * "Wie fuehlst du dich jetzt?"
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusStore.load(this)
        setContent {
            MaterialTheme(colors = ClearColors) { ClearWearApp() }
        }
    }
}

@Composable
private fun ClearWearApp() {
    val context = LocalContext.current
    val navController = rememberSwipeDismissableNavController()
    val status by StatusStore.status.collectAsState()
    var selected by remember { mutableStateOf<RunningIdea?>(null) }
    var sent by remember { mutableStateOf(true) }
    var cancelled by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { PhoneLink.refresh(context) }

    SwipeDismissableNavHost(navController = navController, startDestination = ROUTE_LIST) {
        composable(ROUTE_LIST) {
            IdeasList(status = status, onSelect = { idea ->
                selected = idea
                navController.navigate(ROUTE_RATE)
            })
        }
        composable(ROUTE_RATE) {
            val idea = selected
            if (idea != null) {
                RateScreen(
                    idea = idea,
                    onRate = { mood ->
                        scope.launch {
                            sent = PhoneLink.endIdea(context, idea.id, mood)
                            cancelled = false
                            navController.navigate(ROUTE_DONE) { popUpTo(ROUTE_LIST) }
                        }
                    },
                    onCancel = {
                        scope.launch {
                            sent = PhoneLink.cancelIdea(context, idea.id)
                            cancelled = true
                            navController.navigate(ROUTE_DONE) { popUpTo(ROUTE_LIST) }
                        }
                    }
                )
            }
        }
        composable(ROUTE_DONE) {
            DoneScreen(sent = sent, cancelled = cancelled, onDone = { navController.popBackStack(ROUTE_LIST, inclusive = false) })
        }
    }
}

/** Gemeinsamer Rahmen: Uhrzeit oben, Liste mit Drehen/Wischen scrollbar. */
@Composable
private fun ListScreen(content: androidx.wear.compose.foundation.lazy.ScalingLazyListScope.() -> Unit) {
    val listState = rememberScalingLazyListState()
    Scaffold(
        timeText = { TimeText() },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        ScalingLazyColumn(modifier = Modifier.fillMaxWidth(), state = listState, content = content)
    }
}

@Composable
private fun IdeasList(status: WatchStatus?, onSelect: (RunningIdea) -> Unit) {
    val timeFormat = remember { DateTimeFormatter.ofPattern("HH:mm") }
    ListScreen {
        item { ListHeader { Text(tr("Laufende Ideen", "Running ideas")) } }
        when {
            status == null -> item {
                Hint(
                    tr(
                        "Öffne Clear einmal auf dem Handy, damit die Uhr deinen Stand bekommt.",
                        "Open Clear on your phone once so the watch gets your status."
                    )
                )
            }
            status.ideas.isEmpty() -> item { Hint(tr("Gerade läuft keine Idee.", "No idea is running right now.")) }
            else -> items(status.ideas) { idea ->
                val since = Instant.ofEpochMilli(idea.startedAt).atZone(ZoneId.systemDefault()).format(timeFormat)
                Chip(
                    onClick = { onSelect(idea) },
                    label = { Text(idea.title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    secondaryLabel = { Text(tr("Seit $since · Beenden", "Since $since · End")) },
                    colors = ChipDefaults.secondaryChipColors(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun RateScreen(idea: RunningIdea, onRate: (Int) -> Unit, onCancel: () -> Unit) {
    var busy by remember { mutableStateOf(false) }
    ListScreen {
        item {
            Text(
                idea.title,
                style = MaterialTheme.typography.caption1,
                color = MaterialTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        item { ListHeader { Text(tr(
            "Wie fühlst du dich jetzt?",
            "How do you feel now?"
        ), textAlign = TextAlign.Center) } }
        items((10 downTo 1).toList()) { value ->
            Chip(
                onClick = {
                    if (!busy) {
                        busy = true
                        onRate(value)
                    }
                },
                label = { Text("$value") },
                secondaryLabel = when (value) {
                    10 -> ({ Text(tr("sehr gut", "very good")) })
                    1 -> ({ Text(tr("sehr schlecht", "very bad")) })
                    else -> null
                },
                enabled = !busy,
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Chip(
                onClick = {
                    if (!busy) {
                        busy = true
                        onCancel()
                    }
                },
                label = { Text(tr("Abbrechen", "Cancel")) },
                secondaryLabel = { Text(tr("Ohne Bewertung", "Without rating")) },
                enabled = !busy,
                colors = ChipDefaults.childChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DoneScreen(sent: Boolean, cancelled: Boolean, onDone: () -> Unit) {
    ListScreen {
        item { ListHeader { Text(
                when {
                    !sent -> tr("Nicht verbunden", "Not connected")
                    cancelled -> tr("Abgebrochen", "Cancelled")
                    else -> tr("Beendet", "Ended")
                }
            ) } }
        item {
            Hint(
                when {
                    !sent -> tr(
                        "Dein Handy ist gerade nicht erreichbar. Die Idee läuft weiter. Versuch es gleich noch einmal.",
                        "Your phone can't be reached right now. The idea keeps running. Please try again in a moment."
                    )
                    cancelled -> tr("Die Idee wurde ohne Bewertung beendet.", "The idea ended without a rating.")
                    else -> tr("Gespeichert auf deinem Handy.", "Saved on your phone.")
                }
            )
        }
        item {
            Chip(
                onClick = onDone,
                label = { Text(tr("Fertig", "Done")) },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun Hint(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.body2,
        color = MaterialTheme.colors.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
}
