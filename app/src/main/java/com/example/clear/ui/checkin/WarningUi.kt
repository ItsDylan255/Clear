package com.example.clear.ui.checkin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.clear.domain.CategoryLevel
import com.example.clear.domain.CategoryLevelInfo
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.theme.ClearLevelHigh
import com.example.clear.ui.theme.ClearLevelLow
import com.example.clear.ui.theme.ClearLevelMedium
import java.util.Locale

// Alle Level-Texte an einer Stelle. Ruhig, nicht diagnostisch.


/** Name des Levels. */
fun levelName(level: CategoryLevel): String = when (level) {
    CategoryLevel.Low -> "Niedrig"
    CategoryLevel.Medium -> "Mittel"
    CategoryLevel.High -> "Hoch"
}

/** Zustandstext, der zum Level passt (gleich wie im Ideen-Editor). */
fun levelPhrase(level: CategoryLevel): String = when (level) {
    CategoryLevel.Low -> "Wenn es dir stark beeinträchtigt geht"
    CategoryLevel.Medium -> "Wenn es sinkt"
    CategoryLevel.High -> "Wenn es dir normal geht"
}

fun formatAverage(value: Double): String = String.format(Locale.GERMANY, "%.1f", value)

/** Kurze Zeile auf der Bereichs-Karte. null = noch kein Wert in den letzten 14 Tagen. */
fun levelLabel(info: CategoryLevelInfo?): String? =
    info?.let { "${levelName(it.level)} · Ø ${formatAverage(it.average)}" }

/** Hinweis oben auf der Bereichs-Seite. Zeigt Level und Durchschnitt der letzten 14 Tage. */
@Composable
fun LevelNotice(info: CategoryLevelInfo?) {
    if (info == null) return
    ClearCard(
        containerColor = when (info.level) {
            CategoryLevel.Low -> ClearLevelLow
            CategoryLevel.Medium -> ClearLevelMedium
            CategoryLevel.High -> ClearLevelHigh
        }
    ) {
        Text(
            "Letzte 14 Tage: Ø ${formatAverage(info.average)} von 10 (${levelName(info.level).lowercase()})",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
