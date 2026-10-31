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
import com.example.clear.tr
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
    ClearDetailScreen(title = tr("Erinnerungen", "Reminders"), onBack = onBack) {
        SettingsCard(
            Icons.Outlined.Eco, tr("Check-in-Erinnerung", "Check-in reminder"),
            tr("Ein sanfter Hinweis auf deinen Check-in", "A gentle nudge for your check-in"), onOpenCheckInReminder
        )
        SettingsCard(
            Icons.Outlined.Info, tr("PHQ-9-Erinnerung", "PHQ-9 reminder"),
            tr(
                "Ein Hinweis, wenn der PHQ-9 wieder bereit ist",
                "A note when the PHQ-9 is ready again"
            ), onOpenPhqReminder
        )
        SettingsCard(
            Icons.Outlined.Info, tr("WHO-5-Erinnerung", "WHO-5 reminder"),
            tr(
                "Ein Hinweis, wenn der WHO-5 wieder bereit ist",
                "A note when the WHO-5 is ready again"
            ), onOpenWho5Reminder
        )
        SettingsCard(
            Icons.Outlined.Info, tr("WSAS-Erinnerung", "WSAS reminder"),
            tr(
                "Ein Hinweis, wenn die WSAS wieder bereit ist",
                "A note when the WSAS is ready again"
            ), onOpenWsasReminder
        )
        SettingsCard(
            Icons.Outlined.WbSunny, tr("Abend-Erinnerung", "Evening reminder"),
            tr(
                "Ein Hinweis auf Erfolge und gute Dinge",
                "A note about successes and good things"
            ), onOpenEveningReminder
        )
        SettingsCard(
            Icons.Outlined.SelfImprovement, tr("Meditations-Erinnerung", "Meditation reminder"),
            tr("Ein sanfter Hinweis aufs Meditieren", "A gentle nudge to meditate"), onOpenMeditationReminder
        )
        SettingsCard(
            Icons.Outlined.FormatQuote, tr("Zitat-Erinnerungen", "Quote reminders"),
            tr(
                "Ein Zitat von dir zur gewählten Zeit",
                "One of your quotes at the time you choose"
            ), onOpenQuoteReminders
        )
    }
}
