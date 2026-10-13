package com.example.clear.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clear.ui.theme.ClearSpacing

/** Drei-Punkte-Menue mit "Bearbeiten" und "Loeschen". */
@Composable
fun OptionsMenu(contentDescription: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { open = true }) {
            Icon(Icons.Filled.MoreVert, contentDescription = contentDescription)
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            DropdownMenuItem(text = { Text("Bearbeiten" ) }, onClick = { open = false; onEdit() })
            DropdownMenuItem(text = { Text("Löschen") }, onClick = { open = false; onDelete() })
        }
    }
}

/** Bestaetigung vor dem Loeschen. */
@Composable
fun ConfirmDeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Löschen") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}

/** Ruhiger Leer-Zustand. */
@Composable
fun EmptyHintCard() {
    ClearCard {
        Text(
            "Hier ist noch nichts gespeichert. Du kannst jederzeit etwas hinzufügen.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Grosser Button zum Hinzufuegen. */
@Composable
fun AddButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Text(text, modifier = Modifier.padding(start = ClearSpacing.small))
    }
}

/** "Abbrechen" und "Speichern" am Ende eines Editors. */
@Composable
fun EditorButtons(onCancel: () -> Unit, onSave: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(onClick = onCancel, modifier = Modifier.heightIn(min = 56.dp)) {
            Text("Abbrechen")
        }
        Button(
            onClick = onSave,
            modifier = Modifier.heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text("Speichern") }
    }
}
