package com.example.clear.ui.checkin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.clear.domain.WarningState
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.akut.ClearWarningSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.theme.ClearSurfaceSoft

// Alle Warntexte an einer Stelle. Ruhig, respektvoll, nicht diagnostisch. Kein Druck, keine Versprechen.

const val GENERAL_WARNING_HINT =
    "Bei mehreren deiner Angaben zeigen sich Veränderungen. " +
        "Vielleicht ist ein guter Moment, deine persönlichen Strategien anzusehen."

/** Kurze Zeile auf der Kategorie-Karte. null = kein Hinweis. */
fun warningLabel(state: WarningState): String? = when (state) {
    WarningState.Normal -> null
    WarningState.Sinking -> "Es zeichnet sich eine Veränderung ab"
    WarningState.ContinuedLow -> "Liegt weiterhin unter deinem üblichen Bereich"
}

/** Text oben auf der Kategorie-Seite. null = kein Hinweis. */
fun warningMessage(category: CheckInCategory, state: WarningState): String? = when (state) {
    WarningState.Normal -> null
    WarningState.Sinking ->
        "Bei „${category.title}“ zeichnet sich in letzter Zeit eine Veränderung ab. " +
            "Deine Angaben liegen etwas unter deinem üblichen Bereich. " +
            "Vielleicht möchtest du deine persönlichen Ideen ansehen."
    WarningState.ContinuedLow ->
        "Bei „${category.title}“ liegen deine Angaben schon länger unter deinem üblichen Bereich. " +
            "Vielleicht ist ein guter Moment, deine persönlichen Ideen anzusehen."
}

/** Dezenter Hinweis. "Sinkend" ist neutral gruen, "Weiterhin niedrig" warm abgesetzt. */
@Composable
fun WarningNotice(category: CheckInCategory, state: WarningState) {
    val text = warningMessage(category, state) ?: return
    ClearCard(
        containerColor = if (state == WarningState.ContinuedLow) ClearWarningSoft else ClearSurfaceSoft
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}
