package com.example.clear.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.clear.ui.components.ClearDetailScreen

@Composable
fun ReminderSettingsScreen(
    onBack: () -> Unit,
    onOpenQuoteReminders: () -> Unit,
    onOpenMeditationReminder: () -> Unit,
    onOpenCheckInReminder: () -> Unit
) {
    ClearDetailScreen(title = "Erinnerungen", onBack = onBack) {
        Text(
            "Erinnerungen sind immer freiwillig. Du kannst sie jederzeit ausschalten.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SettingsCard(
            Icons.Outlined.Eco, "Check-in-Erinnerung",
            "Ein sanfter Hinweis auf deinen Check-in", onOpenCheckInReminder
        )
        SettingsCard(
            Icons.Outlined.SelfImprovement, "Meditations-Erinnerung",
            "Ein sanfter Hinweis aufs Meditieren", onOpenMeditationReminder
        )
        SettingsCard(
            Icons.Outlined.FormatQuote, "Zitat-Erinnerungen",
            "Ein Zitat von dir zur gewählten Zeit", onOpenQuoteReminders
        )
    }
}