package com.example.clear.model

/**
 * Der PHQ-9 (Patient Health Questionnaire): neun Fragen, je 0 bis 3 Punkte, Summe 0 bis 27.
 * Wortlaut in der ueblichen deutschen Fassung, in der Du-Form. Bitte gegen die Originalfassung pruefen.
 */
object Phq9 {
    const val INTRO =
        "Wie oft haben dich die folgenden Beschwerden in den letzten zwei Wochen beeinträchtigt?"

    val questions = listOf(
        "Wenig Interesse oder Freude an deinen Tätigkeiten",
        "Niedergeschlagenheit, Schwermut oder Hoffnungslosigkeit",
        "Schwierigkeiten, ein- oder durchzuschlafen, oder vermehrter Schlaf",
        "Müdigkeit oder das Gefühl, keine Energie zu haben",
        "Verminderter Appetit oder übermäßiges Bedürfnis zu essen",
        "Schlechte Meinung von dir selbst, das Gefühl, versagt oder dich oder deine Familie enttäuscht zu haben",
        "Schwierigkeiten, dich auf etwas zu konzentrieren, zum Beispiel beim Zeitunglesen oder Fernsehen",
        "Bewegungen oder Sprache so verlangsamt, dass es auch anderen auffallen würde – " +
            "oder im Gegenteil so zappelig oder ruhelos, dass du dich viel mehr bewegt hast als sonst",
        "Gedanken, dass du lieber tot wärst oder dir Leid zufügen möchtest"
    )

    /** Kurzbezeichnungen fuer Uebersicht und Verlauf. */
    val shortTitles = listOf(
        "Interesse und Freude",
        "Niedergeschlagenheit",
        "Schlaf",
        "Energie",
        "Appetit",
        "Selbstbild",
        "Konzentration",
        "Bewegung und Unruhe",
        "Gedanken an Tod oder Selbstverletzung"
    )

    /** Antwortstufen. Der Index ist der Punktwert. */
    val options = listOf(
        "Überhaupt nicht",
        "An einzelnen Tagen",
        "An mehr als der Hälfte der Tage",
        "Beinahe jeden Tag"
    )

    const val COUNT = 9
    const val MAX_TOTAL = 27

    fun band(total: Int): String = when {
        total <= 4 -> "Keine bis minimale Ausprägung"
        total <= 9 -> "Leichte Ausprägung"
        total <= 14 -> "Mittelgradige Ausprägung"
        total <= 19 -> "Mittelschwere Ausprägung"
        else -> "Schwere Ausprägung"
    }
}
