package com.example.clear.model

/**
 * Wofuer ein Musikstueck gedacht ist. Genau eins von beiden, nie beides.
 * Gespeichert wird die id in MusicTrack.moodGoals (alte Werte wie "calm" gelten als "nicht gewaehlt").
 */
enum class MusicMode(val id: String, val title: String, val shortTitle: String) {
    Allow("allow", "Meine Gefühle zulassen", "Gefühle zulassen"),
    Change("change", "Meine Stimmung verändern", "Stimmung verändern");

    companion object {
        /** null = noch nicht gewaehlt (gilt dann fuer beide Wege). */
        fun parse(stored: String): MusicMode? = entries.firstOrNull { it.id == stored.trim() }
    }
}
