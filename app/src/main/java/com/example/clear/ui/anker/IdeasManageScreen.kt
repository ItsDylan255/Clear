package com.example.clear.ui.anker

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.ClearDatabase
import com.example.clear.data.PersonalIdea
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.model.CheckInCategory
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

/** Ideen anlegen, bearbeiten und loeschen. Im Check-in werden sie nur noch angezeigt und ausprobiert. */
@Composable
fun IdeasManageScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (categoryId: String, ideaId: Long) -> Unit,
    viewModel: IdeasManageViewModel = viewModel()
) {
    val ideas by viewModel.ideas.collectAsState()
    var toDelete by remember { mutableStateOf<PersonalIdea?>(null) }
    val map = ideas

    ClearDetailScreen(title = "Ideen verwalten", onBack = onBack) {
        Text(
            "Hier legst du Ideen für deine Check-in-Bereiche an. Im Check-in tippst du sie dann an, " +
                "um sie auszuprobieren.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AddButton("Idee hinzufügen", onAdd)

        if (map == null) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else {
            CheckInCategory.entries.forEach { category ->
                val list = map[category].orEmpty()
                Text(category.title, style = MaterialTheme.typography.titleMedium)
                if (list.isEmpty()) {
                    Text(
                        "Noch keine Ideen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                list.forEach { idea ->
                    ClearCard(onClick = { onEdit(category.id, idea.id) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                        ) {
                            Text(
                                idea.title,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f).padding(top = ClearSpacing.small)
                            )
                            OptionsMenu(
                                contentDescription = "Optionen für ${idea.title}",
                                onEdit = { onEdit(category.id, idea.id) },
                                onDelete = { toDelete = idea }
                            )
                        }
                    }
                }
            }
        }
    }

    toDelete?.let { idea ->
        ConfirmDeleteDialog(
            title = "Idee löschen?",
            message = "„${idea.title}“ wird dauerhaft gelöscht.",
            onConfirm = { viewModel.delete(idea); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}
