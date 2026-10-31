package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.AppLanguage
import com.example.clear.data.EveningEntry
import com.example.clear.data.EveningType
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.EditorButtons
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.theme.ClearSpacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private fun dayLabel(epochDay: Long): String =
    LocalDate.ofEpochDay(epochDay)
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(AppLanguage.locale))

// ---------- Archiv ----------

@Composable
fun EveningArchiveScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: EveningArchiveViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val query by viewModel.query.collectAsState()
    var toDelete by remember { mutableStateOf<EveningEntry?>(null) }
    val list = entries

    ClearDetailScreen(title = tr("Erfolge und gute Dinge", "Successes and good things"), onBack = onBack) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            label = { Text(tr("Suchen", "Search")) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = tr("Suche löschen", "Clear search"))
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> Text(
                if (query.isNotBlank()) tr("Dazu wurde nichts gefunden.", "Nothing found.")
                else tr(
                    "Hier ist noch nichts gespeichert. Einträge entstehen am Ende deines Check-ins.",
                    "Nothing saved here yet. Entries are created at the end of your check-in."
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> list.groupBy { it.day }.forEach { (day, dayEntries) ->
                Text(dayLabel(day), style = MaterialTheme.typography.titleMedium)
                dayEntries.forEach { entry ->
                    ClearCard(onClick = { onEdit(entry.id) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(top = ClearSpacing.small)) {
                                Text(entry.text, style = MaterialTheme.typography.bodyLarge)
                                if (!entry.reason.isNullOrBlank()) {
                                    Text(
                                        tr("Warum: ${entry.reason}", "Why: ${entry.reason}"),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = ClearSpacing.small)
                                    )
                                }
                            }
                            OptionsMenu(
                                contentDescription = tr("Optionen für diesen Eintrag", "Options for this entry"),
                                onEdit = { onEdit(entry.id) },
                                onDelete = { toDelete = entry }
                            )
                        }
                    }
                }
            }
        }

    }

    toDelete?.let { entry ->
        ConfirmDeleteDialog(
            title = tr("Eintrag löschen?", "Delete entry?"),
            message = tr("Dieser Eintrag wird dauerhaft gelöscht.", "This entry will be deleted permanently."),
            onConfirm = { viewModel.delete(entry); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}

// ---------- Abend-Eingabe ----------

@Composable
fun EveningEntryScreen(onClose: () -> Unit, viewModel: EveningEntryViewModel = viewModel()) {
    val draft by viewModel.draft.collectAsState()
    Box(modifier = Modifier.imePadding()) {
        ClearDetailScreen(title = tr("Heute", "Today"), onBack = onClose) {
            Text(
                tr(
                    "Alles ist freiwillig. Ein Stichwort reicht, auch winzige Dinge zählen.",
                    "Everything is optional. A keyword is enough, and even tiny things count."
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = draft.success,
                onValueChange = viewModel::onSuccessChange,
                label = { Text(tr("Was habe ich heute geschafft?", "What did I get done today?")) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Text(tr(
                "Was war heute gut oder okay?",
                "What was good or okay today?"
            ), style = MaterialTheme.typography.titleMedium)
            repeat(3) { index ->
                OutlinedTextField(
                    value = draft.goodTexts[index],
                    onValueChange = { viewModel.onGoodTextChange(index, it) },
                    label = { Text(tr("Gutes ${index + 1}", "Good thing ${index + 1}")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = draft.goodReasons[index],
                    onValueChange = { viewModel.onGoodReasonChange(index, it) },
                    label = { Text(tr("Warum ist das passiert? (optional)", "Why did it happen? (optional)")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            EditorButtons(onCancel = onClose, onSave = { viewModel.save(onClose) })
        }
    }
}

// ---------- Eintrag bearbeiten ----------

@Composable
fun EveningEditScreen(onClose: () -> Unit, viewModel: EveningEditViewModel = viewModel()) {
    val s by viewModel.state.collectAsState()
    Box(modifier = Modifier.imePadding()) {
        ClearDetailScreen(title = tr("Eintrag bearbeiten", "Edit entry"), onBack = onClose) {
            if (s.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                OutlinedTextField(
                    value = s.text,
                    onValueChange = viewModel::onTextChange,
                    label = {
                        Text(
                            if (s.type == EveningType.SUCCESS) tr("Was habe ich geschafft?", "What did I get done?")
                            else tr("Was war gut oder okay?", "What was good or okay?")
                        )
                    },
                    isError = s.textError,
                    supportingText = { if (s.textError) Text(tr(
                        "Bitte schreib etwas auf.",
                        "Please write something."
                    )) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                if (s.type == EveningType.GOOD) {
                    OutlinedTextField(
                        value = s.reason,
                        onValueChange = viewModel::onReasonChange,
                        label = { Text(tr("Warum ist das passiert? (optional)", "Why did it happen? (optional)")) },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                EditorButtons(onCancel = onClose, onSave = { viewModel.save(onClose) })
            }
        }
    }
}
