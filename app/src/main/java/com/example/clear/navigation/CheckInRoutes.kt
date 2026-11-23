package com.example.clear.navigation

/** Routen innerhalb des Check-In-Bereichs. */
object CheckInRoutes {
    const val START = "check_in_start"
    const val FLOW = "check_in_flow/{parts}"
    const val CATEGORY = "check_in_category/{id}"
    const val EDITOR = "check_in_idea_editor/{topicId}/{ideaId}"

    fun category(id: String) = "check_in_category/$id"

    /** Check-in mit den Teilen als Ziffern, z. B. "123" (alles) oder "2" (nur Teil 2). */
    fun flow(parts: String) = "check_in_flow/$parts"

    /** ideaId = -1 bedeutet: neue Idee. topicId = vorausgewaehltes Thema, -1 = "Ohne Thema". */
    fun editor(topicId: Long = -1L, ideaId: Long = -1L) = "check_in_idea_editor/$topicId/$ideaId"
}
