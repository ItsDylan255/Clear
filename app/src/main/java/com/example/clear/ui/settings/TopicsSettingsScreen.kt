package com.example.clear.ui.settings

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.ClearDatabase
import com.example.clear.data.IdeaTopic
import com.example.clear.data.IdeaTopicRepository
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.tr
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ClearEmptyState
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.TextInputDialog
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Ein Thema mit der Zahl seiner Ideen. */
data class TopicWithCount(val topic: IdeaTopic, val ideaCount: Int)

class TopicsSettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ClearDatabase.getInstance(app)
    private val repository = IdeaTopicRepository(db.ideaTopicDao())

    val topics: StateFlow<List<TopicWithCount>?> =
        combine(repository.observeAll(), PersonalIdeaRepository(db.personalIdeaDao()).observeAll()) { topics, ideas ->
            topics.map { topic -> TopicWithCount(topic, ideas.count { it.topicId == topic.id }) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun add(name: String) = viewModelScope.launch { repository.add(name) }
    fun rename(topic: IdeaTopic, name: String) = viewModelScope.launch { repository.rename(topic, name) }

    /** Loescht das Thema. Seine Ideen wandern nach `moveTo` (null = "Ohne Thema"). */
    fun delete(topic: IdeaTopic, moveTo: Long?) = viewModelScope.launch { repository.delete(topic, moveTo) }
}

/** Eigene Themen fuer Ideen. Antippen = umbenennen, Papierkorb = loeschen. */
@Composable
fun TopicsSettingsScreen(onBack: () -> Unit, viewModel: TopicsSettingsViewModel = viewModel()) {
    val topics by viewModel.topics.collectAsState()
    var adding by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf<IdeaTopic?>(null) }
    var deleting by remember { mutableStateOf<TopicWithCount?>(null) }

    ClearDetailScreen(title = tr("Bereiche", "Areas"), onBack = onBack) {
        AddButton(tr("Bereich anlegen", "Add area")) { adding = true }
        val list = topics
        when {
            list == null -> Unit
            list.isEmpty() -> ClearEmptyState(
                icon = Icons.Outlined.Lightbulb,
                title = tr("Noch keine Bereiche", "No areas yet")
            )
            else -> ClearCard {
                list.forEachIndexed { index, item ->
                    if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable { renaming = item.topic },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.topic.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                when (item.ideaCount) {
                                    0 -> tr("Noch keine Ideen", "No ideas yet")
                                    1 -> tr("1 Idee", "1 idea")
                                    else -> tr("${item.ideaCount} Ideen", "${item.ideaCount} ideas")
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { deleting = item }) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = tr("${item.topic.name} löschen", "Delete ${item.topic.name}")
                            )
                        }
                    }
                }
            }
        }
    }

    if (adding) {
        TextInputDialog(
            title = tr("Neuer Bereich", "New area"),
            label = tr("Name", "Name"),
            initial = "",
            onConfirm = { viewModel.add(it); adding = false },
            onDismiss = { adding = false }
        )
    }
    renaming?.let { topic ->
        TextInputDialog(
            title = tr("Bereich umbenennen", "Rename area"),
            label = tr("Name", "Name"),
            initial = topic.name,
            onConfirm = { viewModel.rename(topic, it); renaming = null },
            onDismiss = { renaming = null }
        )
    }
    deleting?.let { item ->
        if (item.ideaCount == 0) {
            ConfirmDeleteDialog(
                title = tr("Bereich löschen?", "Delete area?"),
                message = tr("„${item.topic.name}“ wird gelöscht.", "“${item.topic.name}” will be deleted."),
                onConfirm = { viewModel.delete(item.topic, null); deleting = null },
                onDismiss = { deleting = null }
            )
        } else {
            MoveIdeasDialog(
                item = item,
                others = topics.orEmpty().map { it.topic }.filter { it.id != item.topic.id },
                onConfirm = { moveTo -> viewModel.delete(item.topic, moveTo); deleting = null },
                onDismiss = { deleting = null }
            )
        }
    }
}

/** Thema mit Ideen loeschen: wohin mit den Ideen? Ein anderes Thema oder "Ohne Thema". */
@Composable
private fun MoveIdeasDialog(
    item: TopicWithCount,
    others: List<IdeaTopic>,
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    // null = "Ohne Thema"
    var target by remember { mutableStateOf<Long?>(null) }
    val options: List<Pair<Long?, String>> = listOf<Pair<Long?, String>>(null to tr("Ohne Bereich", "No area")) +
        others.map { it.id to it.name }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tr("„${item.topic.name}“ löschen?", "Delete “${item.topic.name}”?")) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    if (item.ideaCount == 1) tr("Wohin mit der Idee?", "Where should the idea go?")
                    else tr("Wohin mit den ${item.ideaCount} Ideen?", "Where should the ${item.ideaCount} ideas go?"),
                    style = MaterialTheme.typography.bodyLarge
                )
                options.forEach { (id, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .selectable(selected = target == id, role = Role.RadioButton) { target = id },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                    ) {
                        RadioButton(selected = target == id, onClick = null)
                        Text(name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(target) }) { Text(tr("Löschen", "Delete")) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(tr("Abbrechen", "Cancel")) } }
    )
}
