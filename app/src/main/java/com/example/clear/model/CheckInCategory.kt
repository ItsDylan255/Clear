package com.example.clear.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.clear.AppLanguage

/**
 * Die acht Check-in-Bereiche. Die `id` ist stabil und wird in der Datenbank verwendet.
 * Reihenfolge = Reihenfolge im Check-in. Hoher Wert bedeutet immer: gut.
 */
enum class CheckInCategory(
    val id: String,
    private val de: Texts,
    private val en: Texts,
    val icon: ImageVector
) {
    Energy(
        "energy",
        Texts("Energie", "Energie und Müdigkeit", "Wie viel Energie hast du?", "keine Energie", "viel Energie"),
        Texts("Energy", "Energy and tiredness", "How much energy do you have?", "no energy", "lots of energy"),
        Icons.Outlined.BatteryChargingFull
    ),
    Clarity(
        "clarity",
        Texts(
            "Klarheit", "Konzentration und Gedächtnis",
            "Wie klar kannst du denken, dich konzentrieren und erinnern?", "sehr vernebelt", "sehr klar"
        ),
        Texts(
            "Clarity", "Concentration and memory",
            "How clearly can you think, concentrate and remember?", "very foggy", "very clear"
        ),
        Icons.Outlined.Visibility
    ),
    Recovery(
        "recovery",
        Texts("Erholung", "Schlaf und Erholung", "Wie erholt fühlst du dich?", "gar nicht erholt", "sehr erholt"),
        Texts("Recovery", "Sleep and recovery", "How rested do you feel?", "not rested at all", "very rested"),
        Icons.Outlined.Bedtime
    ),
    Joy(
        "joy",
        Texts("Freude", "Freude und Antrieb", "Wie viel Freude und Interesse erlebst du?", "gar keine", "sehr viel"),
        Texts("Joy", "Joy and drive", "How much joy and interest do you feel?", "none at all", "a lot"),
        Icons.Outlined.WbSunny
    ),
    Mood(
        "mood",
        Texts("Stimmung", "Stimmung und Hoffnung", "Wie ist deine Stimmung?", "sehr niedergeschlagen", "sehr gut"),
        Texts("Mood", "Mood and hope", "How is your mood?", "very low", "very good"),
        Icons.Outlined.Favorite
    ),
    Thoughts(
        "thoughts",
        Texts(
            "Gedanken", "Gedankenmuster und Grübeln",
            "Wie ruhig und freundlich sind deine Gedanken?", "sehr belastend", "sehr ruhig"
        ),
        Texts(
            "Thoughts", "Thought patterns and rumination",
            "How calm and kind are your thoughts?", "very distressing", "very calm"
        ),
        Icons.Outlined.Psychology
    ),
    Connection(
        "connection",
        Texts(
            "Verbundenheit", "Kontakt zu anderen Menschen",
            "Wie verbunden fühlst du dich mit anderen Menschen?", "sehr getrennt", "sehr verbunden"
        ),
        Texts(
            "Connection", "Contact with other people",
            "How connected do you feel to other people?", "very disconnected", "very connected"
        ),
        Icons.Outlined.People
    ),
    Structure(
        "structure",
        Texts("Struktur", "Tagesstruktur", "Wie gut gelingt dir deine Tagesstruktur?", "gar nicht", "sehr gut"),
        Texts("Structure", "Daily structure", "How well are you managing your daily structure?", "not at all", "very good"),
        Icons.Outlined.Home
    );

    /** Die Texte eines Bereichs in einer Sprache. */
    class Texts(
        val title: String,
        val description: String,
        val question: String,
        val lowLabel: String,
        val highLabel: String
    )

    private val texts: Texts get() = if (AppLanguage.isEnglish) en else de
    val title: String get() = texts.title
    val description: String get() = texts.description
    val question: String get() = texts.question
    val lowLabel: String get() = texts.lowLabel
    val highLabel: String get() = texts.highLabel

    companion object {
        fun fromId(id: String?): CheckInCategory? = entries.firstOrNull { it.id == id }
    }
}
