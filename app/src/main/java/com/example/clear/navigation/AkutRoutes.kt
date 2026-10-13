package com.example.clear.navigation

/** Routen innerhalb des Bereichs Akut. */
object AkutRoutes {
    const val START = "akut_start"
    const val SITUATION = "akut_situation/{id}"
    const val EXERCISE = "akut_exercise/{id}"
    const val EMERGENCY = "akut_emergency"

    fun situation(id: String) = "akut_situation/$id"
    fun exercise(id: String) = "akut_exercise/$id"
}
