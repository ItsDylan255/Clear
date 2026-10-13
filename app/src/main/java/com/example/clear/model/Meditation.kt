package com.example.clear.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.ui.graphics.vector.ImageVector

/** Eine gefuehrte Meditation. Alle dauern zehn Minuten. */
data class Meditation(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val durationMinutes: Int = 10
)

object Meditations {
    val all: List<Meditation> = listOf(
        Meditation("mindfulness", "Achtsamkeitsmeditation", "Den Moment bewusst wahrnehmen", Icons.Outlined.Spa),
        Meditation("breath", "Atemmeditation", "Die Aufmerksamkeit zum Atem lenken", Icons.Outlined.Air),
        Meditation("bodyscan", "Body-Scan", "Den Körper bewusst wahrnehmen", Icons.Outlined.AccessibilityNew),
        Meditation("self_compassion", "Selbstmitgefühlsmeditation", "Freundlicher mit dir selbst sein", Icons.Outlined.FavoriteBorder),
        Meditation("sleep", "Schlafmeditation", "Zur Ruhe kommen", Icons.Outlined.Bedtime),
        Meditation("concentration", "Konzentrationsmeditation", "Die Aufmerksamkeit sammeln", Icons.Outlined.CenterFocusStrong),
        Meditation("visualization", "Visualisierungsmeditation", "Mit inneren Bildern arbeiten", Icons.Outlined.Cloud),
        Meditation("mantra", "Mantra-Meditation", "Ein Wort oder einen Satz wiederholen", Icons.Outlined.RecordVoiceOver),
        Meditation("walking", "Gehmeditation", "Bewusstes Gehen", Icons.Outlined.Park),
        Meditation("gratitude", "Dankbarkeitsmeditation", "Wertschätzend wahrnehmen", Icons.Outlined.LocalFlorist)
    )

    fun fromId(id: String?): Meditation? = all.firstOrNull { it.id == id }
}
