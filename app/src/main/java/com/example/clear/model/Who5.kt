package com.example.clear.model

/**
 * Der WHO-5 (Wohlbefindens-Index): fuenf Fragen, je 0 bis 5 Punkte, Summe 0 bis 25.
 * Mal 4 ergibt einen Wert von 0 bis 100. Hoeher heisst: besseres Wohlbefinden.
 * Wortlaut in der ueblichen deutschen Fassung, in der Du-Form. Bitte gegen die Originalfassung pruefen.
 * Das ist keine Diagnose, nur eine Momentaufnahme.
 */
object Who5 {
    const val INTRO =
        "Bitte wähle für jede Aussage aus, was am besten zu deinen letzten zwei Wochen passt."

    val questions = listOf(
        "In den letzten zwei Wochen war ich froh und guter Laune.",
        "In den letzten zwei Wochen habe ich mich ruhig und entspannt gefühlt.",
        "In den letzten zwei Wochen habe ich mich energisch und aktiv gefühlt.",
        "In den letzten zwei Wochen habe ich mich beim Aufwachen frisch und ausgeruht gefühlt.",
        "In den letzten zwei Wochen war mein Alltag voller Dinge, die mich interessieren."
    )

    /** Kurzbezeichnungen fuer Uebersicht und Verlauf. */
    val shortTitles = listOf(
        "Gute Laune",
        "Ruhe und Entspannung",
        "Energie und Aktivität",
        "Frisch und ausgeruht",
        "Interesse im Alltag"
    )

    /** Antwortstufen. Der Index ist der Punktwert (0 bis 5). */
    val options = listOf(
        "Zu keinem Zeitpunkt",
        "Ab und zu",
        "Etwas weniger als die Hälfte der Zeit",
        "Etwas mehr als die Hälfte der Zeit",
        "Meistens",
        "Die ganze Zeit"
    )

    const val COUNT = 5
    const val MAX_RAW = 25
    const val MAX_PERCENT = 100

    /** Rohwert (0 bis 25) -> Wert von 0 bis 100. */
    fun percent(raw: Int): Int = raw * 4

    fun band(percent: Int): String = when {
        percent <= 28 -> "Sehr niedriges Wohlbefinden"
        percent <= 50 -> "Niedriges Wohlbefinden"
        else -> "Eher gutes Wohlbefinden"
    }
}
