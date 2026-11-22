package com.example.clear.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Anchor
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.clear.tr

/** Die Reiter neben Home. Routen sind stabil und aendern sich nicht. */
enum class MainTab(
    val route: String,
    private val labelDe: String,
    private val labelEn: String,
    val icon: ImageVector
) {
    Meditieren("meditieren", "Meditieren", "Meditate", Icons.Outlined.SelfImprovement),
    Akut("akut", "Akut", "Acute", Icons.Outlined.Bolt),
    Anker("anker", "Mein Anker", "My Anchor", Icons.Outlined.Anchor);

    val label: String get() = tr(labelDe, labelEn)
}
