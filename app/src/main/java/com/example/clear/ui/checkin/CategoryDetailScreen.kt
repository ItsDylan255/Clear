package com.example.clear.ui.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.IdeaLevel
import com.example.clear.data.PersonalIdea
import com.example.clear.domain.CategoryLevel
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.akut.ExerciseStat
import com.example.clear.ui.akut.ExerciseStatsViewModel
import com.example.clear.ui.akut.formatAverage
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

/**
 * Ein Check-in-Bereich: Level der letzten 14 Tage und die Ideen dazu. Tippst du eine Idee an,
 * startest du sie (Stimmung davor, laeuft mit Dauer, Stimmung danach). Ideen anlegen und
 * bearbeiten geht im Anker-Tab unter "Ideen verwalten".
 */
@Composable
fun CategoryDetailScreen(
    category: CheckInCategory,
    onBack: () -> Unit,
    onOpenIdea: (Long) -> Unit,
    viewModel: CategoryViewModel = viewModel(),
    warningsViewModel: CheckInWarningsViewModel = viewModel(),
    statsViewModel: ExerciseStatsViewModel = viewModel()
) {
    val context = LocalContext.current
    val ideas by viewModel.ideas.collectAsState()
    val levels by warningsViewModel.levels.collectAsState()
    val stats by statsViewModel.stats.collectAsState()
    val runningIdeas by ActiveIdeas.running.collectAsState()
    remember { ActiveIdeas.load(context) }
    val info = levels?.get(category.id)
    val list = ideas

    ClearDetailScreen(title = category.title, onBack = onBack) {
        LevelNotice(info)
        Text(
            category.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> ClearCard {
                Text(
                    "Hier gibt es noch keine Ideen. Du kannst sie im Anker-Tab unter " +
                        "„Ideen verwalten“ hinzufügen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                Text(
                    "Tippe auf eine Idee, um die Beschreibung zu lesen und sie zu starten.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // Jede Idee zeigt den Text, der zum aktuellen Level passt
                val current = info?.level.toIdeaLevel()
                list.forEach { idea ->
                    IdeaCard(
                        idea = idea,
                        level = current,
                        stat = stats[ActiveIdeas.key(idea.id)],
                        running = runningIdeas.containsKey(idea.id),
                        onOpen = { onOpenIdea(idea.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IdeaCard(
    idea: PersonalIdea,
    level: IdeaLevel,
    stat: ExerciseStat?,
    running: Boolean,
    onOpen: () -> Unit
) {
    ClearCard(onClick = onOpen) {
        Column(verticalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
            ) {
                Text(
                    idea.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // Durchschnittliche Veraenderung der Stimmung, sobald es Durchlaeufe gibt
                if (stat != null) {
                    Text(
                        formatAverage(stat.average),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            val text = idea.textFor(level)
            if (text.isNotBlank()) {
                Text(text, style = MaterialTheme.typography.bodyLarge)
            } else {
                Text(
                    "Für dieses Level fehlt noch ein Text. Du kannst ihn im Anker-Tab ergänzen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (running) {
                Text(
                    "Läuft gerade",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                ideaLevelPhrase(level),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = ClearSpacing.small)
            )
        }
    }
}

/** Level der Kategorie -> passende Stufe fuer persoenliche Ideen. Ohne Wert: normal. */
internal fun CategoryLevel?.toIdeaLevel(): IdeaLevel = when (this) {
    CategoryLevel.Low -> IdeaLevel.Severe
    CategoryLevel.Medium -> IdeaLevel.Sinking
    CategoryLevel.High, null -> IdeaLevel.Normal
}

internal fun ideaLevelPhrase(level: IdeaLevel): String = when (level) {
    IdeaLevel.Normal -> levelPhrase(CategoryLevel.High)
    IdeaLevel.Sinking -> levelPhrase(CategoryLevel.Medium)
    IdeaLevel.Severe -> levelPhrase(CategoryLevel.Low)
}
