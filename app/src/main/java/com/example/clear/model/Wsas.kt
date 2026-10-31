package com.example.clear.model

import com.example.clear.tr

/**
 * Die WSAS (Work and Social Adjustment Scale): fuenf Fragen, je 0 bis 8 Punkte, Summe 0 bis 40.
 * Sie misst, wie stark deine Probleme dich im Alltag beeintraechtigen. Hoeher heisst: staerkere
 * Beeintraechtigung. Wortlaut sinngemaess in der Du-Form. Bitte gegen die Originalfassung pruefen.
 * Das ist keine Diagnose, nur eine Momentaufnahme.
 */
object Wsas {
    val INTRO: String
        get() = tr(
            "Wähle für jede Aussage, wie stark deine Probleme dich im Alltag beeinträchtigen.",
            "For each statement, choose how much your problems affect your daily life."
        )

    val questions: List<String> get() = listOf(
        tr(
            "Meine Fähigkeit zu arbeiten ist wegen meiner Probleme beeinträchtigt. " +
                "(Wenn du aus anderen Gründen nicht arbeitest, wähle 0.)",
            "Because of my problems, my ability to work is impaired. " +
                "(If you don't work for other reasons, choose 0.)"
        ),
        tr(
            "Meine Haushaltsarbeit (Putzen, Einkaufen, Kochen, Aufräumen, Rechnungen bezahlen) " +
                "ist wegen meiner Probleme beeinträchtigt.",
            "Because of my problems, my home management (cleaning, tidying, shopping, cooking, paying bills) " +
                "is impaired."
        ),
        tr(
            "Meine Freizeitaktivitäten mit anderen (zum Beispiel Ausgehen, Besuche, Feiern, Ausflüge) " +
                "sind wegen meiner Probleme beeinträchtigt.",
            "Because of my problems, my social leisure activities (for example going out, visits, parties, outings) " +
                "are impaired."
        ),
        tr(
            "Meine Freizeitaktivitäten allein (zum Beispiel Lesen, Basteln, Sport, Spazierengehen) " +
                "sind wegen meiner Probleme beeinträchtigt.",
            "Because of my problems, my private leisure activities (for example reading, crafts, sports, walking) " +
                "are impaired."
        ),
        tr(
            "Meine Fähigkeit, enge Beziehungen zu führen und zu pflegen (Partner, Familie, Freunde), " +
                "ist wegen meiner Probleme beeinträchtigt.",
            "Because of my problems, my ability to form and maintain close relationships " +
                "(partner, family, friends) is impaired."
        )
    )

    val shortTitles: List<String> get() = listOf(
        tr("Arbeit", "Work"),
        tr("Haushalt", "Home management"),
        tr("Freizeit mit anderen", "Social leisure"),
        tr("Freizeit allein", "Private leisure"),
        tr("Beziehungen", "Relationships")
    )

    /** Beschriftung der Skala. */
    val SCALE_HINT: String
        get() = tr("0 = gar nicht, 4 = deutlich, 8 = sehr stark", "0 = not at all, 4 = definitely, 8 = very severely")

    const val COUNT = 5
    const val MAX_ANSWER = 8
    const val MAX_TOTAL = 40

    fun band(total: Int): String = when {
        total <= 9 -> tr("Kaum Beeinträchtigung", "Little impairment")
        total <= 20 -> tr("Deutliche Beeinträchtigung", "Significant impairment")
        else -> tr("Starke Beeinträchtigung", "Severe impairment")
    }
}
