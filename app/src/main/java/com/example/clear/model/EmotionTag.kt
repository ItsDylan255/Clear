package com.example.clear.model

/** Emotionen, denen du Zitate, Bilder und Musik zuordnen kannst. Entsprechen den Akut-Situationen. */
enum class EmotionTag(val id: String, val title: String) {
    Anxiety("anxiety", "Angst"),
    Panic("panic", "Panik"),
    Anger("anger", "Wut"),
    Rumination("rumination", "Grübeln"),
    Sadness("sadness", "Traurigkeit"),
    Overwhelm("overwhelm", "Überforderung"),
    Insomnia("insomnia", "Schlaflosigkeit"),
    Loneliness("loneliness", "Einsamkeit");

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
