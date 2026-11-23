package com.example.clear.domain

import com.example.clear.data.IdeaLevel
import com.example.clear.model.CheckInCategory

/**
 * Welche Fassung einer Idee vorgeschlagen wird. Reine Rechenlogik, ohne Android und ohne UI.
 *
 *  - Pro "Braucht"-Kategorie zaehlt ihr 14-Tage-Durchschnitt, bei mehreren der Durchschnitt davon.
 *    Kategorien ohne Werte in den 14 Tagen werden ausgelassen.
 *  - 7 bis 10: "wenn es dir normal geht", 4 bis unter 7: "wenn es sinkt",
 *    unter 4: "wenn es dir stark beeintraechtigt geht" (gleiche Grenzen wie das Level).
 *  - "Braucht" leer oder gar keine Werte: "wenn es sinkt".
 */
object IdeaSuggestion {

    fun suggest(needs: Set<CheckInCategory>, levels: Map<String, CategoryLevelInfo?>?): IdeaLevel {
        val averages = needs.mapNotNull { levels?.get(it.id)?.average }
        if (averages.isEmpty()) return IdeaLevel.Sinking
        return when (LevelCalculator.levelOf(averages.average())) {
            CategoryLevel.High -> IdeaLevel.Normal
            CategoryLevel.Medium -> IdeaLevel.Sinking
            CategoryLevel.Low -> IdeaLevel.Severe
        }
    }
}
