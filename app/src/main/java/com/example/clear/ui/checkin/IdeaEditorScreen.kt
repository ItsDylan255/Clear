package com.example.clear.ui.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.AssistChip
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.clear.data.IdeaLevel
import com.example.clear.data.IdeaNeeds
import com.example.clear.ui.components.TextInputDialog
import com.example.clear.tr
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IdeaEditorScreen(
    onClose: () -> Unit,
    viewModel: IdeaEditorViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()
    val topics by viewModel.topics.collectAsState()
    var addingTopic by remember { mutableStateOf(false) }

    // imePadding: Inhalt rutscht ueber die Tastatur
    Box(modifier = Modifier.imePadding()) {
        ClearDetailScreen(
            title = if (s.isNew) tr("Eigene Idee", "Your own idea") else tr("Idee bearbeiten", "Edit idea"),
            onBack = onClose
        ) {
            if (s.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                OutlinedTextField(
                    value = s.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text(tr("Titel", "Title")) },
                    isError = s.titleError,
                    supportingText = { if (s.titleError) Text(tr(
                        "Bitte gib einen Titel ein.",
                        "Please enter a title."
                    )) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(tr("Bereich", "Area"), style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                    FilterChip(
                        selected = s.topicId == null,
                        onClick = { viewModel.onTopicChange(null) },
                        label = { Text(tr("Ohne Bereich", "No area")) }
                    )
                    topics.forEach { topic ->
                        FilterChip(
                            selected = s.topicId == topic.id,
                            onClick = { viewModel.onTopicChange(topic.id) },
                            label = { Text(topic.name) }
                        )
                    }
                    AssistChip(
                        onClick = { addingTopic = true },
                        label = { Text("+") },
                        modifier = Modifier.semantics { contentDescription = tr("Bereich anlegen", "Add area") }
                    )
                }

                Text(tr("Braucht", "Needs"), style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                    IdeaNeeds.all.forEach { category ->
                        FilterChip(
                            selected = category in s.needs,
                            onClick = { viewModel.toggleNeed(category) },
                            label = { Text(category.title) }
                        )
                    }
                }

                Text(tr("Fassungen", "Versions"), style = MaterialTheme.typography.titleMedium)
                StateField(
                    label = ideaLevelPhrase(IdeaLevel.Normal),
                    value = s.textNormal,
                    onChange = viewModel::onNormalChange,
                    isError = s.normalError
                )
                StateField(
                    label = ideaLevelPhrase(IdeaLevel.Sinking),
                    value = s.textSinking,
                    onChange = viewModel::onSinkingChange,
                    isError = s.sinkingError
                )
                StateField(
                    label = ideaLevelPhrase(IdeaLevel.Severe),
                    value = s.textSevere,
                    onChange = viewModel::onSevereChange,
                    isError = s.severeError
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onClose, modifier = Modifier.heightIn(min = 56.dp)) {
                        Text(tr("Abbrechen", "Cancel"))
                    }
                    Button(
                        onClick = { viewModel.save(onClose) },
                        modifier = Modifier.heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text(tr("Speichern", "Save")) }
                }
            }
        }
    }

    if (addingTopic) {
        TextInputDialog(
            title = tr("Neuer Bereich", "New area"),
            label = tr("Name", "Name"),
            initial = "",
            onConfirm = { viewModel.addTopic(it); addingTopic = false },
            onDismiss = { addingTopic = false }
        )
    }
}

/** Ein Textfeld pro Zustand. Alle drei muessen ausgefuellt werden. */
@Composable
private fun StateField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        isError = isError,
        supportingText = { if (isError) Text(tr("Bitte ergänze diesen Text.", "Please fill in this text.")) },
        minLines = 2,
        modifier = Modifier.fillMaxWidth()
    )
}
