package com.example.clear.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSurfaceSoft

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
        } catch (e: Exception) {
            ""
        }
    }

    ClearDetailScreen(title = "Über Clear", onBack = onBack) {
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text("Clear", style = MaterialTheme.typography.titleMedium)
            Text("Klarheit, wenn du sie brauchst.", style = MaterialTheme.typography.bodyLarge)
            if (version.isNotBlank()) {
                Text(
                    "Version $version",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ClearCard {
            Text("Datenschutz", style = MaterialTheme.typography.titleMedium)
            Text(
                "Clear speichert alles auf deinem Handy. Es gibt keine Konten, keine Cloud " +
                    "und keine Auswertung deiner Daten. Clear braucht keine Internetverbindung. " +
                    "Unter „Meine Daten“ kannst du alles jederzeit vollständig löschen.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        ClearCard {
            Text("Wichtig", style = MaterialTheme.typography.titleMedium)
            Text(
                "Clear begleitet dich im Alltag. Es stellt keine Diagnosen und ersetzt keine " +
                    "ärztliche oder therapeutische Hilfe. Wenn es dir akut sehr schlecht geht, " +
                    "findest du unter „Akut“ die Notfallkarte mit dem Notruf 112 und der " +
                    "Telefonseelsorge.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
