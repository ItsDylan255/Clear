package com.example.clear.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Anchor
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.clear.tr

/** Die fuenf Hauptbereiche. Routen sind stabil und aendern sich nicht. */
enum class MainTab(
    val route: String,
    private val labelDe: String,
    private val labelEn: String,
    val icon: ImageVector
) {
    CheckIn("check_in", "Check In", "Check In", Icons.Outlined.Eco),
    Meditieren("meditieren", "Meditieren", "Meditate", Icons.Outlined.SelfImprovement),
    Akut("akut", "Akut", "Acute", Icons.Outlined.Bolt),
    Anker("anker", "Mein Anker", "My Anchor", Icons.Outlined.Anchor),
    Einstellungen("einstellungen", "Einstellungen", "Settings", Icons.Outlined.Settings);

    val label: String get() = tr(labelDe, labelEn)
}
