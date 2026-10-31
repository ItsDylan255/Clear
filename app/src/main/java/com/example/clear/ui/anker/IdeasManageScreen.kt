package com.example.clear.ui.anker

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
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.model.CheckInCategory
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

/** Alle Ideen, nach Bereich sortiert. null = wird noch geladen. */
class IdeasManageViewModel(app: Application) : AndroidViewModel(app) {
    private val repository =
        PersonalIdeaRepository(ClearDatabase.getInstance(app).personalIdeaDao())

    val ideas: StateFlow<Map<CheckInCategory, List<PersonalIdea>>?> =
        combine(CheckInCategory.entries.map { repository.observe(it.id) }) { lists ->
            CheckInCategory.entries.zip(lists.toList()).toMap()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun delete(idea: PersonalIdea) {
        viewModelScope.launch { repository.delete(idea) }
    }
}

/**
 * Ideen anlegen, bearbeiten und loeschen. Uebersichtlich auch bei vielen Ideen: Die Bereiche sind
 * eingeklappt und zeigen nur die Anzahl. Antippen klappt einen Bereich auf. Die Suche findet Ideen
 * ueber alle Bereiche. Im Check-in werden Ideen nur angezeigt und ausprobiert.
 */
@Composable
fun IdeasManageScreen(
    onBack: () -> Unit,
    onAdd: (categoryId: String) -> Unit,
    onEdit: (categoryId: String, ideaId: Long) -> Unit,
    viewModel: IdeasManageViewModel = viewModel()
) {
    val ideas by viewModel.ideas.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    // Saveable, damit aufgeklappte Bereiche nach dem Bearbeiten einer Idee offen bleiben
    var expanded by rememberSaveable(stateSaver = expandedSaver) { mutableStateOf(setOf<String>()) }
    var toDelete by remember { mutableStateOf<PersonalIdea?>(null) }
    val map = ideas

    ClearDetailScreen(title = tr("Ideen verwalten", "Manage ideas"), onBack = onBack) {
        AddButton(tr("Idee hinzufügen", "Add idea")) { onAdd(CheckInCategory.entries.first().id) }

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

        if (map == null) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else if (query.isNotBlank()) {
            // Suche: Treffer aus allen Bereichen, mit dem Namen des Bereichs
            val matches = CheckInCategory.entries.flatMap { category ->
                map[category].orEmpty()
                    .filter { it.title.contains(query.trim(), ignoreCase = true) }
                    .map { category to it }
            }
            if (matches.isEmpty()) {
                Text(
                    tr("Dazu wurde nichts gefunden.", "Nothing found."),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            matches.forEach { (category, idea) ->
                IdeaRow(
                    idea = idea,
                    caption = category.title,
                    onEdit = { onEdit(category.id, idea.id) },
                    onDelete = { toDelete = idea }
                )
            }
        } else {
            CheckInCategory.entries.forEach { category ->
                val list = map[category].orEmpty()
                val isOpen = category.id in expanded
                ClearCard(
                    onClick = {
                        expanded = if (isOpen) expanded - category.id else expanded + category.id
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
                                category.icon, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(category.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                when (list.size) {
                                    0 -> tr("Noch keine Ideen", "No ideas yet")
                                    1 -> tr("1 Idee", "1 idea")
                                    else -> tr("${list.size} Ideen", "${list.size} ideas")
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
                    list.forEach { idea ->
                        IdeaRow(
                            idea = idea,
                            caption = null,
                            onEdit = { onEdit(category.id, idea.id) },
                            onDelete = { toDelete = idea }
                        )
                    }
                    TextButton(
                        onClick = { onAdd(category.id) },
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) { Text(tr("Idee zu „${category.title}“ hinzufügen", "Add idea to “${category.title}”")) }
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
