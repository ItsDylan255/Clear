package com.example.clear.ui.anker

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
import com.example.clear.ui.akut.ClearWarningSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

private val weekdayLabels = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

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

    ClearDetailScreen(title = "Zitat-Erinnerungen", onBack = onBack) {
        Text(
            "Auf Wunsch zeigt Clear dir zur gewählten Zeit eines deiner ausgewählten Zitate. " +
                "Du kannst das jederzeit ausschalten.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

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
                Text("Erinnerungen aktivieren", style = MaterialTheme.typography.titleMedium)
                Switch(checked = settings.enabled, onCheckedChange = null)
            }
        }

        if (permissionDenied) {
            ClearCard(containerColor = ClearWarningSoft) {
                Text(
                    "Ohne Erlaubnis kann Clear keine Erinnerungen senden. " +
                        "Du kannst sie später in den Systemeinstellungen erteilen.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        if (settings.enabled && !NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            ClearCard(containerColor = ClearWarningSoft) {
                Text(
                    "Benachrichtigungen sind für Clear in den Systemeinstellungen ausgeschaltet. " +
                        "Du bekommst deshalb keine Erinnerungen.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Text("Uhrzeit", style = MaterialTheme.typography.titleMedium)
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
        ) { Text("%02d:%02d Uhr".format(settings.hour, settings.minute)) }
        Text(
            "Die Uhrzeit ist ungefähr. Android kann sie um einige Minuten verschieben.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text("Wochentage", style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
            weekdayLabels.forEachIndexed { index, label ->
                FilterChip(
                    selected = settings.daysMask and (1 shl index) != 0,
                    onClick = { viewModel.toggleDay(index) },
                    label = { Text(label) }
                )
            }
        }

        Text("Welche Zitate?", style = MaterialTheme.typography.titleMedium)
        if (state.quotes.isEmpty()) {
            ClearCard {
                Text(
                    "Lege zuerst unter „Zitate und Gedanken“ ein Zitat an.",
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
                    "Wähle mindestens ein Zitat aus, sonst zeigt Clear nichts an.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
