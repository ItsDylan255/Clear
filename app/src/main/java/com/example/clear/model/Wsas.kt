package com.example.clear.model

/**
 * Die WSAS (Work and Social Adjustment Scale): fuenf Fragen, je 0 bis 8 Punkte, Summe 0 bis 40.
 * Sie misst, wie stark deine Probleme dich im Alltag beeintraechtigen. Hoeher heisst: staerkere
 * Beeintraechtigung. Wortlaut sinngemaess in der Du-Form. Bitte gegen die Originalfassung pruefen.
 * Das ist keine Diagnose, nur eine Momentaufnahme.
 */
object Wsas {
    const val INTRO =
        "Wähle für jede Aussage, wie stark deine Probleme dich im Alltag beeinträchtigen."

    val questions = listOf(
        "Meine Fähigkeit zu arbeiten ist wegen meiner Probleme beeinträchtigt. " +
            "(Wenn du aus anderen Gründen nicht arbeitest, wähle 0.)",
        "Meine Haushaltsarbeit (Putzen, Einkaufen, Kochen, Aufräumen, Rechnungen bezahlen) " +
            "ist wegen meiner Probleme beeinträchtigt.",
        "Meine Freizeitaktivitäten mit anderen (zum Beispiel Ausgehen, Besuche, Feiern, Ausflüge) " +
            "sind wegen meiner Probleme beeinträchtigt.",
        "Meine Freizeitaktivitäten allein (zum Beispiel Lesen, Basteln, Sport, Spazierengehen) " +
            "sind wegen meiner Probleme beeinträchtigt.",
        "Meine Fähigkeit, enge Beziehungen zu führen und zu pflegen (Partner, Familie, Freunde), " +
            "ist wegen meiner Probleme beeinträchtigt."
    )

    val shortTitles = listOf(
        "Arbeit",
        "Haushalt",
        "Freizeit mit anderen",
        "Freizeit allein",
        "Beziehungen"
    )

    /** Beschriftung der Skala. */
    const val SCALE_HINT = "0 = gar nicht, 4 = deutlich, 8 = sehr stark"

    const val COUNT = 5
    const val MAX_ANSWER = 8
    const val MAX_TOTAL = 40

    fun band(total: Int): String = when {
        total <= 9 -> "Kaum Beeinträchtigung"
        total <= 20 -> "Deutliche Beeinträchtigung"
        else -> "Starke Beeinträchtigung"
    }
}
