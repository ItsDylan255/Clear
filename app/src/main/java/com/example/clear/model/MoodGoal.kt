package com.example.clear.model

/** Wohin dich Musik beim Weg "Meine Stimmung veraendern" begleiten kann. */
enum class MoodGoal(val id: String, val title: String) {
    Calm("calm", "Ruhe"),
    Hope("hope", "Hoffnung"),
    Energy("energy", "Energie");

    companion object {
        fun parse(stored: String): Set<MoodGoal> =
            stored.split(',').mapNotNull { id -> entries.firstOrNull { it.id == id.trim() } }.toSet()

        fun serialize(goals: Set<MoodGoal>): String =
            entries.filter { it in goals }.joinToString(",") { it.id }
    }
}
