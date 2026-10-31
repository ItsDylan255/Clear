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
import androidx.compose.material3.OutlinedButton
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
import com.example.clear.model.EmotionTag
import com.example.clear.tr
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.EditorButtons
import com.example.clear.ui.components.EmotionTagDialog
import com.example.clear.ui.components.EmptyHintCard
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.components.emotionTagsLabel
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

    ClearDetailScreen(
        title = viewModel.emotionName?.let { tr("Zitate · $it", "Quotes · $it") } ?: tr("Zitate und Gedanken", "Quotes and thoughts"),
        onBack = onBack
    ) {
        Text(
            tr("Worte, die dir guttun.", "Words that do you good."),
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
                            contentDescription = tr("Optionen für dieses Zitat", "Options for this quote"),
                            onEdit = { onEdit(quote.id) },
                            onDelete = { toDelete = quote }
                        )
                    }
                }
            }
        }
        AddButton(tr("Zitat hinzufügen", "Add quote"), onAdd)
    }

    toDelete?.let { quote ->
        ConfirmDeleteDialog(
            title = tr("Zitat löschen?", "Delete quote?"),
            message = tr(
                "Dieses Zitat wird dauerhaft gelöscht.",
                "This quote will be deleted permanently."
            ) + deleteNote(quote.emotionTags),
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
            title = if (s.isNew) tr("Neues Zitat", "New quote") else tr("Zitat bearbeiten", "Edit quote"),
            onBack = onClose
        ) {
            if (s.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                OutlinedTextField(
                    value = s.text,
                    onValueChange = viewModel::onTextChange,
                    label = { Text(tr("Zitat oder Gedanke", "Quote or thought")) },
                    isError = s.textError,
                    supportingText = { if (s.textError) Text(tr(
                        "Bitte schreib etwas auf.",
                        "Please write something."
                    )) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = s.author,
                    onValueChange = viewModel::onAuthorChange,
                    label = { Text(tr("Von (optional)", "By (optional)")) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                EditorButtons(onCancel = onClose, onSave = { viewModel.save(onClose) })
            }
        }
    }
}
