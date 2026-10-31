package com.example.clear.model

import com.example.clear.tr

/**
 * Der PHQ-9 (Patient Health Questionnaire): neun Fragen, je 0 bis 3 Punkte, Summe 0 bis 27.
 * Wortlaut in der ueblichen deutschen Fassung, in der Du-Form. Bitte gegen die Originalfassung pruefen.
 */
object Phq9 {
    val INTRO: String
        get() = tr(
            "Wie oft haben dich die folgenden Beschwerden in den letzten zwei Wochen beeinträchtigt?",
            "Over the last two weeks, how often have you been bothered by any of the following problems?"
        )

    val questions: List<String> get() = listOf(
        tr("Wenig Interesse oder Freude an deinen Tätigkeiten", "Little interest or pleasure in doing things"),
        tr("Niedergeschlagenheit, Schwermut oder Hoffnungslosigkeit", "Feeling down, depressed, or hopeless"),
        tr(
            "Schwierigkeiten, ein- oder durchzuschlafen, oder vermehrter Schlaf",
            "Trouble falling or staying asleep, or sleeping too much"
        ),
        tr("Müdigkeit oder das Gefühl, keine Energie zu haben", "Feeling tired or having little energy"),
        tr("Verminderter Appetit oder übermäßiges Bedürfnis zu essen", "Poor appetite or overeating"),
        tr(
            "Schlechte Meinung von dir selbst, das Gefühl, versagt oder dich oder deine Familie enttäuscht zu haben",
            "Feeling bad about yourself – or that you are a failure or have let yourself or your family down"
        ),
        tr(
            "Schwierigkeiten, dich auf etwas zu konzentrieren, zum Beispiel beim Zeitunglesen oder Fernsehen",
            "Trouble concentrating on things, such as reading the newspaper or watching television"
        ),
        tr(
            "Bewegungen oder Sprache so verlangsamt, dass es auch anderen auffallen würde – " +
                "oder im Gegenteil so zappelig oder ruhelos, dass du dich viel mehr bewegt hast als sonst",
            "Moving or speaking so slowly that other people could have noticed – " +
                "or the opposite, being so fidgety or restless that you have been moving around a lot more than usual"
        ),
        tr(
            "Gedanken, dass du lieber tot wärst oder dir Leid zufügen möchtest",
            "Thoughts that you would be better off dead, or of hurting yourself in some way"
        )
    )

    /** Kurzbezeichnungen fuer Uebersicht und Verlauf. */
    val shortTitles: List<String> get() = listOf(
        tr("Interesse und Freude", "Interest and pleasure"),
        tr("Niedergeschlagenheit", "Feeling down"),
        tr("Schlaf", "Sleep"),
        tr("Energie", "Energy"),
        tr("Appetit", "Appetite"),
        tr("Selbstbild", "Self-image"),
        tr("Konzentration", "Concentration"),
        tr("Bewegung und Unruhe", "Movement and restlessness"),
        tr("Gedanken an Tod oder Selbstverletzung", "Thoughts of death or self-harm")
    )

    /** Antwortstufen. Der Index ist der Punktwert. */
    val options: List<String> get() = listOf(
        tr("Überhaupt nicht", "Not at all"),
        tr("An einzelnen Tagen", "Several days"),
        tr("An mehr als der Hälfte der Tage", "More than half the days"),
        tr("Beinahe jeden Tag", "Nearly every day")
    )

    const val COUNT = 9
    const val MAX_TOTAL = 27

    fun band(total: Int): String = when {
        total <= 4 -> tr("Keine bis minimale Ausprägung", "None to minimal")
        total <= 9 -> tr("Leichte Ausprägung", "Mild")
        total <= 14 -> tr("Mittelgradige Ausprägung", "Moderate")
        total <= 19 -> tr("Mittelschwere Ausprägung", "Moderately severe")
        else -> tr("Schwere Ausprägung", "Severe")
    }
}
