package com.example.clear.model

/** Gefuehle im Gedankenprotokoll. */
enum class ThoughtEmotion(val id: String, val title: String) {
    Fear("fear", "Angst"),
    Sadness("sadness", "Traurigkeit"),
    Anger("anger", "Wut"),
    Shame("shame", "Scham"),
    Guilt("guilt", "Schuld"),
    Loneliness("loneliness", "Einsamkeit"),
    Overwhelm("overwhelm", "Überforderung"),
    Other("other", "Etwas anderes");

    companion object {
        fun fromId(id: String?): ThoughtEmotion? = entries.firstOrNull { it.id == id }
    }
}

/** Denkfallen mit je einem Satz Erklaerung. */
enum class ThinkingTrap(val id: String, val title: String, val explanation: String) {
    AllOrNothing(
        "all_or_nothing", "Alles oder nichts",
        "Du siehst nur Schwarz oder Weiß, zum Beispiel mit „nie“ oder „immer“."
    ),
    Catastrophizing(
        "catastrophizing", "Katastrophisieren",
        "Du gehst vom schlimmsten möglichen Ausgang aus."
    ),
    MindReading(
        "mind_reading", "Gedankenlesen",
        "Du glaubst zu wissen, was andere denken, zum Beispiel „Die denken bestimmt …“."
    ),
    SelfDevaluation(
        "self_devaluation", "Selbstabwertung",
        "Du bewertest dich als ganze Person schlecht, statt eine einzelne Situation zu sehen."
    ),
    EmotionalReasoning(
        "emotional_reasoning", "Gefühl als Beweis",
        "„Ich fühle mich schlecht, also ist es so.“ Ein Gefühl ist noch kein Beweis."
    );

    companion object {
        fun parse(stored: String): Set<ThinkingTrap> =
            stored.split(',').mapNotNull { id -> entries.firstOrNull { it.id == id.trim() } }.toSet()

        fun serialize(traps: Set<ThinkingTrap>): String =
            entries.filter { it in traps }.joinToString(",") { it.id }
    }
}

/** Die drei Hilfsfragen fuer die neue Sichtweise. Der Index wird gespeichert. */
object HelpQuestions {
    val all = listOf(
        "Was würde ich einem Freund sagen?",
        "Was spricht dagegen?",
        "Wie sehe ich das in einer Woche?"
    )

    /** Index der Frage "Was spricht dagegen?" (hier zeigt die App eigene Eintraege). */
    const val AGAINST = 1
}
