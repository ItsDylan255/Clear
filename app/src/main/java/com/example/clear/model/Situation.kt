package com.example.clear.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.ui.graphics.vector.ImageVector

data class Exercise(
    val id: String,
    val title: String,
    val duration: String,          // z. B. "1–3 Min."
    val explanation: String,
    val steps: List<String>
)

data class Situation(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val exercises: List<Exercise>,   // genau drei
    val intro: String? = null,       // ruhiger Satz oben auf der Seite
    val hint: String? = null         // Hinweiskarte (z. B. medizinische Hilfe)
)

/**
 * ALLE Inhalte des Akut-Bereichs stehen in dieser Datei.
 * Um eine Uebung auszuarbeiten, ersetze unten `ex(...)` durch ein volles `Exercise(...)`.
 * Als Vorlage dient die Uebung "5–4–3–2–1".
 */
object Situations {

    // Platzhalter, bis die Uebung ausgearbeitet ist
    private fun ex(id: String, title: String, duration: String) = Exercise(
        id = id,
        title = title,
        duration = duration,
        explanation = "Die Anleitung für diese Übung wird noch ausgearbeitet.",
        steps = listOf("Hier erscheinen bald die einzelnen Schritte dieser Übung.")
    )

    // Vorlage: so sieht eine fertige Uebung aus
    private val fiveFourThreeTwoOne = Exercise(
        id = "anxiety_54321",
        title = "5–4–3–2–1",
        duration = "2–3 Min.",
        explanation = "Du richtest deine Aufmerksamkeit nacheinander auf deine Sinne. " +
            "Das kann helfen, im Hier und Jetzt anzukommen.",
        steps = listOf(
            "Atme einmal ruhig ein und aus.",
            "Suche 5 Dinge, die du sehen kannst.",
            "Suche 4 Dinge, die du spüren kannst, zum Beispiel deine Kleidung oder den Stuhl.",
            "Suche 3 Dinge, die du hören kannst.",
            "Suche 2 Dinge, die du riechen kannst.",
            "Suche 1 Ding, das du schmecken kannst.",
            "Atme noch einmal ruhig durch. Du kannst jederzeit aufhören."
        )
    )
    private val grounding = ex("anxiety_grounding", "Boden unter den Füßen", "1 Min.")
    private val sounds = ex("insomnia_sounds", "Ruhige Geräusche", "2–5 Min.")

    val all: List<Situation> = listOf(
        Situation(
            "anxiety", "Angst", Icons.Outlined.Shield,
            listOf(
                grounding,
                fiveFourThreeTwoOne,
                ex("anxiety_safety", "Moment der Geborgenheit", "2 Min.")
            )
        ),
        Situation(
            "panic", "Panik", Icons.Outlined.Waves,
            listOf(
                ex("panic_here_now", "Hier und jetzt", "1 Min."),
                ex("panic_pressure", "Druck und Halt", "1–2 Min."),
                ex("panic_breath", "Atem kommen und gehen lassen", "1–3 Min.")
            ),
            hint = "Wenn du Brustschmerzen, Ohnmachtsgefühle oder schwere Atemnot hast, " +
                "ruf bitte den Notruf 112 an oder lass dich medizinisch abklären."
        ),
        Situation(
            "anger", "Wut", Icons.Outlined.LocalFireDepartment,
            listOf(
                ex("anger_move", "Spannung in Bewegung", "2 Min."),
                ex("anger_tense", "Anspannen und loslassen", "1–2 Min."),
                ex("anger_pause", "Reaktionspause", "1 Min.")
            )
        ),
        Situation(
            "rumination", "Grübeln", Icons.Outlined.Psychology,
            listOf(
                ex("rumination_put_down", "Gedanken ablegen", "3 Min."),
                ex("rumination_name", "Gedanken benennen", "1–2 Min."),
                ex("rumination_return", "Zur Umgebung zurückkehren", "2 Min.")
            )
        ),
        Situation(
            "sadness", "Traurigkeit", Icons.Outlined.SentimentDissatisfied,
            listOf(
                ex("sadness_kind", "Freundlich zu mir selbst", "2 Min."),
                ex("sadness_light", "Kleiner Lichtblick", "2 Min."),
                ex("sadness_need", "Was brauche ich?", "1–3 Min.")
            )
        ),
        Situation(
            "overwhelm", "Überforderung", Icons.Outlined.Layers,
            listOf(
                ex("overwhelm_next_step", "Nur der nächste Schritt", "2 Min."),
                ex("overwhelm_pause", "Reizpause", "1–3 Min."),
                ex("overwhelm_body", "Ankommen im Körper", "1 Min.")
            )
        ),
        Situation(
            "insomnia", "Schlaflosigkeit", Icons.Outlined.NightsStay,
            listOf(
                ex("insomnia_day", "Den Tag loslassen", "3 Min."),
                ex("insomnia_bodyscan", "Sanfter Body-Scan", "3–5 Min."),
                sounds
            )
        ),
        Situation(
            "loneliness", "Einsamkeit", Icons.Outlined.PersonOutline,
            listOf(
                ex("loneliness_remember", "Verbindung erinnern", "2 Min."),
                ex("loneliness_company", "Mir selbst Gesellschaft", "2 Min."),
                ex("loneliness_small", "Kleine Verbindung", "1–3 Min.")
            )
        ),
        Situation(
            "unknown", "Ich weiß nicht genau", Icons.Outlined.Explore,
            listOf(
                fiveFourThreeTwoOne.copy(id = "unknown_look_around", title = "Schau dich um"),
                grounding.copy(id = "unknown_support", title = "Spüre, was dich trägt"),
                sounds.copy(id = "unknown_listen", title = "Hör einfach zu")
            ),
            intro = "Das ist okay. Du musst gerade nicht wissen, was los ist. " +
                "Wir können einfach mit dem Moment anfangen."
        )
    )

    fun fromId(id: String?): Situation? = all.firstOrNull { it.id == id }

    fun findExercise(id: String?): Exercise? =
        all.flatMap { it.exercises }.firstOrNull { it.id == id }
}
