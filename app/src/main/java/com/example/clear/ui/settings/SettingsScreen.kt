package com.example.clear.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

@Composable
fun SettingsScreen(
    onOpenAppearance: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenEmergency: () -> Unit,
    onOpenMyData: () -> Unit,
    onOpenAbout: () -> Unit
) {
    ClearScreen(title = "Einstellungen") {
        SettingsCard(Icons.Outlined.Palette, "Darstellung",
            "Farben und Erscheinungsbild", onOpenAppearance)
        SettingsCard(Icons.Outlined.Notifications, "Erinnerungen",
            "Freiwillige Check-in- und Zitat-Erinnerungen", onOpenReminders)
        SettingsCard(Icons.Outlined.Phone, "Notfallkarte",
            "Kontakte und persönlichen Plan verwalten", onOpenEmergency)
        SettingsCard(Icons.Outlined.Lock, "Meine Daten",
            "Datenverwaltung und vollständiges Löschen", onOpenMyData)
        SettingsCard(Icons.Outlined.Info, "Über Clear",
            "App-Version, Datenschutz und Informationen", onOpenAbout)
    }
}

/** Karte mit Icon, Titel, kurzer Erklaerung und Pfeil. onClick == null: kommt spaeter. */
@Composable
fun SettingsCard(icon: ImageVector, title: String, description: String, onClick: (() -> Unit)?) {
    ClearCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(ClearSurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = ClearTextDark)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onClick != null) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
