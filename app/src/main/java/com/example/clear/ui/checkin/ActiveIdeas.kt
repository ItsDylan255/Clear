package com.example.clear.ui.checkin

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Die Ideen, die gerade laufen. Mehrere gleichzeitig sind moeglich. Der Stand bleibt gespeichert,
 * auch wenn die App geschlossen wird. Zur Sicherheit endet eine Idee nach 24 Stunden von selbst.
 */
object ActiveIdeas {
    data class Running(val ideaId: Long, val startedAt: Long)

    private const val PREFS = "clear_active_ideas"
    private const val KEY_RUNNING = "running"
    private const val MAX_AGE_MS = 24 * 60 * 60 * 1000L

    private val _running = MutableStateFlow<Map<Long, Running>>(emptyMap())
    val running: StateFlow<Map<Long, Running>> = _running.asStateFlow()
    private var loaded = false

    /** Schluessel einer Idee fuer die gespeicherten Durchlaeufe. */
    fun key(ideaId: Long): String = "idea_$ideaId"

    fun isRunning(ideaId: Long): Boolean = _running.value.containsKey(ideaId)

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Liest laufende Ideen nach einem App-Neustart wieder ein. */
    fun load(context: Context) {
        if (loaded) return
        loaded = true
        val now = System.currentTimeMillis()
        val stored = prefs(context).getString(KEY_RUNNING, "").orEmpty()
        val map = stored.split(";").mapNotNull { part ->
            val pieces = part.split(":")
            val id = pieces.getOrNull(0)?.toLongOrNull()
            val started = pieces.getOrNull(1)?.toLongOrNull()
            if (id != null && started != null && now - started < MAX_AGE_MS) id to Running(id, started)
            else null
        }.toMap()
        _running.value = _running.value + map
        persist(context)
    }

    fun start(context: Context, ideaId: Long, startedAt: Long) {
        load(context)
        _running.value = _running.value + (ideaId to Running(ideaId, startedAt))
        persist(context)
    }

    fun end(context: Context, ideaId: Long) {
        load(context)
        _running.value = _running.value - ideaId
        persist(context)
    }

    private fun persist(context: Context) {
        val text = _running.value.values.joinToString(";") { "${it.ideaId}:${it.startedAt}" }
        prefs(context).edit().putString(KEY_RUNNING, text).apply()
    }
}
