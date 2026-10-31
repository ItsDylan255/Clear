package com.example.clear.ui.akut

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clear.tr
import com.example.clear.ui.theme.isClearDark

/** Warmes Terrakotta fuer Hinweise rund um Hilfe. Nicht alarmierend. Passt sich dem Design an. */
val ClearWarningSoft: Color
    @Composable get() = if (isClearDark()) Color(0xFF4A3A32) else Color(0xFFF4E1D8)

/** Kleiner, ruhiger Link zur Notfallkarte. Steht auf den Akut-Seiten, immer 1 Tap entfernt. */
@Composable
fun EmergencyLink(onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.heightIn(min = 48.dp)) {
        Icon(Icons.Filled.Phone, contentDescription = null)
        Text(tr("Notfallkarte öffnen", "Open emergency card"), modifier = Modifier.padding(start = 8.dp))
    }
}
