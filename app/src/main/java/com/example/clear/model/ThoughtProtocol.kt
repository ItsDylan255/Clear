package com.example.clear.model

import com.example.clear.tr

/** Gefuehle im Gedankenprotokoll. */
enum class ThoughtEmotion(val id: String, private val titleDe: String, private val titleEn: String) {
    Fear("fear", "Angst", "Fear"),
    Sadness("sadness", "Traurigkeit", "Sadness"),
    Anger("anger", "Wut", "Anger"),
    Shame("shame", "Scham", "Shame"),
    Guilt("guilt", "Schuld", "Guilt"),
    Loneliness("loneliness", "Einsamkeit", "Loneliness"),
    Overwhelm("overwhelm", "Überforderung", "Overwhelm"),
    Other("other", "Etwas anderes", "Something else");

    val title: String get() = tr(titleDe, titleEn)

    companion object {
        fun fromId(id: String?): ThoughtEmotion? = entries.firstOrNull { it.id == id }
    }
}

/** Denkfallen mit je einem Satz Erklaerung. */
enum class ThinkingTrap(
    val id: String,
    private val titleDe: String, private val titleEn: String,
    private val explanationDe: String, private val explanationEn: String
) {
    AllOrNothing(
        "all_or_nothing", "Alles oder nichts", "All or nothing",
        "Du siehst nur Schwarz oder Weiß, zum Beispiel mit „nie“ oder „immer“.",
        "You only see black or white, for example with “never” or “always”."
    ),
    Catastrophizing(
        "catastrophizing", "Katastrophisieren", "Catastrophizing",
        "Du gehst vom schlimmsten möglichen Ausgang aus.",
        "You assume the worst possible outcome."
    ),
    MindReading(
        "mind_reading", "Gedankenlesen", "Mind reading",
        "Du glaubst zu wissen, was andere denken, zum Beispiel „Die denken bestimmt …“.",
        "You believe you know what others think, for example “They surely think …”."
    ),
    SelfDevaluation(
        "self_devaluation", "Selbstabwertung", "Putting yourself down",
        "Du bewertest dich als ganze Person schlecht, statt eine einzelne Situation zu sehen.",
        "You judge yourself badly as a whole person instead of looking at a single situation."
    ),
    EmotionalReasoning(
        "emotional_reasoning", "Gefühl als Beweis", "Feeling as proof",
        "„Ich fühle mich schlecht, also ist es so.“ Ein Gefühl ist noch kein Beweis.",
        "“I feel bad, so it must be true.” A feeling is not proof."
    );

    val title: String get() = tr(titleDe, titleEn)
    val explanation: String get() = tr(explanationDe, explanationEn)

    companion object {
        fun parse(stored: String): Set<ThinkingTrap> =
            stored.split(',').mapNotNull { id -> entries.firstOrNull { it.id == id.trim() } }.toSet()

        fun serialize(traps: Set<ThinkingTrap>): String =
            entries.filter { it in traps }.joinToString(",") { it.id }
    }
}

/** Die drei Hilfsfragen fuer die neue Sichtweise. Der Index wird gespeichert. */
object HelpQuestions {
    val all: List<String> get() = listOf(
        tr("Was würde ich einem Freund sagen?", "What would I say to a friend?"),
        tr("Was spricht dagegen?", "What speaks against it?"),
        tr("Wie sehe ich das in einer Woche?", "How will I see this in a week?")
    )

    /** Index der Frage "Was spricht dagegen?" (hier zeigt die App eigene Eintraege). */
    const val AGAINST = 1
}
