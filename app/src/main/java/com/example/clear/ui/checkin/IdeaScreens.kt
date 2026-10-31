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
import com.example.clear.domain.CategoryLevel
import com.example.clear.model.CheckInCategory
import com.example.clear.tr
import com.example.clear.ui.akut.ExerciseStatsViewModel
import com.example.clear.ui.akut.formatAverage
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSurfaceSoft

/**
 * Beschreibung einer Idee. "Starten" fragt zuerst, wie du dich fuehlst, und bringt dich dann
 * hierher zurueck. Solange die Idee laeuft, kannst du sie hier oder in der Benachrichtigung beenden
 * oder ohne Bewertung abbrechen.
 */
@Composable
fun IdeaDetailScreen(
    onBack: () -> Unit,
    onStart: (Long) -> Unit,
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
    var showOthers by rememberSaveable { mutableStateOf(false) }
    var confirmCancel by rememberSaveable { mutableStateOf(false) }

    ClearDetailScreen(title = idea?.title ?: tr("Idee", "Idea"), onBack = onBack) {
        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            idea == null -> Text(
                tr("Diese Idee gibt es nicht mehr.", "This idea no longer exists."),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> {
                val currentLevel = levels?.get(idea.categoryId)?.level ?: CategoryLevel.High
                val level = currentLevel.toIdeaLevel()
                val running = runningMap.containsKey(idea.id)
                val text = idea.textFor(level)

                CheckInCategory.fromId(idea.categoryId)?.let {
                    Text(
                        it.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                ClearCard {
                    Text(levelName(currentLevel), style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (text.isNotBlank()) text
                        else tr(
                            "Für dieses Level fehlt noch ein Text. Du kannst ihn im Anker-Tab ergänzen.",
                            "There is no text for this level yet. You can add it in the Anchor tab."
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
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

                // Die anderen Varianten (Level) der Idee, nur bei Bedarf
                val others = listOf(CategoryLevel.High, CategoryLevel.Medium, CategoryLevel.Low)
                    .filter { it != currentLevel }
                TextButton(
                    onClick = { showOthers = !showOthers },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) { Text(if (showOthers) tr("Andere Varianten ausblenden", "Hide other variants") else tr("Andere Varianten anzeigen", "Show other variants")) }
                if (showOthers) {
                    others.forEach { variant ->
                        val variantText = idea.textFor(variant.toIdeaLevel())
                        ClearCard {
                            Text(levelName(variant), style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (variantText.isNotBlank()) variantText else tr(
                                    "Dafür gibt es noch keinen Text.",
                                    "There is no text for this yet."
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (variantText.isNotBlank()) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (running) {
                    ClearCard(containerColor = ClearSurfaceSoft) {
                        Text(tr(
                            "Diese Idee läuft gerade.",
                            "This idea is running right now."
                        ), style = MaterialTheme.typography.titleMedium)
                        Text(
                            tr(
                                "Du siehst sie auch in der Benachrichtigungsleiste und kannst sie dort beenden.",
                                "You can also see it in the notification bar and end it there."
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                        AlertDialog(
                            onDismissRequest = { confirmCancel = false },
                            title = { Text(tr("Idee abbrechen?", "Cancel idea?")) },
                            text = {
                                Text(tr(
                                    "Die Idee endet ohne Bewertung. Dieser Durchlauf wird nicht gespeichert.",
                                    "The idea ends without a rating. This run won't be saved."
                                ))
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    confirmCancel = false
                                    viewModel.cancel()
                                }) { Text(tr("Abbrechen", "Cancel")) }
                            },
                            dismissButton = {
                                TextButton(onClick = { confirmCancel = false }) { Text(tr(
                                    "Weiterlaufen lassen",
                                    "Keep running"
                                )) }
                            }
                        )
                    }
                } else {
                    Button(
                        onClick = { onStart(idea.id) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text(tr("Starten", "Start")) }
                }
            }
        }
    }
}

/** "Wie fuehlst du dich gerade?" (vor dem Start) oder "...jetzt?" (nach dem Beenden). */
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
                    if (viewModel.isBefore) tr("Wie fühlst du dich gerade?", "How do you feel right now?") else tr("Wie fühlst du dich jetzt?", "How do you feel now?"),
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
