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
import com.example.clear.tr

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
    val all: List<Meditation> get() = listOf(
        Meditation("mindfulness", tr("Achtsamkeitsmeditation", "Mindfulness meditation"), tr("Den Moment bewusst wahrnehmen", "Being aware of the present moment"), Icons.Outlined.Spa, shortTitle = tr("Achtsamkeit", "Mindfulness")),
        Meditation("breath", tr("Atemmeditation", "Breathing meditation"), tr("Die Aufmerksamkeit zum Atem lenken", "Bringing your attention to the breath"), Icons.Outlined.Air, shortTitle = tr("Atem", "Breath")),
        Meditation("bodyscan", tr("Body-Scan", "Body scan"), tr("Den Körper bewusst wahrnehmen", "Being aware of your body"), Icons.Outlined.AccessibilityNew, shortTitle = tr("Body-Scan", "Body scan")),
        Meditation("self_compassion", tr("Selbstmitgefühlsmeditation", "Self-compassion meditation"), tr("Freundlicher mit dir selbst sein", "Being kinder to yourself"), Icons.Outlined.FavoriteBorder, shortTitle = tr("Selbstmitgefühl", "Self-compassion")),
        Meditation("sleep", tr("Schlafmeditation", "Sleep meditation"), tr("Zur Ruhe kommen", "Settling down"), Icons.Outlined.Bedtime, shortTitle = tr("Schlaf", "Sleep")),
        Meditation("concentration", tr("Konzentrationsmeditation", "Concentration meditation"), tr("Die Aufmerksamkeit sammeln", "Gathering your attention"), Icons.Outlined.CenterFocusStrong, shortTitle = tr("Konzentration", "Concentration")),
        // Die ID "visualization" bleibt, damit die Audiodatei meditation_visualization weiter gefunden wird
        Meditation("visualization", tr("Vergebungsmeditation", "Forgiveness meditation"), tr("Loslassen und Frieden schließen", "Letting go and making peace"), Icons.Outlined.VolunteerActivism, shortTitle = tr("Vergebung", "Forgiveness")),
        Meditation("mantra", tr("Mantra-Meditation", "Mantra meditation"), tr("Ein Wort oder einen Satz wiederholen", "Repeating a word or a phrase"), Icons.Outlined.RecordVoiceOver, shortTitle = tr("Mantra", tr("Mantra", "Mantra"))),
        Meditation("walking", tr("Gehmeditation", "Walking meditation"), tr("Bewusstes Gehen", "Mindful walking"), Icons.Outlined.Park, shortTitle = tr("Gehen", "Walking")),
        Meditation("gratitude", tr("Dankbarkeitsmeditation", "Gratitude meditation"), tr("Wertschätzend wahrnehmen", "Noticing with appreciation"), Icons.Outlined.LocalFlorist, shortTitle = tr("Dankbarkeit", "Gratitude"))
    )

    fun fromId(id: String?): Meditation? = all.firstOrNull { it.id == id }
}
