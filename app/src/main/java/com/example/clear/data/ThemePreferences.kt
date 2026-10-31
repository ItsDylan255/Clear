package com.example.clear.data

import android.content.Context
import android.content.SharedPreferences
import com.example.clear.tr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(
    private val labelDe: String, private val labelEn: String,
    private val descriptionDe: String, private val descriptionEn: String
) {
    System("Wie dein Handy", "Same as your phone", "Folgt der Einstellung deines Handys", "Follows your phone's setting"),
    Light("Hell", "Light", "Warm und ruhig", "Warm and calm"),
    Dark("Dunkel", "Dark", "Schonend für die Augen am Abend", "Easy on the eyes in the evening");

    val label: String get() = tr(labelDe, labelEn)
    val description: String get() = tr(descriptionDe, descriptionEn)
}

/** Merkt sich die gewaehlte Darstellung (kleine Einstellung, keine Datenbank noetig). */
object ThemePreferences {
    private const val FILE = "clear_prefs"
    private const val KEY = "theme_mode"

    private var prefs: SharedPreferences? = null
    private val _mode = MutableStateFlow(ThemeMode.System)
    val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    /** Einmal beim App-Start aufrufen. */
    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        prefs = p
        _mode.value = ThemeMode.entries.firstOrNull { it.name == p.getString(KEY, null) } ?: ThemeMode.System
    }

    fun set(mode: ThemeMode) {
        prefs?.edit()?.putString(KEY, mode.name)?.apply()
        _mode.value = mode
    }

    fun reset() {
        prefs?.edit()?.clear()?.apply()
        _mode.value = ThemeMode.System
    }
}
