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
import com.example.clear.data.ContentType
import com.example.clear.data.IdeaLevel
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IdeaEditorScreen(
    onClose: () -> Unit,
    viewModel: IdeaEditorViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()

    // imePadding: Inhalt rutscht ueber die Tastatur
    Box(modifier = Modifier.imePadding()) {
        ClearDetailScreen(
            title = if (s.isNew) "Eigene Idee" else "Idee bearbeiten",
            onBack = onClose
        ) {
            if (s.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                OutlinedTextField(
                    value = s.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Titel") },
                    isError = s.titleError,
                    supportingText = { if (s.titleError) Text("Bitte gib einen Titel ein.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = s.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Beschreibung (optional)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Bereich", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                    CheckInCategory.entries.forEach { category ->
                        FilterChip(
                            selected = s.category == category,
                            onClick = { viewModel.onCategoryChange(category) },
                            label = { Text(category.title) }
                        )
                    }
                }

                Text("Art des Inhalts", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                    ContentType.entries.forEach { type ->
                        FilterChip(
                            selected = s.contentType == type,
                            onClick = { viewModel.onContentTypeChange(type) },
                            label = { Text(type.label) }
                        )
                    }
                }

                Text("Für welchen Zustand?", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                    IdeaLevel.entries.forEach { level ->
                        FilterChip(
                            selected = s.level == level,
                            onClick = { viewModel.onLevelChange(level) },
                            label = { Text(level.label) }
                        )
                    }
                }
                Text(
                    "Clear zeigt diese Idee zuerst, wenn dieser Zustand bei dir zutrifft. " +
                        "Du findest sie aber immer in der Liste.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onClose, modifier = Modifier.heightIn(min = 56.dp)) {
                        Text("Abbrechen")
                    }
                    Button(
                        onClick = { viewModel.save(onClose) },
                        modifier = Modifier.heightIn(min = 56.dp),
                        shape = MaterialTheme.shapes.large
                    ) { Text("Speichern") }
                }
            }
        }
    }
}
