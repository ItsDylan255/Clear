package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.clear.data.Quote
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.EditorButtons
import com.example.clear.ui.components.EmptyHintCard
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.theme.ClearSpacing

@Composable
fun QuotesScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: QuotesViewModel = viewModel()
) {
    val quotes by viewModel.quotes.collectAsState()
    val list = quotes
    var toDelete by remember { mutableStateOf<Quote?>(null) }

    ClearDetailScreen(title = "Zitate und Gedanken", onBack = onBack) {
        Text(
            "Worte, die dir guttun.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> EmptyHintCard()
            else -> list.forEach { quote ->
                ClearCard(onClick = { onEdit(quote.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(top = ClearSpacing.small)) {
                            Text(quote.text, style = MaterialTheme.typography.bodyLarge)
                            if (!quote.author.isNullOrBlank()) {
                                Text(
                                    "– ${quote.author}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = ClearSpacing.small)
                                )
                            }
                        }
                        OptionsMenu(
                            contentDescription = "Optionen für dieses Zitat",
                            onEdit = { onEdit(quote.id) },
                            onDelete = { toDelete = quote }
                        )
                    }
                }
            }
        }
        AddButton("Zitat hinzufügen", onAdd)
    }

    toDelete?.let { quote ->
        ConfirmDeleteDialog(
            title = "Zitat löschen?",
            message = "Dieses Zitat wird dauerhaft gelöscht.",
            onConfirm = { viewModel.delete(quote); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}

@Composable
fun QuoteEditorScreen(onClose: () -> Unit, viewModel: QuoteEditorViewModel = viewModel()) {
    val s by viewModel.state.collectAsState()
    Box(modifier = Modifier.imePadding()) {
        ClearDetailScreen(
            title = if (s.isNew) "Neues Zitat" else "Zitat bearbeiten",
            onBack = onClose
        ) {
            if (s.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                OutlinedTextField(
                    value = s.text,
                    onValueChange = viewModel::onTextChange,
                    label = { Text("Zitat oder Gedanke") },
                    isError = s.textError,
                    supportingText = { if (s.textError) Text("Bitte schreib etwas auf.") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = s.author,
                    onValueChange = viewModel::onAuthorChange,
                    label = { Text("Von (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                EditorButtons(onCancel = onClose, onSave = { viewModel.save(onClose) })
            }
        }
    }
}
