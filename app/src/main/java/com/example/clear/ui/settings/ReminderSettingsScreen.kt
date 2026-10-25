package com.example.clear.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.clear.ui.components.ClearDetailScreen

@Composable
fun ReminderSettingsScreen(
    onBack: () -> Unit,
    onOpenQuoteReminders: () -> Unit,
    onOpenMeditationReminder: () -> Unit,
    onOpenCheckInReminder: () -> Unit,
    onOpenPhqReminder: () -> Unit,
    onOpenWho5Reminder: () -> Unit,
    onOpenWsasReminder: () -> Unit,
    onOpenEveningReminder: () -> Unit
) {
    ClearDetailScreen(title = "Erinnerungen", onBack = onBack) {
        SettingsCard(
            Icons.Outlined.Eco, "Check-in-Erinnerung",
            "Ein sanfter Hinweis auf deinen Check-in", onOpenCheckInReminder
        )
        SettingsCard(
            Icons.Outlined.Info, "PHQ-9-Erinnerung",
            "Ein Hinweis, wenn der PHQ-9 wieder bereit ist", onOpenPhqReminder
        )
        SettingsCard(
            Icons.Outlined.Info, "WHO-5-Erinnerung",
            "Ein Hinweis, wenn der WHO-5 wieder bereit ist", onOpenWho5Reminder
        )
        SettingsCard(
            Icons.Outlined.Info, "WSAS-Erinnerung",
            "Ein Hinweis, wenn die WSAS wieder bereit ist", onOpenWsasReminder
        )
        SettingsCard(
            Icons.Outlined.WbSunny, "Abend-Erinnerung",
            "Ein Hinweis auf Erfolge und gute Dinge", onOpenEveningReminder
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