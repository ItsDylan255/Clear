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

        /** {Anxiety, Panic} -> "anxiety,panic". Leere Menge ergibt "". */
        fun serialize(tags: Set<EmotionTag>): String =
            entries.filter { it in tags }.joinToString(",") { it.id }
    }
}
