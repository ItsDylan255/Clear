package com.example.clear.navigation

/** Routen innerhalb des Check-In-Bereichs. */
object CheckInRoutes {
    const val START = "check_in_start"
    const val FLOW = "check_in_flow"
    const val CATEGORY = "check_in_category/{id}"
    const val EDITOR = "check_in_idea_editor/{categoryId}/{ideaId}"

    fun category(id: String) = "check_in_category/$id"

    /** ideaId = -1 bedeutet: neue Idee. */
    fun editor(categoryId: String, ideaId: Long = -1L) = "check_in_idea_editor/$categoryId/$ideaId"
}
