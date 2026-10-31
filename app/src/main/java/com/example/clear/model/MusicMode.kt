package com.example.clear.model

import com.example.clear.tr

/**
 * Wofuer ein Musikstueck gedacht ist. Genau eins von beiden, nie beides.
 * Gespeichert wird die id in MusicTrack.moodGoals (alte Werte wie "calm" gelten als "nicht gewaehlt").
 */
enum class MusicMode(
    val id: String,
    private val titleDe: String, private val titleEn: String,
    private val shortTitleDe: String, private val shortTitleEn: String
) {
    Allow("allow", "Meine Gefühle zulassen", "Allow my feelings", "Gefühle zulassen", "Allow feelings"),
    Change("change", "Meine Stimmung verändern", "Change my mood", "Stimmung verändern", "Change mood");

    val title: String get() = tr(titleDe, titleEn)
    val shortTitle: String get() = tr(shortTitleDe, shortTitleEn)

    companion object {
        /** null = noch nicht gewaehlt (gilt dann fuer beide Wege). */
        fun parse(stored: String): MusicMode? = entries.firstOrNull { it.id == stored.trim() }
    }
}
