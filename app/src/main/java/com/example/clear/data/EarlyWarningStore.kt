package com.example.clear.data

import android.content.Context
import com.example.clear.domain.EarlyWarningRules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Zustand der Fruehwarnung (klein, darum in SharedPreferences).
 * Die Karte auf der Startseite ist sichtbar, bis sie geschlossen wird oder 14 Tage alt ist.
 */
object EarlyWarningStore {
    const val FILE = "clear_early_warning"
    private const val KEY_SHOWN_AT = "shown_at"
    private const val KEY_DISMISSED = "dismissed"
    private const val KEY_LAST_PHQ_ID = "last_phq_id"

    private val _visible = MutableStateFlow(false)
    val visible: StateFlow<Boolean> = _visible.asStateFlow()

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun init(context: Context) = refresh(context)

    private fun refresh(context: Context) {
        val p = prefs(context)
        val shownAt = p.getLong(KEY_SHOWN_AT, 0L)
        _visible.value = shownAt > 0 && !p.getBoolean(KEY_DISMISSED, false) &&
            EarlyWarningRules.inPause(shownAt, Instant.now())
    }

    /** Karte schliessen. Die 14-Tage-Pause bleibt bestehen. */
    fun dismiss(context: Context) {
        prefs(context).edit().putBoolean(KEY_DISMISSED, true).apply()
        refresh(context)
    }

    /** Nach "Meine Daten loeschen". */
    fun reset(context: Context) {
        prefs(context).edit().clear().apply()
        refresh(context)
    }

    /** Prueft die Regeln (nach jedem Check-in und nach einem PHQ-9) und zeigt bei Bedarf die Karte. */
    suspend fun evaluate(context: Context) = withContext(Dispatchers.Default) {
        val p = prefs(context)
        val now = Instant.now()
        if (EarlyWarningRules.inPause(p.getLong(KEY_SHOWN_AT, 0L), now)) {
            refresh(context)
            return@withContext
        }
        val db = ClearDatabase.getInstance(context)
        val zone = ZoneId.systemDefault()
        val entries = db.checkInDao().observeAll().first()
        val phq = db.phq9Dao().observeAll().first().map { Triple(it.id, it.createdAt, it.total) }

        // Ein PHQ-9-Anstieg loest nur einmal aus, nicht bei jedem weiteren Check-in erneut
        val phqId = EarlyWarningRules.phqRiseId(phq)?.takeIf { it != p.getLong(KEY_LAST_PHQ_ID, -1L) }
        val dropped = EarlyWarningRules.valuesDropped(entries, LocalDate.now(zone), zone)

        if (dropped || phqId != null) {
            p.edit()
                .putLong(KEY_SHOWN_AT, now.toEpochMilli())
                .putBoolean(KEY_DISMISSED, false)
                .apply { if (phqId != null) putLong(KEY_LAST_PHQ_ID, phqId) }
                .apply()
        }
        refresh(context)
    }
}
