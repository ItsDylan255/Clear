package com.example.clear.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Eine gefuehrte Meditation. Alle dauern zehn Minuten.
 * `shortTitle` ist der kurze Name fuer die schmalen Kacheln der Uebersicht.
 */
data class Meditation(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val durationMinutes: Int = 10,
    val shortTitle: String = title
)

object Meditations {
    val all: List<Meditation> = listOf(
        Meditation("mindfulness", "Achtsamkeitsmeditation", "Den Moment bewusst wahrnehmen", Icons.Outlined.Spa, shortTitle = "Achtsamkeit"),
        Meditation("breath", "Atemmeditation", "Die Aufmerksamkeit zum Atem lenken", Icons.Outlined.Air, shortTitle = "Atem"),
        Meditation("bodyscan", "Body-Scan", "Den Körper bewusst wahrnehmen", Icons.Outlined.AccessibilityNew, shortTitle = "Body-Scan"),
        Meditation("self_compassion", "Selbstmitgefühlsmeditation", "Freundlicher mit dir selbst sein", Icons.Outlined.FavoriteBorder, shortTitle = "Selbstmitgefühl"),
        Meditation("sleep", "Schlafmeditation", "Zur Ruhe kommen", Icons.Outlined.Bedtime, shortTitle = "Schlaf"),
        Meditation("concentration", "Konzentrationsmeditation", "Die Aufmerksamkeit sammeln", Icons.Outlined.CenterFocusStrong, shortTitle = "Konzentration"),
        // Die ID "visualization" bleibt, damit die Audiodatei meditation_visualization weiter gefunden wird
        Meditation("visualization", "Vergebungsmeditation", "Loslassen und Frieden schließen", Icons.Outlined.VolunteerActivism, shortTitle = "Vergebung"),
        Meditation("mantra", "Mantra-Meditation", "Ein Wort oder einen Satz wiederholen", Icons.Outlined.RecordVoiceOver, shortTitle = "Mantra"),
        Meditation("walking", "Gehmeditation", "Bewusstes Gehen", Icons.Outlined.Park, shortTitle = "Gehen"),
        Meditation("gratitude", "Dankbarkeitsmeditation", "Wertschätzend wahrnehmen", Icons.Outlined.LocalFlorist, shortTitle = "Dankbarkeit")
    )

    fun fromId(id: String?): Meditation? = all.firstOrNull { it.id == id }
}
