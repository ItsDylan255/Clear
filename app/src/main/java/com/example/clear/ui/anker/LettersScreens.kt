package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.AppLanguage
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.EditorButtons
import com.example.clear.ui.components.EmotionTagDialog
import com.example.clear.ui.components.EmptyHintCard
import com.example.clear.ui.components.emotionTagsLabel
import com.example.clear.model.EmotionTag
import com.example.clear.tr
import java.text.DateFormat
import java.util.Date

private fun formatDate(timestamp: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, AppLanguage.locale).format(Date(timestamp))

@Composable
fun LettersScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    viewModel: LettersViewModel = viewModel()
) {
    val letters by viewModel.letters.collectAsState()
    val list = letters

    ClearDetailScreen(
        title = viewModel.emotionName?.let { tr("Briefe · $it", "Letters · $it") } ?: tr("Briefe an mich selbst", "Letters to myself"),
        onBack = onBack
    ) {
        Text(
            tr(
                "Schreib dir Worte, wenn es dir gut geht. Lies sie, wenn du sie brauchst.",
                "Write yourself words when you are doing well. Read them when you need them."
            ),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> EmptyHintCard()
            else -> list.forEach { letter ->
                ClearCard(onClick = { onOpen(letter.id) }) {
                    Text(letter.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        formatDate(letter.createdAt),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        AddButton(tr("Brief schreiben", "Write a letter"), onAdd)
    }
}

@Composable
fun LetterReaderScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: LetterReaderViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()
    val letter = s.letter
    var confirmDelete by remember { mutableStateOf(false) }

    ClearDetailScreen(title = letter?.title ?: "", onBack = onBack) {
        if (s.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else if (letter != null) {
            Text(
                formatDate(letter.createdAt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ClearCard { Text(letter.text, style = MaterialTheme.typography.bodyLarge) }
            OutlinedButton(
                onClick = { onEdit(letter.id) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) { Text(tr("Bearbeiten", "Edit")) }
            TextButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.heightIn(min = 48.dp)
            ) { Text(tr("Löschen", "Delete")) }
        }
    }

    if (confirmDelete && letter != null) {
        ConfirmDeleteDialog(
            title = tr("Brief löschen?", "Delete letter?"),
            message = tr(
                "„${letter.title}“ wird dauerhaft gelöscht.",
                "“${letter.title}” will be deleted permanently."
            ) + deleteNote(letter.emotionTags),
            onConfirm = {
                confirmDelete = false
                viewModel.delete(letter, onDone = onBack)
            },
            onDismiss = { confirmDelete = false }
        )
    }
}

@Composable
fun LetterEditorScreen(onClose: () -> Unit, viewModel: LetterEditorViewModel = viewModel()) {
    val s by viewModel.state.collectAsState()
    Box(modifier = Modifier.imePadding()) {
        ClearDetailScreen(
            title = if (s.isNew) tr("Neuer Brief", "New letter") else tr("Brief bearbeiten", "Edit letter"),
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
                OutlinedTextField(
                    value = s.text,
                    onValueChange = viewModel::onTextChange,
                    label = { Text(tr("Dein Brief", "Your letter")) },
                    isError = s.textError,
                    supportingText = { if (s.textError) Text(tr(
                        "Bitte schreib etwas auf.",
                        "Please write something."
                    )) },
                    minLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
                EditorButtons(onCancel = onClose, onSave = { viewModel.save(onClose) })
            }
        }
    }
}
