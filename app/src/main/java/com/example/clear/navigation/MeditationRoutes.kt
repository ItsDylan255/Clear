package com.example.clear.navigation

/** Routen innerhalb des Bereichs Meditieren. */
object MeditationRoutes {
    const val START = "meditieren_start"
    const val PLAYER = "meditieren_player/{id}"
    fun player(id: String) = "meditieren_player/$id"
}
