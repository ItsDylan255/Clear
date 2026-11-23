package com.example.clear.ui.anker

import com.example.clear.ui.components.ClearEmptyState
import androidx.compose.material.icons.outlined.SearchOff
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.ClearDatabase
import com.example.clear.data.PersonalIdea
import com.example.clear.data.IdeaLevel
import com.example.clear.data.IdeaTopic
import com.example.clear.data.IdeaTopicRepository
import com.example.clear.data.PersonalIdeaRepository
import androidx.compose.material.icons.outlined.Lightbulb
import com.example.clear.tr
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Eine Gruppe der Verwaltung: ein Thema oder "Ohne Thema" (topic == null). */
data class TopicGroup(val topic: IdeaTopic?, val ideas: List<PersonalIdea>) {
    val key: String get() = topic?.id?.toString() ?: "none"
    val name: String get() = topic?.name ?: tr("Ohne Bereich", "No area")
}

/** Alle Ideen, nach Thema gruppiert. "Ohne Thema" steht am Ende. null = wird noch geladen. */
class IdeasManageViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ClearDatabase.getInstance(app)
    private val repository = PersonalIdeaRepository(db.personalIdeaDao())

    val groups: StateFlow<List<TopicGroup>?> =
        combine(repository.observeAll(), IdeaTopicRepository(db.ideaTopicDao()).observeAll()) { ideas, topics ->
            val known = topics.map { it.id }.toSet()
            topics.map { topic -> TopicGroup(topic, ideas.filter { it.topicId == topic.id }) } +
                TopicGroup(null, ideas.filter { it.topicId == null || it.topicId !in known })
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(idea: PersonalIdea) {
        viewModelScope.launch { repository.delete(idea) }
    }
}

/**
 * Ideen anlegen, bearbeiten und loeschen, gruppiert nach Thema. Die Themen sind eingeklappt und
 * zeigen nur die Anzahl. Antippen klappt ein Thema auf. Die Suche findet Ideen ueber alle Themen.
 */
@Composable
fun IdeasManageScreen(
    onBack: () -> Unit,
    onAdd: (topicId: Long?) -> Unit,
    onEdit: (ideaId: Long) -> Unit,
    viewModel: IdeasManageViewModel = viewModel()
) {
    val groups by viewModel.groups.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    // Saveable, damit aufgeklappte Themen nach dem Bearbeiten einer Idee offen bleiben
    var expanded by rememberSaveable(stateSaver = expandedSaver) { mutableStateOf(setOf<String>()) }
    var toDelete by remember { mutableStateOf<PersonalIdea?>(null) }
    val list = groups

    ClearDetailScreen(title = tr("Ideen verwalten", "Manage ideas"), onBack = onBack) {
        AddButton(tr("Idee hinzufügen", "Add idea")) { onAdd(null) }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(tr("Suchen", "Search")) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = tr("Suche löschen", "Clear search"))
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (list == null) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else if (query.isNotBlank()) {
            // Suche: Treffer aus allen Themen (Titel und Fassungen), mit dem Namen des Themas
            val needle = query.trim()
            val matches = list.flatMap { group ->
                group.ideas
                    .filter { idea ->
                        idea.title.contains(needle, ignoreCase = true) ||
                            IdeaLevel.entries.any { idea.textFor(it).contains(needle, ignoreCase = true) }
                    }
                    .map { group to it }
            }
            if (matches.isEmpty()) {
                ClearEmptyState(
                    icon = Icons.Outlined.SearchOff,
                    title = tr("Nichts gefunden", "Nothing found")
                )
            }
            matches.forEach { (group, idea) ->
                IdeaRow(
                    idea = idea,
                    caption = group.name,
                    onEdit = { onEdit(idea.id) },
                    onDelete = { toDelete = idea }
                )
            }
        } else {
            list.forEach { group ->
                // "Ohne Thema" nur zeigen, wenn dort Ideen liegen oder es noch keine Themen gibt
                if (group.topic == null && group.ideas.isEmpty() && list.size > 1) return@forEach
                val isOpen = group.key in expanded
                ClearCard(
                    onClick = {
                        expanded = if (isOpen) expanded - group.key else expanded + group.key
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Lightbulb, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(group.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                when (group.ideas.size) {
                                    0 -> tr("Noch keine Ideen", "No ideas yet")
                                    1 -> tr("1 Idee", "1 idea")
                                    else -> tr("${group.ideas.size} Ideen", "${group.ideas.size} ideas")
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            if (isOpen) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (isOpen) tr("Zuklappen", "Collapse") else tr("Aufklappen", "Expand"),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (isOpen) {
                    group.ideas.forEach { idea ->
                        IdeaRow(
                            idea = idea,
                            caption = null,
                            onEdit = { onEdit(idea.id) },
                            onDelete = { toDelete = idea }
                        )
                    }
                    TextButton(
                        onClick = { onAdd(group.topic?.id) },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) { Text(tr("Idee zu „${group.name}“ hinzufügen", "Add idea to “${group.name}”")) }
                }
            }
        }
    }

    toDelete?.let { idea ->
        ConfirmDeleteDialog(
            title = tr("Idee löschen?", "Delete idea?"),
            message = tr("„${idea.title}“ wird dauerhaft gelöscht.", "“${idea.title}” will be deleted permanently."),
            onConfirm = { viewModel.delete(idea); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}

@Composable
private fun IdeaRow(idea: PersonalIdea, caption: String?, onEdit: () -> Unit, onDelete: () -> Unit) {
    ClearCard(onClick = onEdit) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
        ) {
            Column(modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                Text(idea.title, style = MaterialTheme.typography.bodyLarge)
                if (caption != null) {
                    Text(
                        caption,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            OptionsMenu(
                contentDescription = tr("Optionen für ${idea.title}", "Options for ${idea.title}"),
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}

private val expandedSaver = listSaver<Set<String>, String>(save = { it.toList() }, restore = { it.toSet() })
