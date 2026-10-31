package com.example.clear.ui.settings

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.tr
import com.example.clear.ui.theme.ClearNoticeSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

private val weekdayLabels: List<String> get() = listOf(tr("Mo", "Mo"), tr("Di", "Tu"), tr("Mi", "We"), tr("Do", "Th"), tr("Fr", "Fr"), tr("Sa", "Sa"), tr("So", "Su"))

@Composable
fun MeditationReminderScreen(
    onBack: () -> Unit,
    viewModel: MeditationReminderViewModel = viewModel()
) = SimpleReminderScreen(
    title = tr("Meditations-Erinnerung", "Meditation reminder"),
    intro = tr(
        "Auf Wunsch erinnert Clear dich zur gewählten Zeit ans Meditieren. ",
        "If you like, Clear reminds you to meditate at the time you choose. "
    ) +
            tr("Du kannst das jederzeit ausschalten.", "You can turn this off at any time."),
    viewModel = viewModel,
    onBack = onBack
)

@Composable
fun CheckInReminderScreen(
    onBack: () -> Unit,
    viewModel: CheckInReminderViewModel = viewModel()
) = SimpleReminderScreen(
    title = tr("Check-in-Erinnerung", "Check-in reminder"),
    intro = tr(
        "Auf Wunsch erinnert Clear dich zur gewählten Zeit an deinen Check-in. ",
        "If you like, Clear reminds you of your check-in at the time you choose. "
    ) +
            tr(
                "Hast du an dem Tag schon eingecheckt, bleibt die Erinnerung aus.",
                "If you have already checked in that day, there is no reminder."
            ),
    viewModel = viewModel,
    onBack = onBack
)

@Composable
fun Phq9ReminderScreen(
    onBack: () -> Unit,
    viewModel: Phq9ReminderViewModel = viewModel()
) = SimpleReminderScreen(
    title = tr("PHQ-9-Erinnerung", "PHQ-9 reminder"),
    intro = tr(
        "Auf Wunsch erinnert Clear dich zur gewählten Zeit an den PHQ-9, sobald seit dem ",
        "If you like, Clear reminds you of the PHQ-9 at the time you choose, once at least two weeks "
    ) +
            tr(
                "letzten Ausfüllen mindestens zwei Wochen vergangen sind. Die Erinnerung kommt jeden Tag, " +
                    "bis du ihn ausgefüllt hast.",
                "have passed since you last filled it in. The reminder comes every day " +
                    "until you have filled it in."
            ),
    viewModel = viewModel,
    onBack = onBack
)

@Composable
fun Who5ReminderScreen(
    onBack: () -> Unit,
    viewModel: Who5ReminderViewModel = viewModel()
) = SimpleReminderScreen(
    title = tr("WHO-5-Erinnerung", "WHO-5 reminder"),
    intro = tr(
        "Auf Wunsch erinnert Clear dich zur gewählten Zeit an den WHO-5, sobald seit dem ",
        "If you like, Clear reminds you of the WHO-5 at the time you choose, once at least two weeks "
    ) +
            tr(
                "letzten Ausfüllen mindestens zwei Wochen vergangen sind. Die Erinnerung kommt jeden Tag, " +
                    "bis du ihn ausgefüllt hast.",
                "have passed since you last filled it in. The reminder comes every day " +
                    "until you have filled it in."
            ),
    viewModel = viewModel,
    onBack = onBack
)

@Composable
fun WsasReminderScreen(
    onBack: () -> Unit,
    viewModel: WsasReminderViewModel = viewModel()
) = SimpleReminderScreen(
    title = tr("WSAS-Erinnerung", "WSAS reminder"),
    intro = tr(
        "Auf Wunsch erinnert Clear dich zur gewählten Zeit an die WSAS, sobald seit dem ",
        "If you like, Clear reminds you of the WSAS at the time you choose, once at least two weeks "
    ) +
            tr(
                "letzten Ausfüllen mindestens zwei Wochen vergangen sind. Die Erinnerung kommt jeden Tag, " +
                    "bis du sie ausgefüllt hast.",
                "have passed since you last filled it in. The reminder comes every day " +
                    "until you have filled it in."
            ),
    viewModel = viewModel,
    onBack = onBack
)

@Composable
fun EveningReminderScreen(
    onBack: () -> Unit,
    viewModel: EveningReminderViewModel = viewModel()
) = SimpleReminderScreen(
    title = tr("Abend-Erinnerung", "Evening reminder"),
    intro = tr(
        "Auf Wunsch erinnert Clear dich am Abend an Erfolge und gute Dinge. ",
        "If you like, Clear reminds you in the evening about successes and good things. "
    ) +
            tr(
                "Hast du an dem Tag schon etwas eingetragen, bleibt die Erinnerung aus.",
                "If you have already added something that day, there is no reminder."
            ),
    viewModel = viewModel,
    onBack = onBack
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SimpleReminderScreen(
    title: String,
    intro: String,
    viewModel: SimpleReminderViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    var permissionDenied by remember { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            permissionDenied = !granted
            if (granted) viewModel.setEnabled(true)
        }

    fun onSwitch(checked: Boolean) {
        if (!checked) {
            viewModel.setEnabled(false)
            return
        }
        val needsPermission = Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
        if (needsPermission) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        else viewModel.setEnabled(true)
    }

    ClearDetailScreen(title = title, onBack = onBack) {
        ClearCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(
                        value = settings.enabled,
                        role = Role.Switch,
                        onValueChange = { onSwitch(it) }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(tr("Erinnerung aktivieren", "Turn on reminder"), style = MaterialTheme.typography.titleMedium)
                Switch(checked = settings.enabled, onCheckedChange = null)
            }
        }

        if (permissionDenied) {
            ClearCard(containerColor = ClearNoticeSoft) {
                Text(
                    tr(
                        "Ohne Erlaubnis kann Clear keine Erinnerungen senden. " +
                            "Du kannst sie später in den Systemeinstellungen erteilen.",
                        "Without permission, Clear can't send reminders. " +
                            "You can grant it later in the system settings."
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        if (settings.enabled && !NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            ClearCard(containerColor = ClearNoticeSoft) {
                Text(
                    tr(
                        "Benachrichtigungen sind für Clear in den Systemeinstellungen ausgeschaltet. " +
                            "Du bekommst deshalb keine Erinnerungen.",
                        "Notifications for Clear are turned off in the system settings. " +
                            "So you won't get any reminders."
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Text(tr("Uhrzeit", "Time"), style = MaterialTheme.typography.titleMedium)
        OutlinedButton(
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute -> viewModel.setTime(hour, minute) },
                    settings.hour, settings.minute, true
                ).show()
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text(tr("%02d:%02d Uhr", "%02d:%02d").format(settings.hour, settings.minute)) }

        Text(tr("Wochentage", "Weekdays"), style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
            weekdayLabels.forEachIndexed { index, label ->
                FilterChip(
                    selected = settings.daysMask and (1 shl index) != 0,
                    onClick = { viewModel.toggleDay(index) },
                    label = { Text(label) }
                )
            }
        }
    }
}
