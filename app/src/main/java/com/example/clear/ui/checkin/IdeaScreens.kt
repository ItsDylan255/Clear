package com.example.clear.ui.checkin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.akut.ExerciseStatsViewModel
import com.example.clear.ui.akut.formatAverage
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSurfaceSoft

/**
 * Beschreibung einer Idee. "Starten" fragt zuerst, wie du dich fuehlst, und bringt dich dann
 * hierher zurueck. Solange die Idee laeuft, kannst du sie hier oder in der Benachrichtigung beenden.
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

    ClearDetailScreen(title = idea?.title ?: "Idee", onBack = onBack) {
        when {
            state.isLoading -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            idea == null -> Text(
                "Diese Idee gibt es nicht mehr.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> {
                val level = levels?.get(idea.categoryId)?.level.toIdeaLevel()
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
                    Text(
                        if (text.isNotBlank()) text
                        else "Für dieses Level fehlt noch ein Text. Du kannst ihn im Anker-Tab ergänzen.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        ideaLevelPhrase(level),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                stats[ActiveIdeas.key(idea.id)]?.let { stat ->
                    Text(
                        "Im Durchschnitt: ${formatAverage(stat.average)} bei ${stat.count} " +
                            if (stat.count == 1) "Durchlauf" else "Durchläufen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (running) {
                    ClearCard(containerColor = ClearSurfaceSoft) {
                        Text("Diese Idee läuft gerade.", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Du siehst sie auch in der Benachrichtigungsleiste und kannst sie dort beenden.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        onClick = { onEnd(idea.id) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text("Beenden") }
                } else {
                    Button(
                        onClick = { onStart(idea.id) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text("Starten") }
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
        title = s.idea?.title ?: "Idee",
        onBack = onBack
    ) {
        val result = s.result
        when {
            s.isLoading -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            s.idea == null -> Text(
                "Diese Idee gibt es nicht mehr.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            result != null -> {
                ClearCard(containerColor = ClearSurfaceSoft) {
                    Text("Von ${result.first} auf ${result.second}", style = MaterialTheme.typography.headlineMedium)
                    s.average?.let { average ->
                        Text(
                            "Im Durchschnitt bei dieser Idee: ${formatAverage(average)}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text("Fertig") }
            }
            else -> {
                Text(
                    if (viewModel.isBefore) "Wie fühlst du dich gerade?" else "Wie fühlst du dich jetzt?",
                    style = MaterialTheme.typography.titleLarge
                )
                ScaleSelector(
                    selected = s.mood, onSelect = viewModel::select,
                    lowLabel = "sehr schlecht", highLabel = "sehr gut"
                )
                Button(
                    onClick = { confirm() },
                    enabled = s.mood != null,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text(if (viewModel.isBefore) "Starten" else "Beenden") }
            }
        }
    }
}
