package com.example.clear.ui.anker

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemindersScreen(onBack: () -> Unit, viewModel: RemindersViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val settings = state.settings
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

    ClearDetailScreen(title = tr("Zitat-Erinnerungen", "Quote reminders"), onBack = onBack) {
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
                Text(tr("Erinnerungen aktivieren", "Turn on reminders"), style = MaterialTheme.typography.titleMedium)
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

        ClearCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(
                        value = settings.randomWindow,
                        role = Role.Switch,
                        onValueChange = { viewModel.setRandomWindow(it) }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(tr("Zufällige Zeiten", "Random times"), style = MaterialTheme.typography.titleMedium)
                    Text(
                        tr(
                            "Innerhalb eines Zeitfensters, nicht immer zur selben Uhrzeit.",
                            "Within a time window, not always at the same time."
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = settings.randomWindow, onCheckedChange = null)
            }
        }

        if (settings.randomWindow) {
            Text(tr("Zeitfenster", "Time window"), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
            ) {
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute -> viewModel.setTime(hour, minute) },
                            settings.hour, settings.minute, true
                        ).show()
                    },
                    modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text(tr("Ab %02d:%02d", "From %02d:%02d").format(settings.hour, settings.minute)) }
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hour, minute -> viewModel.setEndTime(hour, minute) },
                            settings.endHour, settings.endMinute, true
                        ).show()
                    },
                    modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text(tr("Bis %02d:%02d", "Until %02d:%02d").format(settings.endHour, settings.endMinute)) }
            }
            if (settings.endHour * 60 + settings.endMinute <= settings.hour * 60 + settings.minute) {
                Text(
                    tr("Das Ende sollte nach dem Start liegen.", "The end should be after the start."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(tr(
                "Wie viele Erinnerungen pro Tag?",
                "How many reminders per day?"
            ), style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                (1..5).forEach { count ->
                    FilterChip(
                        selected = settings.timesPerDay == count,
                        onClick = { viewModel.setTimesPerDay(count) },
                        label = { Text("$count") }
                    )
                }
            }
        } else {
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
        }

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

        Text(tr("Welche Zitate?", "Which quotes?"), style = MaterialTheme.typography.titleMedium)
        if (state.quotes.isEmpty()) {
            ClearCard {
                Text(
                    tr(
                        "Lege zuerst unter „Zitate und Gedanken“ ein Zitat an.",
                        "First add a quote under “Quotes and thoughts”."
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            ClearCard {
                state.quotes.forEach { quote ->
                    val selected = quote.id in state.selectedIds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .toggleable(
                                value = selected,
                                role = Role.Checkbox,
                                onValueChange = { viewModel.togglePool(quote.id, it) }
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                    ) {
                        Checkbox(checked = selected, onCheckedChange = null)
                        Text(quote.text, style = MaterialTheme.typography.bodyLarge, maxLines = 3)
                    }
                }
            }
            if (settings.enabled && state.selectedIds.isEmpty()) {
                Text(
                    tr(
                        "Wähle mindestens ein Zitat aus, sonst zeigt Clear nichts an.",
                        "Choose at least one quote, otherwise Clear won't show anything."
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
