package com.example.clear.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.clear.model.Situations
import com.example.clear.ui.anker.EmotionAnkerScreen
import com.example.clear.ui.anker.ImageViewScreen
import com.example.clear.ui.anker.LetterViewScreen
import com.example.clear.ui.anker.QuoteViewScreen
import com.example.clear.ui.anker.TrackViewScreen

/**
 * Routen der Mein-Anker-Ansicht einer Emotion samt Einzelansichten.
 * Die Ansicht wird von Akut und von Mein Anker aus geoeffnet. Jeder Bereich bekommt eigene
 * Routennamen (Praefix), damit die richtige Registerkarte markiert bleibt. Die Seiten selbst
 * sind in beiden Faellen dieselben.
 *
 * Die Einzelansichten bekommen die Reihenfolge der Elemente als "ids" (z. B. "12-5-9").
 * So kann man dort nach links und rechts zum naechsten Element wischen.
 */
class AnkerViewRoutes(private val prefix: String) {
    val viewPattern = "$prefix/{id}"
    val quotePattern = "${prefix}_zitat/{itemId}?ids={ids}"
    val imagePattern = "${prefix}_bild/{itemId}?ids={ids}"
    val trackPattern = "${prefix}_musik/{itemId}?ids={ids}"
    val letterPattern = "${prefix}_brief/{itemId}?ids={ids}"

    /** Einzelansichten: die untere Leiste ist dort ausgeblendet. */
    val singleViewPatterns: List<String>
        get() = listOf(quotePattern, imagePattern, trackPattern, letterPattern)

    fun view(situationId: String) = "$prefix/$situationId"
    fun quote(id: Long, ids: List<Long>) = "${prefix}_zitat/$id?ids=${ids.joinToString("-")}"
    fun image(id: Long, ids: List<Long>) = "${prefix}_bild/$id?ids=${ids.joinToString("-")}"
    fun track(id: Long, ids: List<Long>) = "${prefix}_musik/$id?ids=${ids.joinToString("-")}"
    fun letter(id: Long, ids: List<Long>) = "${prefix}_brief/$id?ids=${ids.joinToString("-")}"
}

fun NavGraphBuilder.ankerViewRoutes(nav: NavHostController, r: AnkerViewRoutes) {
    val itemArgs = listOf(
        navArgument("itemId") { type = NavType.LongType },
        navArgument("ids") {
            type = NavType.StringType
            defaultValue = ""
        }
    )
    composable(
        route = r.viewPattern,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { entry ->
        val situation = Situations.fromId(entry.arguments?.getString("id"))
        if (situation != null) {
            EmotionAnkerScreen(
                situation = situation,
                onBack = { nav.popBackStack() },
                onOpenQuote = { id, ids -> nav.navigate(r.quote(id, ids)) },
                onOpenImage = { id, ids -> nav.navigate(r.image(id, ids)) },
                onOpenTrack = { id, ids -> nav.navigate(r.track(id, ids)) },
                onOpenLetter = { id, ids -> nav.navigate(r.letter(id, ids)) }
            )
        }
    }
    composable(route = r.quotePattern, arguments = itemArgs) {
        QuoteViewScreen(onBack = { nav.popBackStack() })
    }
    composable(route = r.imagePattern, arguments = itemArgs) {
        ImageViewScreen(onBack = { nav.popBackStack() })
    }
    composable(route = r.trackPattern, arguments = itemArgs) {
        TrackViewScreen(onBack = { nav.popBackStack() })
    }
    composable(route = r.letterPattern, arguments = itemArgs) {
        LetterViewScreen(onBack = { nav.popBackStack() })
    }
}
