package com.example.clear.model

import com.example.clear.tr

/**
 * Der WHO-5 (Wohlbefindens-Index): fuenf Fragen, je 0 bis 5 Punkte, Summe 0 bis 25.
 * Mal 4 ergibt einen Wert von 0 bis 100. Hoeher heisst: besseres Wohlbefinden.
 * Wortlaut in der ueblichen deutschen Fassung, in der Du-Form. Bitte gegen die Originalfassung pruefen.
 * Das ist keine Diagnose, nur eine Momentaufnahme.
 */
object Who5 {
    val INTRO: String
        get() = tr(
            "Bitte wähle für jede Aussage aus, was am besten zu deinen letzten zwei Wochen passt.",
            "For each statement, please choose what fits your last two weeks best."
        )

    val questions: List<String> get() = listOf(
        tr(
            "In den letzten zwei Wochen war ich froh und guter Laune.",
            "Over the last two weeks I have felt cheerful and in good spirits."
        ),
        tr(
            "In den letzten zwei Wochen habe ich mich ruhig und entspannt gefühlt.",
            "Over the last two weeks I have felt calm and relaxed."
        ),
        tr(
            "In den letzten zwei Wochen habe ich mich energisch und aktiv gefühlt.",
            "Over the last two weeks I have felt active and vigorous."
        ),
        tr(
            "In den letzten zwei Wochen habe ich mich beim Aufwachen frisch und ausgeruht gefühlt.",
            "Over the last two weeks I woke up feeling fresh and rested."
        ),
        tr(
            "In den letzten zwei Wochen war mein Alltag voller Dinge, die mich interessieren.",
            "Over the last two weeks my daily life has been filled with things that interest me."
        )
    )

    /** Kurzbezeichnungen fuer Uebersicht und Verlauf. */
    val shortTitles: List<String> get() = listOf(
        tr("Gute Laune", "Good spirits"),
        tr("Ruhe und Entspannung", "Calm and relaxed"),
        tr("Energie und Aktivität", "Active and vigorous"),
        tr("Frisch und ausgeruht", "Fresh and rested"),
        tr("Interesse im Alltag", "Interest in daily life")
    )

    /** Antwortstufen. Der Index ist der Punktwert (0 bis 5). */
    val options: List<String> get() = listOf(
        tr("Zu keinem Zeitpunkt", "At no time"),
        tr("Ab und zu", "Some of the time"),
        tr("Etwas weniger als die Hälfte der Zeit", "Less than half of the time"),
        tr("Etwas mehr als die Hälfte der Zeit", "More than half of the time"),
        tr("Meistens", "Most of the time"),
        tr("Die ganze Zeit", "All of the time")
    )

    const val COUNT = 5
    const val MAX_RAW = 25
    const val MAX_PERCENT = 100

    /** Rohwert (0 bis 25) -> Wert von 0 bis 100. */
    fun percent(raw: Int): Int = raw * 4

    fun band(percent: Int): String = when {
        percent <= 28 -> tr("Sehr niedriges Wohlbefinden", "Very low well-being")
        percent <= 50 -> tr("Niedriges Wohlbefinden", "Low well-being")
        else -> tr("Eher gutes Wohlbefinden", "Fairly good well-being")
    }
}
