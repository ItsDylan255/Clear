package com.example.clear.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Anchor
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Die fuenf Hauptbereiche. Routen sind stabil und aendern sich nicht. */
enum class MainTab(val route: String, val label: String, val icon: ImageVector) {
    CheckIn("check_in", "Check In", Icons.Outlined.Eco),
    Meditieren("meditieren", "Meditieren", Icons.Outlined.SelfImprovement),
    Akut("akut", "Akut", Icons.Outlined.Bolt),
    Anker("anker", "Mein Anker", Icons.Outlined.Anchor),
    Einstellungen("einstellungen", "Einstellungen", Icons.Outlined.Settings)
}
