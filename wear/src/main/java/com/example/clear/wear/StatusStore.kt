package com.example.clear.wear

import android.content.Context
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.DataMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

data class RunningIdea(val id: Long, val title: String, val startedAt: Long)

/** Deutscher oder englischer Text, je nach Sprache in Clear. */
fun tr(de: String, en: String): String = if (StatusStore.isEnglish) en else de

/** Der Stand, den das Handy zuletzt geschickt hat. */
/** english: Sprache, die in Clear auf dem Handy eingestellt ist (null = noch unbekannt). */
data class WatchStatus(val ideas: List<RunningIdea>, val lastCheckIn: Long, val english: Boolean? = null) {
    fun checkedInToday(zone: ZoneId = ZoneId.systemDefault()): Boolean =
        lastCheckIn > 0L &&
            Instant.ofEpochMilli(lastCheckIn).atZone(zone).toLocalDate() == LocalDate.now(zone)

    companion object {
        fun from(map: DataMap) = WatchStatus(
            ideas = map.getDataMapArrayList("ideas").orEmpty().map {
                RunningIdea(it.getLong("id"), it.getString("title").orEmpty(), it.getLong("startedAt"))
            },
            lastCheckIn = map.getLong("lastCheckIn"),
            english = map.getString("lang")?.let { it == "en" }
        )
    }
}

/**
 * Haelt den Stand auf der Uhr, damit App und Kachel ihn auch ohne Verbindung zeigen koennen.
 * null = die Uhr hat vom Handy noch nichts bekommen.
 */
object StatusStore {
    private const val PREFS = "clear_watch"
    private const val KEY_STATUS = "status"

    private val _status = MutableStateFlow<WatchStatus?>(null)
    val status: StateFlow<WatchStatus?> = _status.asStateFlow()
    private var loaded = false

    fun load(context: Context): WatchStatus? {
        if (!loaded) {
            loaded = true
            val text = prefs(context).getString(KEY_STATUS, null)
            if (text != null) _status.value = runCatching { decode(text) }.getOrNull()
        }
        return _status.value
    }

    fun save(context: Context, status: WatchStatus) {
        loaded = true
        _status.value = status
        prefs(context).edit().putString(KEY_STATUS, encode(status)).apply()
        TileService.getUpdater(context.applicationContext).requestUpdate(ClearTileService::class.java)
    }

    /** Texte in der Sprache vom Handy. Bevor das Handy etwas geschickt hat, gilt die Sprache der Uhr. */
    val isEnglish: Boolean
        get() = _status.value?.english ?: (Locale.getDefault().language != "de")

    /** Nach dem Beenden oder Abbrechen auf der Uhr: sofort ausblenden, das Handy bestaetigt spaeter. */
    fun removeIdea(context: Context, ideaId: Long) {
        val current = load(context) ?: return
        save(context, current.copy(ideas = current.ideas.filter { it.id != ideaId }))
    }

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun encode(status: WatchStatus): String = JSONObject()
        .put("lastCheckIn", status.lastCheckIn)
        .apply { status.english?.let { put("english", it) } }
        .put("ideas", JSONArray().apply {
            status.ideas.forEach {
                put(JSONObject().put("id", it.id).put("title", it.title).put("startedAt", it.startedAt))
            }
        })
        .toString()

    private fun decode(text: String): WatchStatus {
        val json = JSONObject(text)
        val array = json.getJSONArray("ideas")
        val ideas = (0 until array.length()).map { i ->
            val item = array.getJSONObject(i)
            RunningIdea(item.getLong("id"), item.getString("title"), item.getLong("startedAt"))
        }
        val english = if (json.has("english")) json.getBoolean("english") else null
        return WatchStatus(ideas, json.getLong("lastCheckIn"), english)
    }
}
