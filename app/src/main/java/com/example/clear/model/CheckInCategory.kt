package com.example.clear.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.clear.AppLanguage

/**
 * Die acht Check-in-Kategorien (Teil 1 "Wie geht es mir?"). Die `id` ist stabil und wird in der
 * Datenbank verwendet. Reihenfolge = Reihenfolge im Check-in. Skala 1 bis 10, 10 ist immer gut.
 */
enum class CheckInCategory(
    val id: String,
    private val de: Texts,
    private val en: Texts,
    val icon: ImageVector
) {
    Mood(
        "mood",
        Texts("Stimmung", "Wie war deine Stimmung heute?", "niedergeschlagen, hoffnungslos", "gut, zuversichtlich"),
        Texts("Mood", "How was your mood today?", "down, hopeless", "good, hopeful"),
        Icons.Outlined.Favorite
    ),
    Joy(
        "joy",
        Texts("Freude", "Wie viel Freude oder Interesse hast du heute gespürt?", "gar keine", "sehr viel"),
        Texts("Joy", "How much joy or interest did you feel today?", "none at all", "a lot"),
        Icons.Outlined.WbSunny
    ),
    Energy(
        "energy",
        Texts("Energie", "Wie viel Energie und Antrieb hattest du heute?", "komme kaum hoch", "voller Tatendrang"),
        Texts("Energy", "How much energy and drive did you have today?", "can barely get up", "full of drive"),
        Icons.Outlined.BatteryChargingFull
    ),
    Clarity(
        "clarity",
        Texts("Klarheit", "Wie gut konntest du dich heute konzentrieren?", "sehr vernebelt", "sehr klar"),
        Texts("Clarity", "How well could you concentrate today?", "very foggy", "very clear"),
        Icons.Outlined.Visibility
    ),
    Thoughts(
        "thoughts",
        Texts("Gedanken", "Wie bist du heute in Gedanken mit dir umgegangen?", "Grübeln, hart zu mir", "ruhig und freundlich"),
        Texts("Thoughts", "How did you treat yourself in your thoughts today?", "brooding, hard on myself", "calm and kind"),
        Icons.Outlined.Psychology
    ),
    Connection(
        "connection",
        Texts("Verbundenheit", "Wie verbunden hast du dich heute mit anderen gefühlt?", "völlig allein", "sehr verbunden"),
        Texts("Connection", "How connected did you feel to others today?", "completely alone", "very connected"),
        Icons.Outlined.People
    ),
    Calm(
        "calm",
        Texts("Gelassenheit", "Wie ruhig warst du heute innerlich?", "stark angespannt", "ganz gelassen"),
        Texts("Calm", "How calm were you inside today?", "very tense", "completely calm"),
        Icons.Outlined.Spa
    ),
    Satisfaction(
        "satisfaction",
        Texts("Zufriedenheit", "Wie zufrieden warst du heute mit dir?", "sehr unzufrieden", "sehr zufrieden"),
        Texts("Satisfaction", "How satisfied were you with yourself today?", "very dissatisfied", "very satisfied"),
        Icons.Outlined.SentimentSatisfied
    );

    /** Die Texte einer Kategorie in einer Sprache. */
    class Texts(
        val title: String,
        val question: String,
        val lowLabel: String,
        val highLabel: String
    )

    private val texts: Texts get() = if (AppLanguage.isEnglish) en else de
    val title: String get() = texts.title
    val question: String get() = texts.question
    /** Kurzbeschreibung unter dem Titel (Bereichsseite): die Frage der Kategorie. */
    val description: String get() = texts.question
    val lowLabel: String get() = texts.lowLabel
    val highLabel: String get() = texts.highLabel

    companion object {
        fun fromId(id: String?): CheckInCategory? = entries.firstOrNull { it.id == id }
    }
}
