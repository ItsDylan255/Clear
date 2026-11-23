package com.example.clear.ui.checkin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.border
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.example.clear.data.IdeaLevel
import com.example.clear.data.PersonalIdea
import com.example.clear.domain.IdeaSuggestion
import com.example.clear.tr
import com.example.clear.ui.akut.ExerciseStatsViewModel
import com.example.clear.ui.akut.formatAverage
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSurfaceSoft

/**
 * Eine Idee mit allen drei Fassungen. Die vorgeschlagene Fassung (aus "Braucht" und den
 * 14-Tage-Durchschnitten) ist vorausgewaehlt und markiert, jede andere laesst sich antippen.
 * "Starten" fragt nach der Stimmung und bringt dich dann hierher zurueck. Solange die Idee laeuft,
 * kannst du sie hier oder in der Benachrichtigung beenden oder ohne Bewertung abbrechen.
 */
@Composable
fun IdeaDetailScreen(
    onBack: () -> Unit,
    onStart: (ideaId: Long, variant: IdeaLevel) -> Unit,
    onEnd: (Long) -> Unit,
    viewModel: IdeaDetailViewModel = viewModel(),
    warningsViewModel: CheckInWarningsViewModel = viewModel(),
    statsViewModel: ExerciseStatsViewModel = viewModel()
) {
    val context = LocalContext.current
    remember { ActiveIdeas.load(context) }
    val state by viewModel.state.collectAsState()
    val levels by warningsViewModel.levels.collectAsState()
    val stats by statsViewModel.stats.collectAsState()
    val runningMap by ActiveIdeas.running.collectAsState()
    val idea = state.idea
    // Selbst gewaehlte Fassung (Name von IdeaLevel). null = die vorgeschlagene
    var chosen by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmCancel by rememberSaveable { mutableStateOf(false) }

    ClearDetailScreen(title = idea?.title ?: tr("Idee", "Idea"), onBack = onBack) {
        when {
            state.isLoading || levels == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            idea == null -> Text(
                tr("Diese Idee gibt es nicht mehr.", "This idea no longer exists."),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> {
                val running = runningMap.containsKey(idea.id)
                val suggested = IdeaSuggestion.suggest(idea.needSet, levels)
                val picked = IdeaLevel.entries.firstOrNull { it.name == chosen }
                // Laeuft die Idee, bleibt die Fassung des laufenden Durchlaufs ausgewaehlt
                val selected = (if (running) state.runningVariant ?: picked else picked) ?: suggested

                stats[ActiveIdeas.key(idea.id)]?.let { stat ->
                    Text(
                        tr(
                            "Im Durchschnitt: ${formatAverage(stat.average)} bei ${stat.count} ",
                            "On average: ${formatAverage(stat.average)} over ${stat.count} "
                        ) +
                            if (stat.count == 1) tr("Durchlauf", "run") else tr("Durchläufen", "runs"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IdeaLevel.entries.forEach { variant ->
                    VariantCard(
                        idea = idea,
                        variant = variant,
                        selected = variant == selected,
                        suggested = variant == suggested,
                        enabled = !running,
                        onSelect = { chosen = variant.name }
                    )
                }

                if (running) {
                    ClearCard(containerColor = ClearSurfaceSoft) {
                        Text(tr(
                            "Diese Idee läuft gerade.",
                            "This idea is running right now."
                        ), style = MaterialTheme.typography.titleMedium)
                    }
                    Button(
                        onClick = { onEnd(idea.id) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text(tr("Beenden", "End")) }
                    OutlinedButton(
                        onClick = { confirmCancel = true },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text(tr("Abbrechen", "Cancel")) }
                    if (confirmCancel) {
                        CancelIdeaDialog(
                            onConfirm = {
                                confirmCancel = false
                                viewModel.cancel()
                            },
                            onDismiss = { confirmCancel = false }
                        )
                    }
                } else {
                    Button(
                        onClick = { onStart(idea.id, selected) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text(tr("Starten", "Start")) }
                }
            }
        }
    }
}

/**
 * Eine Fassung zum Antippen, nur mit ihrem Text. Ausgewaehlt: farbig hinterlegt.
 * Vorgeschlagen: umrandet (und beim Oeffnen schon ausgewaehlt).
 */
@Composable
private fun VariantCard(
    idea: PersonalIdea,
    variant: IdeaLevel,
    selected: Boolean,
    suggested: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit
) {
    val text = idea.textFor(variant)
    val shape = MaterialTheme.shapes.medium
    ClearCard(
        onClick = if (enabled) onSelect else null,
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else null,
        modifier = Modifier
            .then(if (suggested) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape) else Modifier)
            .semantics { this.selected = selected }
    ) {
        Text(text.ifBlank { "–" }, style = MaterialTheme.typography.bodyLarge)
    }
}

/** Rueckfrage vor dem Abbrechen einer laufenden Idee. */
@Composable
internal fun CancelIdeaDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Idee abbrechen?", "Cancel idea?")) },
        text = {
            Text(tr(
                "Die Idee endet ohne Bewertung. Dieser Durchlauf wird nicht gespeichert.",
                "The idea ends without a rating. This run won't be saved."
            ))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(tr("Abbrechen", "Cancel")) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(tr(
                "Weiterlaufen lassen",
                "Keep running"
            )) }
        }
    )
}

/** "Wie ist deine Stimmung gerade?" (vor dem Start) oder "...jetzt?" (nach dem Beenden). */
@Composable
fun IdeaRateScreen(
    onBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: IdeaRateViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Beim ersten Start einmal um die Erlaubnis fuer Benachrichtigungen bitten. Die Idee startet in jedem Fall.
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.confirm(onDone)
        }
    fun confirm() {
        val needsPermission = viewModel.isBefore && Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsPermission) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        else viewModel.confirm(onDone)
    }

    ClearDetailScreen(
        title = s.idea?.title ?: tr("Idee", "Idea"),
        onBack = onBack
    ) {
        val result = s.result
        when {
            s.isLoading -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            s.idea == null -> Text(
                tr("Diese Idee gibt es nicht mehr.", "This idea no longer exists."),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            result != null -> {
                ClearCard(containerColor = ClearSurfaceSoft) {
                    Text(tr(
                        "Von ${result.first} auf ${result.second}",
                        "From ${result.first} to ${result.second}"
                    ), style = MaterialTheme.typography.headlineMedium)
                    s.average?.let { average ->
                        Text(
                            tr(
                                "Im Durchschnitt bei dieser Idee: ${formatAverage(average)}",
                                "Average for this idea: ${formatAverage(average)}"
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text(tr("Fertig", "Done")) }
            }
            else -> {
                Text(
                    if (viewModel.isBefore) tr("Wie ist deine Stimmung gerade?", "How is your mood right now?") else tr("Wie ist deine Stimmung jetzt?", "How is your mood now?"),
                    style = MaterialTheme.typography.titleLarge
                )
                ScaleSelector(
                    selected = s.mood, onSelect = viewModel::select,
                    lowLabel = tr("sehr schlecht", "very bad"), highLabel = tr("sehr gut", "very good")
                )
                Button(
                    onClick = { confirm() },
                    enabled = s.mood != null,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text(if (viewModel.isBefore) tr("Starten", "Start") else tr("Beenden", "End")) }
            }
        }
    }
}
