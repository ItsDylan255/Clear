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
import com.example.clear.model.CheckInCategory
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
                    CheckInCategory.entries.forEach { category ->
                        FilterChip(
                            selected = s.category == category,
                            onClick = { viewModel.onCategoryChange(category) },
                            label = { Text(category.title) }
                        )
                    }
                }

                Text(tr(
                    "Was gilt in welchem Zustand?",
                    "What applies in which state?"
                ), style = MaterialTheme.typography.titleMedium)
                StateField(
                    label = tr("Hoch", "High"),
                    value = s.textNormal,
                    onChange = viewModel::onNormalChange,
                    isError = s.normalError
                )
                StateField(
                    label = tr("Mittel", "Medium"),
                    value = s.textSinking,
                    onChange = viewModel::onSinkingChange,
                    isError = s.sinkingError
                )
                StateField(
                    label = tr("Niedrig", "Low"),
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
