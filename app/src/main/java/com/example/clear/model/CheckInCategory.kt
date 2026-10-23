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

/**
 * Die acht Check-in-Bereiche. Die `id` ist stabil und wird in der Datenbank verwendet.
 * Reihenfolge = Reihenfolge im Check-in. Hoher Wert bedeutet immer: gut.
 */
enum class CheckInCategory(
    val id: String,
    val title: String,
    val description: String,
    val question: String,
    val lowLabel: String,
    val highLabel: String,
    val icon: ImageVector
) {
    Energy(
        "energy", "Energie", "Energie und Müdigkeit",
        "Wie viel Energie hast du?", "keine Energie", "viel Energie",
        Icons.Outlined.BatteryChargingFull
    ),
    Clarity(
        "clarity", "Klarheit", "Konzentration und Gedächtnis",
        "Wie klar kannst du denken, dich konzentrieren und erinnern?",
        "sehr vernebelt", "sehr klar", Icons.Outlined.Visibility
    ),
    Recovery(
        "recovery", "Erholung", "Schlaf und Erholung",
        "Wie erholt fühlst du dich?", "gar nicht erholt", "sehr erholt", Icons.Outlined.Bedtime
    ),
    Joy(
        "joy", "Freude", "Freude und Antrieb",
        "Wie viel Freude und Interesse erlebst du?", "gar keine", "sehr viel",
        Icons.Outlined.WbSunny
    ),
    Mood(
        "mood", "Stimmung", "Stimmung und Hoffnung",
        "Wie ist deine Stimmung?", "sehr niedergeschlagen", "sehr gut", Icons.Outlined.Favorite
    ),
    Thoughts(
        "thoughts", "Gedanken", "Gedankenmuster und Grübeln",
        "Wie ruhig und freundlich sind deine Gedanken?", "sehr belastend", "sehr ruhig",
        Icons.Outlined.Psychology
    ),
    Connection(
        "connection", "Verbundenheit", "Kontakt zu anderen Menschen",
        "Wie verbunden fühlst du dich mit anderen Menschen?",
        "sehr getrennt", "sehr verbunden", Icons.Outlined.People
    ),
    Structure(
        "structure", "Struktur", "Tagesstruktur",
        "Wie gut gelingt dir deine Tagesstruktur?", "gar nicht", "sehr gut", Icons.Outlined.Home
    );

    companion object {
        fun fromId(id: String?): CheckInCategory? = entries.firstOrNull { it.id == id }
    }
}
