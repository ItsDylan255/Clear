package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.EditorButtons

/** Persoenliche Angaben der Notfallkarte bearbeiten. Alle Felder sind freiwillig. */
@Composable
fun EmergencyEditScreen(onClose: () -> Unit, viewModel: EmergencyEditViewModel = viewModel()) {
    val s by viewModel.state.collectAsState()
    Box(modifier = Modifier.imePadding()) {
        ClearDetailScreen(title = "Meine Notfallkarte", onBack = onClose) {
            Text(
                "Alles hier ist freiwillig und bleibt auf deinem Handy.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (s.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            } else {
                OutlinedTextField(
                    value = s.name, onValueChange = viewModel::onNameChange,
                    label = { Text("Name deiner Notfallperson") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = s.phone, onValueChange = viewModel::onPhoneChange,
                    label = { Text("Telefonnummer") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = s.note, onValueChange = viewModel::onNoteChange,
                    label = { Text("Hinweis (optional)") },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = s.plan, onValueChange = viewModel::onPlanChange,
                    label = { Text("Mein Plan") },
                    supportingText = {
                        Text("Zum Beispiel: Was hilft mir? Wen rufe ich an? Wo fühle ich mich sicher?")
                    },
                    minLines = 5, modifier = Modifier.fillMaxWidth()
                )
                EditorButtons(onCancel = onClose, onSave = { viewModel.save(onClose) })
            }
        }
    }
}
