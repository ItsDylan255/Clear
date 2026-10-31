package com.example.clear.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.clear.tr
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

    ClearDetailScreen(title = tr("Über Clear", "About Clear"), onBack = onBack) {
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text("Clear", style = MaterialTheme.typography.titleMedium)
            Text(tr(
                "Klarheit, wenn du sie brauchst.",
                "Clarity when you need it."
            ), style = MaterialTheme.typography.bodyLarge)
            if (version.isNotBlank()) {
                Text(
                    "Version $version",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ClearCard {
            Text(tr("Datenschutz", "Privacy"), style = MaterialTheme.typography.titleMedium)
            Text(
                tr(
                    "Clear speichert alles auf deinem Handy. Es gibt keine Konten, keine Cloud " +
                        "und keine Auswertung deiner Daten. Clear braucht keine Internetverbindung. " +
                        "Unter „Meine Daten“ kannst du alles jederzeit vollständig löschen.",
                    "Clear stores everything on your phone. There are no accounts, no cloud " +
                        "and no analysis of your data. Clear doesn't need an internet connection. " +
                        "Under “My data” you can delete everything completely at any time."
                ),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                tr(
                    "Nutzt du Clear auf einer Wear-OS-Uhr, bekommt die Uhr nur die Titel laufender " +
                        "Ideen, die Zeit deines letzten Check-ins und die eingestellte Sprache. Das geht " +
                        "meist direkt per Bluetooth. Ist die Uhr nur per WLAN oder Mobilfunk verbunden, " +
                        "leitet Google diese Daten verschlüsselt weiter.",
                    "If you use Clear on a Wear OS watch, the watch only receives the titles of running " +
                        "ideas, the time of your last check-in and your language setting. This usually " +
                        "goes directly over Bluetooth. If the watch is only connected via Wi-Fi or mobile " +
                        "data, Google relays this data encrypted."
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        ClearCard {
            Text(tr("Wichtig", "Important"), style = MaterialTheme.typography.titleMedium)
            Text(
                tr(
                    "Clear begleitet dich im Alltag. Es stellt keine Diagnosen und ersetzt keine " +
                        "ärztliche oder therapeutische Hilfe. Wenn es dir akut sehr schlecht geht, " +
                        "findest du unter „Akut“ die Notfallkarte mit dem Notruf 112 und der " +
                        "Telefonseelsorge.",
                    "Clear supports you in everyday life. It doesn't make diagnoses and doesn't replace " +
                        "medical or therapeutic help. If you are in acute distress, " +
                        "you'll find the emergency card under “Acute”, with the emergency number 112 and the German " +
                        "crisis line (Telefonseelsorge). Outside Germany, please use your local emergency number."
                ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
