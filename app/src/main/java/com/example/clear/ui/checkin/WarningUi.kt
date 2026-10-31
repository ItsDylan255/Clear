package com.example.clear.ui.checkin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.clear.AppLanguage
import com.example.clear.domain.CategoryLevel
import com.example.clear.domain.CategoryLevelInfo
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.theme.ClearLevelHigh
import com.example.clear.ui.theme.ClearLevelLow
import com.example.clear.ui.theme.ClearLevelMedium

// Alle Level-Texte an einer Stelle. Ruhig, nicht diagnostisch.


/** Name des Levels. */
fun levelName(level: CategoryLevel): String = when (level) {
    CategoryLevel.Low -> tr("Niedrig", "Low")
    CategoryLevel.Medium -> tr("Mittel", "Medium")
    CategoryLevel.High -> tr("Hoch", "High")
}

/** Zustandstext, der zum Level passt (gleich wie im Ideen-Editor). */
fun levelPhrase(level: CategoryLevel): String = when (level) {
    CategoryLevel.Low -> tr("Wenn es dir stark beeinträchtigt geht", "When you are severely affected")
    CategoryLevel.Medium -> tr("Wenn es sinkt", "When it is sinking")
    CategoryLevel.High -> tr("Wenn es dir normal geht", "When you are doing normally")
}

fun formatAverage(value: Double): String = String.format(AppLanguage.locale, "%.1f", value)

/** Kurze Zeile auf der Bereichs-Karte. null = noch kein Wert in den letzten 14 Tagen. */
fun levelLabel(info: CategoryLevelInfo?): String? =
    info?.let { tr(
        "${levelName(it.level)} · Ø ${formatAverage(it.average)}",
        "${levelName(it.level)} · avg ${formatAverage(it.average)}"
    ) }

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
            tr(
                "Letzte 14 Tage: Ø ${formatAverage(info.average)} von 10 (${levelName(info.level).lowercase()})",
                "Last 14 days: avg ${formatAverage(info.average)} of 10 (${levelName(info.level).lowercase()})"
            ),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
