package com.example.clear.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Die sechs Check-in-Bereiche. Die `id` ist stabil und wird spaeter
 * auch in der Datenbank verwendet. Reihenfolge = Reihenfolge im Check-in.
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
    General(
        "general", "Allgemein", "Dein allgemeines Wohlbefinden",
        "Wie geht es dir insgesamt?", "sehr schlecht", "sehr gut", Icons.Outlined.Favorite
    ),
    Energy(
        "energy", "Energie", "Antrieb und Kraft",
        "Wie viel Energie und Antrieb hast du?", "keine Energie", "viel Energie",
        Icons.Outlined.BatteryChargingFull
    ),
    Joy(
        "joy", "Freude & Interesse", "Freude an Dingen erleben",
        "Wie gut kannst du Freude empfinden oder Interesse an Dingen haben?",
        "gar nicht", "sehr gut", Icons.Outlined.WbSunny
    ),
    Sleep(
        "sleep", "Schlaf & Erholung", "Wie erholt du dich fühlst",
        "Wie erholt fühlst du dich?", "gar nicht erholt", "sehr erholt", Icons.Outlined.Bedtime
    ),
    DailyLife(
        "daily_life", "Alltag bewältigen", "Mit alltäglichen Aufgaben zurechtkommen",
        "Wie gut kommst du mit alltäglichen Aufgaben zurecht?",
        "sehr schwer", "sehr leicht", Icons.Outlined.Home
    ),
    Connection(
        "connection", "Verbundenheit", "Verbindung zu anderen Menschen",
        "Wie verbunden fühlst du dich mit anderen Menschen?",
        "sehr getrennt", "sehr verbunden", Icons.Outlined.People
    );

    val isGeneral: Boolean get() = this == General

    companion object {
        val specialized: List<CheckInCategory> get() = entries.filter { !it.isGeneral }
        fun fromId(id: String?): CheckInCategory? = entries.firstOrNull { it.id == id }
    }
}
