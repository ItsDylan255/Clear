package com.example.clear.ui.settings

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import com.example.clear.reminder.DoNotDisturb
import com.example.clear.tr
import com.example.clear.ui.theme.ClearNoticeSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen

/** Einstellung: Waehrend eine Idee laeuft, automatisch "Nicht stoeren". */
@Composable
fun FocusSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(DoNotDisturb.isEnabled(context)) }
    var hasAccess by remember { mutableStateOf(DoNotDisturb.hasAccess(context)) }

    // Kommt man aus den Systemeinstellungen zurueck, wird die Erlaubnis neu geprueft
    val accessLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            hasAccess = DoNotDisturb.hasAccess(context)
        }
    fun openAccessSettings() {
        accessLauncher.launch(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
    }

    ClearDetailScreen(title = tr("Nicht stören", "Do Not Disturb"), onBack = onBack) {
        ClearCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(
                        value = enabled,
                        role = Role.Switch,
                        onValueChange = { checked ->
                            enabled = checked
                            DoNotDisturb.setEnabled(context, checked)
                            if (checked && !hasAccess) openAccessSettings()
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(tr(
                    "Bei Ideen automatisch",
                    "Automatically during ideas"
                ), style = MaterialTheme.typography.titleMedium)
                Switch(checked = enabled, onCheckedChange = null)
            }
        }

        if (enabled && !hasAccess) {
            ClearCard(containerColor = ClearNoticeSoft) {
                Text(
                    tr(
                        "Dafür braucht Clear deine Erlaubnis in den Systemeinstellungen " +
                            "(Zugriff auf „Nicht stören“). Ohne sie bleibt dein Handy unverändert.",
                        "For this, Clear needs your permission in the system settings " +
                            "(access to “Do Not Disturb”). Without it, your phone stays unchanged."
                    ),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            OutlinedButton(
                onClick = { openAccessSettings() },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) { Text(tr("Zugriff erlauben", "Allow access")) }
        }
    }
}
