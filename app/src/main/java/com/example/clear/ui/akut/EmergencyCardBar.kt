package com.example.clear.ui.akut

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import com.example.clear.tr
import com.example.clear.ui.components.ClearNavCard
import com.example.clear.ui.theme.ClearNoticeSoft

/** Notfallkarte fest unten auf den Akut-Seiten, damit sie in einer Krise immer sichtbar ist. */
@Composable
fun EmergencyCardBar(onClick: () -> Unit) {
    ClearNavCard(
        icon = Icons.Filled.Phone,
        title = tr("Notfallkarte", "Emergency card"),
        subtitle = tr("Person, Plan und Warnzeichen", "Contact, plan and warning signs"),
        onClick = onClick,
        containerColor = ClearNoticeSoft
    )
}
