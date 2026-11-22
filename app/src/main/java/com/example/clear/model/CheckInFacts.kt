package com.example.clear.model

import com.example.clear.AppLanguage

/** Eine Stufe eines Fakten-Feldes. Die `id` ist stabil und wird in der Datenbank gespeichert. */
class FactOption(val id: String, private val de: String, private val en: String) {
    val label: String get() = if (AppLanguage.isEnglish) en else de
}

/**
 * Teil 2 des Check-ins "Was habe ich gemacht?": Felder mit genau einer Stufe (oder leer).
 * Reihenfolge der Stufen = Reihenfolge der Chips. `aboutYesterday` = Abschnitt
 * "Letzte Nacht / gestern" (Schlaf, Alkohol), sonst "Heute".
 */
enum class CheckInFact(
    val id: String,
    private val titleDe: String,
    private val titleEn: String,
    val aboutYesterday: Boolean,
    val options: List<FactOption>
) {
    Sleep(
        "sleep", "Schlaf", "Sleep", true,
        listOf(
            FactOption("bad", "schlecht", "bad"),
            FactOption("okay", "okay", "okay"),
            FactOption("good", "gut", "good")
        )
    ),
    Alcohol(
        "alcohol", "Alkohol", "Alcohol", true,
        listOf(
            FactOption("sober", "nüchtern", "sober"),
            FactOption("tipsy", "angetrunken", "tipsy"),
            FactOption("drunk", "betrunken", "drunk"),
            FactOption("very_drunk", "sehr betrunken", "very drunk")
        )
    ),
    Movement(
        "movement", "Bewegung", "Movement", false,
        listOf(
            FactOption("none", "keine", "none"),
            FactOption("light", "leicht", "light"),
            FactOption("medium", "mittel", "medium"),
            FactOption("hard", "schwer", "hard")
        )
    ),
    Routine(
        "routine", "Tagesablauf", "Daily routine", false,
        listOf(
            FactOption("worked", "geklappt", "worked"),
            FactOption("partly", "teilweise", "partly"),
            FactOption("fell_apart", "zerflossen", "fell apart")
        )
    ),
    Contact(
        "contact", "Kontakt", "Contact", false,
        listOf(
            FactOption("none", "keiner", "none"),
            FactOption("texted", "geschrieben", "texted"),
            FactOption("called", "telefoniert", "called"),
            FactOption("met", "getroffen", "met up"),
            FactOption("activity", "unternommen", "did something")
        )
    ),
    Rest(
        "rest", "Ruhe", "Rest", false,
        listOf(
            FactOption("none", "keine", "none"),
            FactOption("little", "wenig", "little"),
            FactOption("enough", "genug", "enough")
        )
    );

    val title: String get() = if (AppLanguage.isEnglish) titleEn else titleDe

    fun option(id: String?): FactOption? = options.firstOrNull { it.id == id }
}

/** "Tag" im Faktenteil (Mehrfachauswahl). Die `id` ist stabil und wird gespeichert. */
enum class DayType(val id: String, private val de: String, private val en: String) {
    Uni("uni", "Uni", "Uni"),
    Work("work", "Arbeit", "Work"),
    Free("free", "Frei", "Day off"),
    Study("study", "Lerntag", "Study day"),
    Exam("exam", "Prüfung", "Exam"),
    Sick("sick", "Krank", "Sick"),
    Away("away", "Unterwegs", "Travelling");

    val label: String get() = if (AppLanguage.isEnglish) en else de

    companion object {
        fun fromId(id: String): DayType? = entries.firstOrNull { it.id == id }
    }
}
