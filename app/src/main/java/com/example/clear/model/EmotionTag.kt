package com.example.clear.model

import com.example.clear.tr

/** Emotionen, denen du Zitate, Bilder und Musik zuordnen kannst. Entsprechen den Akut-Situationen. */
enum class EmotionTag(val id: String, private val titleDe: String, private val titleEn: String) {
    Anxiety("anxiety", "Angst", "Anxiety"),
    Panic("panic", "Panik", "Panic"),
    Anger("anger", "Wut", "Anger"),
    Rumination("rumination", "Grübeln", "Rumination"),
    Sadness("sadness", "Traurigkeit", "Sadness"),
    Overwhelm("overwhelm", "Überforderung", "Overwhelm"),
    Insomnia("insomnia", "Schlaflosigkeit", "Insomnia"),
    Loneliness("loneliness", "Einsamkeit", "Loneliness");

    val title: String get() = tr(titleDe, titleEn)

    companion object {
        /** "anxiety,panic" -> {Anxiety, Panic}. Unbekannte Eintraege werden ignoriert. */
        fun parse(stored: String): Set<EmotionTag> =
            stored.split(',').mapNotNull { id -> entries.firstOrNull { it.id == id.trim() } }.toSet()

        /**
         * Gehoert ein Eintrag zur gewaehlten Emotion? "unknown" (Allgemein) = Eintraege ohne
         * Emotion. null = alle.
         */
        fun matches(stored: String, emotionId: String?): Boolean = when (emotionId) {
            null -> true
            "unknown" -> parse(stored).isEmpty()
            else -> parse(stored).any { it.id == emotionId }
        }

        /** Neue Eintraege bekommen genau die gewaehlte Emotion. Allgemein = keine. */
        fun forNewEntry(emotionId: String?): String =
            entries.firstOrNull { it.id == emotionId }?.id ?: ""

        /** {Anxiety, Panic} -> "anxiety,panic". Leere Menge ergibt "". */
        fun serialize(tags: Set<EmotionTag>): String =
            entries.filter { it in tags }.joinToString(",") { it.id }
    }
}
