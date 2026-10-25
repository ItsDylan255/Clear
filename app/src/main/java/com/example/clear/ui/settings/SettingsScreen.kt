package com.example.clear.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.HorizontalDivider
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

/** Einstellungen in drei Gruppen: Darstellung, Erinnerungen, Daten. */
@Composable
fun SettingsScreen(
    onOpenAppearance: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenFocus: () -> Unit,
    onOpenMyData: () -> Unit
) {
    ClearScreen(title = "Einstellungen") {
        ClearCard {
            SettingsRow(Icons.Outlined.Palette, "Darstellung", "Hell, Dunkel oder System", onOpenAppearance)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(
                Icons.Outlined.Notifications, "Erinnerungen",
                "Hinweise zu Check-in, Fragebögen und mehr", onOpenReminders
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(Icons.Outlined.Bedtime, "Nicht stören", "Bei Ideen automatisch ruhig stellen", onOpenFocus)
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SettingsRow(Icons.Outlined.Lock, "Meine Daten", "Verwalten und vollständig löschen", onOpenMyData)
        }
    }
}

/** Abschnitt mit kleiner Ueberschrift und einer gemeinsamen Karte fuer alle Zeilen. */
@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
        ClearCard(content = content)
    }
}

/** Eine Zeile mit rundem Symbol, Titel, kurzer Erklaerung und Pfeil. Ohne eigene Karte. */
@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: (() -> Unit)?,
    showArrow: Boolean = onClick != null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clip(MaterialTheme.shapes.small).clickable(onClick = onClick) else Modifier)
            .heightIn(min = 64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (showArrow) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Karte mit Icon, Titel, kurzer Erklaerung und Pfeil (zum Beispiel in der Liste der Erinnerungen). */
@Composable
fun SettingsCard(icon: ImageVector, title: String, description: String, onClick: (() -> Unit)?) {
    ClearCard(onClick = onClick) {
        SettingsRow(icon, title, description, onClick = null, showArrow = onClick != null)
    }
}
