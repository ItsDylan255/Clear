package com.example.clear.ui.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.IdeaLevel
import com.example.clear.data.PersonalIdea
import com.example.clear.domain.WarningState
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

@Composable
fun CategoryDetailScreen(
    category: CheckInCategory,
    onBack: () -> Unit,
    onAddIdea: () -> Unit,
    onEditIdea: (Long) -> Unit,
    viewModel: CategoryViewModel = viewModel(),
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val ideas by viewModel.ideas.collectAsState()
    val analysis by warningsViewModel.analysis.collectAsState()
    val warningState = analysis?.categories?.get(category.id)?.state ?: WarningState.Normal
    var ideaToDelete by remember { mutableStateOf<PersonalIdea?>(null) }
    val list = ideas

    ClearDetailScreen(title = category.title, onBack = onBack) {
        // Optionaler, dezenter Warnhinweis. Die Kategorie bleibt immer voll nutzbar.
        WarningNotice(category, warningState)
        Text(
            category.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> ClearCard {
                Text(
                    "Hier ist noch nichts gespeichert. Du kannst jederzeit etwas hinzufügen.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                // Ideen zur aktuell zutreffenden Stufe zuerst, alle anderen darunter
                val current = warningState.toIdeaLevel()
                val matching = list.filter { idea -> idea.level == current }
                val others = list.filter { idea -> idea.level != current }
                if (matching.isNotEmpty()) {
                    if (others.isNotEmpty()) {
                        Text("Passend zu deinem Zustand", style = MaterialTheme.typography.titleMedium)
                    }
                    matching.forEach { idea ->
                        IdeaCard(
                            idea = idea,
                            onEdit = { onEditIdea(idea.id) },
                            onDelete = { ideaToDelete = idea }
                        )
                    }
                } else if (warningState != WarningState.Normal) {
                    Text(
                        "Für diesen Zustand hast du noch keine Idee gespeichert. " +
                            "Du kannst jederzeit eine hinzufügen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (others.isNotEmpty()) {
                    if (matching.isNotEmpty() || warningState != WarningState.Normal) {
                        Text("Weitere Ideen", style = MaterialTheme.typography.titleMedium)
                    }
                    others.forEach { idea ->
                        IdeaCard(
                            idea = idea,
                            onEdit = { onEditIdea(idea.id) },
                            onDelete = { ideaToDelete = idea }
                        )
                    }
                }
            }
        }

        Button(
            onClick = onAddIdea,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Text("Eigene Idee hinzufügen", modifier = Modifier.padding(start = ClearSpacing.small))
        }
    }

    // Bestaetigung vor dem Loeschen
    ideaToDelete?.let { idea ->
        AlertDialog(
            onDismissRequest = { ideaToDelete = null },
            title = { Text("Idee löschen?") },
            text = { Text("„${idea.title}“ wird dauerhaft gelöscht.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(idea)
                    ideaToDelete = null
                }) { Text("Löschen") }
            },
            dismissButton = {
                TextButton(onClick = { ideaToDelete = null }) { Text("Abbrechen") }
            }
        )
    }
}

@Composable
private fun IdeaCard(idea: PersonalIdea, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }

    ClearCard(onClick = onEdit) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
        ) {
            Column(modifier = Modifier.weight(1f).padding(top = ClearSpacing.small)) {
                Text(idea.title, style = MaterialTheme.typography.titleMedium)
                if (!idea.description.isNullOrBlank()) {
                    Text(idea.description, style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    "${idea.contentType.label} · ${idea.level.label}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = ClearSpacing.small)
                )
            }
            Box {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Optionen für ${idea.title}")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Bearbeiten") },
                        onClick = { menuOpen = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text("Löschen") },
                        onClick = { menuOpen = false; onDelete() }
                    )
                }
            }
        }
    }
}

/** Warnzustand der Kategorie -> passende Stufe fuer persoenliche Ideen. */
private fun WarningState.toIdeaLevel(): IdeaLevel = when (this) {
    WarningState.Normal -> IdeaLevel.Normal
    WarningState.Sinking -> IdeaLevel.Sinking
    WarningState.ContinuedLow -> IdeaLevel.Severe
}
