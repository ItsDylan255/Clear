package com.example.clear.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.clear.data.ThemeMode
import com.example.clear.data.ThemePreferences
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

@Composable
fun AppearanceScreen(onBack: () -> Unit) {
    val mode by ThemePreferences.mode.collectAsState()

    ClearDetailScreen(title = "Darstellung", onBack = onBack) {
        Text(
            "Wähle, wie Clear aussehen soll.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        ThemeMode.entries.forEach { option ->
            ClearCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .selectable(
                            selected = mode == option,
                            role = Role.RadioButton,
                            onClick = { ThemePreferences.set(option) }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                ) {
                    RadioButton(selected = mode == option, onClick = null)
                    Column {
                        Text(option.label, style = MaterialTheme.typography.titleMedium)
                        Text(
                            option.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
