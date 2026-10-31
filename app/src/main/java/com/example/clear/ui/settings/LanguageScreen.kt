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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.clear.AppLanguage
import com.example.clear.LanguageChoice
import com.example.clear.navigation.findActivity
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

/** Sprache der App: wie das Handy, Deutsch oder Englisch. */
@Composable
fun LanguageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(AppLanguage.choice(context)) }

    ClearDetailScreen(title = tr("Sprache", "Language"), onBack = onBack) {
        Text(
            tr("Wähle, in welcher Sprache Clear dich begleitet.", "Choose the language Clear uses."),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        LanguageChoice.entries.forEach { option ->
            ClearCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .selectable(
                            selected = selected == option,
                            role = Role.RadioButton,
                            onClick = {
                                if (selected != option) {
                                    selected = option
                                    context.findActivity()?.let { AppLanguage.set(it, option) }
                                }
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                ) {
                    RadioButton(selected = selected == option, onClick = null)
                    Column {
                        Text(
                            when (option) {
                                LanguageChoice.System -> tr("Wie dein Handy", "Same as your phone")
                                LanguageChoice.German -> "Deutsch"
                                LanguageChoice.English -> "English"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (option == LanguageChoice.System) {
                            Text(
                                tr(
                                    "Deutsch auf deutschen Handys, sonst Englisch",
                                    "German on German phones, otherwise English"
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        Text(
            tr(
                "Die Meditationen sind nur auf Deutsch gesprochen.",
                "The meditations are spoken in German only."
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
