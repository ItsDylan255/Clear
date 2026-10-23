package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.theme.ClearSpacing

/** Meine Warnzeichen: einzelne Punkte, erreichbar ueber die Notfallkarte. */
@Composable
fun WarningSignsScreen(onBack: () -> Unit, viewModel: WarningSignsViewModel = viewModel()) {
    val items by viewModel.items.collectAsState()
    TextListScreen(
        title = "Meine Warnzeichen",
        intro = "Woran merkst du, dass es dir schlechter geht? Schreib jedes Warnzeichen als " +
            "einzelnen Punkt auf, zum Beispiel „Ich ziehe mich zurück“.",
        emptyText = "Hier ist noch nichts gespeichert. Du kannst jederzeit etwas hinzufügen.",
        addLabel = "Warnzeichen hinzufügen",
        editTitle = "Warnzeichen",
        deleteTitle = "Warnzeichen löschen?",
        items = items,
        onBack = onBack,
        onAdd = viewModel::add,
        onUpdate = viewModel::update,
        onDelete = viewModel::delete
    )
}

@Composable
private fun TextListScreen(
    title: String,
    intro: String,
    emptyText: String,
    addLabel: String,
    editTitle: String,
    deleteTitle: String,
    items: List<TextItem>?,
    onBack: () -> Unit,
    onAdd: (String) -> Unit,
    onUpdate: (Long, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    var adding by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<TextItem?>(null) }
    var toDelete by remember { mutableStateOf<TextItem?>(null) }

    ClearDetailScreen(title = title, onBack = onBack) {
        Text(
            intro,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        AddButton(addLabel) { adding = true }

        when {
            items == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            items.isEmpty() -> Text(
                emptyText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> items.forEach { item ->
                ClearCard(onClick = { editing = item }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                    ) {
                        Text(
                            item.text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f).padding(top = ClearSpacing.small)
                        )
                        OptionsMenu(
                            contentDescription = "Optionen für diesen Eintrag",
                            onEdit = { editing = item },
                            onDelete = { toDelete = item }
                        )
                    }
                }
            }
        }
    }

    if (adding) {
        TextEditDialog(
            title = "$editTitle hinzufügen",
            initial = "",
            onSave = { onAdd(it); adding = false },
            onDismiss = { adding = false }
        )
    }
    editing?.let { item ->
        TextEditDialog(
            title = "$editTitle bearbeiten",
            initial = item.text,
            onSave = { onUpdate(item.id, it); editing = null },
            onDismiss = { editing = null }
        )
    }
    toDelete?.let { item ->
        ConfirmDeleteDialog(
            title = deleteTitle,
            message = "Dieser Eintrag wird dauerhaft gelöscht.",
            onConfirm = { onDelete(item.id); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}

@Composable
private fun TextEditDialog(
    title: String,
    initial: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text) }, enabled = text.isNotBlank()) { Text("Speichern") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}
