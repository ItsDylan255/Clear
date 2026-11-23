package com.example.clear.ui.ideas

import android.app.Application
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.ClearDatabase
import com.example.clear.data.IdeaTopic
import com.example.clear.data.IdeaTopicRepository
import com.example.clear.data.PersonalIdea
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.domain.IdeaSuggestion
import com.example.clear.tr
import com.example.clear.ui.checkin.ActiveIdeas
import com.example.clear.ui.checkin.CancelIdeaDialog
import com.example.clear.ui.checkin.CheckInWarningsViewModel
import com.example.clear.ui.checkin.IdeaSessions
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearEmptyState
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Inhalt des Reiters: laufende Ideen (aelteste zuerst), alle Ideen und die Themen. */
data class IdeasTabState(
    val running: List<PersonalIdea> = emptyList(),
    val ideas: List<PersonalIdea>? = null,
    val topics: List<IdeaTopic> = emptyList()
)

class IdeasTabViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ClearDatabase.getInstance(app)

    init {
        ActiveIdeas.load(app)
    }

    val state: StateFlow<IdeasTabState> = combine(
        ActiveIdeas.running,
        PersonalIdeaRepository(db.personalIdeaDao()).observeAll(),
        IdeaTopicRepository(db.ideaTopicDao()).observeAll()
    ) { running, ideas, topics ->
        val byId = ideas.associateBy { it.id }
        IdeasTabState(
            running = running.values.sortedBy { it.startedAt }.mapNotNull { byId[it.ideaId] },
            ideas = ideas,
            topics = topics
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), IdeasTabState())

    /** Bricht eine laufende Idee ohne Bewertung ab. */
    fun cancel(ideaId: Long) {
        viewModelScope.launch { IdeaSessions.cancel(getApplication(), ideaId) }
    }
}

/**
 * Reiter "Ideen": oben die laufenden Ideen mit Beenden und Abbrechen, darunter die Ideen mit dem
 * Text der vorgeschlagenen Fassung. Das Filter-Symbol oben filtert nach eigenem Bereich.
 */
@Composable
fun IdeasScreen(
    onOpenIdea: (Long) -> Unit,
    onEndIdea: (Long) -> Unit,
    viewModel: IdeasTabViewModel = viewModel(),
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val levels by warningsViewModel.levels.collectAsState()
    // Gewaehlter Bereich (eigenes Thema), null = alle
    var topicFilter by rememberSaveable { mutableStateOf<Long?>(null) }
    var toCancel by rememberSaveable { mutableStateOf<Long?>(null) }
    var showFilter by rememberSaveable { mutableStateOf(false) }
    // Ein geloeschtes Thema faellt auf "alle" zurueck
    val filter = topicFilter?.takeIf { id -> state.topics.any { it.id == id } }

    val filterActive = filter != null
    ClearScreen(
        title = tr("Ideen", "Ideas"),
        actions = {
            // Nur mit eigenen Bereichen. Aktiver Filter: Symbol farbig hinterlegt
            if (state.topics.isEmpty()) Unit
            else if (filterActive) {
                FilledTonalIconButton(onClick = { showFilter = true }) {
                    Icon(Icons.Outlined.FilterList, contentDescription = tr("Filter (aktiv)", "Filter (active)"))
                }
            } else {
                IconButton(onClick = { showFilter = true }) {
                    Icon(Icons.Outlined.FilterList, contentDescription = tr("Filter", "Filter"))
                }
            }
        }
    ) {
        if (state.running.isNotEmpty()) {
            SectionLabel(tr("LÄUFT GERADE", "RUNNING NOW"))
            state.running.forEach { idea ->
                ClearCard(onClick = { onOpenIdea(idea.id) }) {
                    Text(idea.title, style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                        Button(
                            onClick = { onEndIdea(idea.id) },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                            shape = MaterialTheme.shapes.large
                        ) { Text(tr("Beenden", "End")) }
                        OutlinedButton(
                            onClick = { toCancel = idea.id },
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                            shape = MaterialTheme.shapes.large
                        ) { Text(tr("Abbrechen", "Cancel")) }
                    }
                }
            }
        }

        val all = state.ideas
        if (all != null && levels != null) {
            val shown = all
                .filter { filter == null || it.topicId == filter }
            if (shown.isEmpty()) {
                ClearEmptyState(icon = Icons.Outlined.Lightbulb, title = tr("Noch keine Ideen", "No ideas yet"))
            }
            shown.forEach { idea ->
                val text = idea.textFor(IdeaSuggestion.suggest(idea.needSet, levels))
                ClearCard(onClick = { onOpenIdea(idea.id) }) {
                    Text(idea.title, style = MaterialTheme.typography.titleMedium)
                    if (text.isNotBlank()) {
                        Text(
                            text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showFilter) {
        FilterDialog(
            topics = state.topics,
            topicId = filter,
            onTopic = { topicFilter = it },
            onDismiss = { showFilter = false }
        )
    }

    toCancel?.let { id ->
        CancelIdeaDialog(
            onConfirm = { viewModel.cancel(id); toCancel = null },
            onDismiss = { toCancel = null }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = ClearSpacing.small)
    )
}

/** Filter fuer die Ideen: ein eigener Bereich oder "Alle". */
@Composable
private fun FilterDialog(
    topics: List<IdeaTopic>,
    topicId: Long?,
    onTopic: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("Bereich", "Area")) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                FilterOption(tr("Alle", "All"), topicId == null) { onTopic(null); onDismiss() }
                topics.forEach { topic ->
                    FilterOption(topic.name, topicId == topic.id) { onTopic(topic.id); onDismiss() }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(tr("Schließen", "Close")) } }
    )
}

@Composable
private fun FilterOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}
