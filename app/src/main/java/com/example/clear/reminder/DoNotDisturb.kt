package com.example.clear.reminder

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Schaltet das Handy waehrend einer Akut-Uebung auf "Nur Wecker" und danach zurueck.
 * Braucht die Erlaubnis "Nicht stoeren" in den Systemeinstellungen. Der Schalter in den
 * Einstellungen ist standardmaessig aus. Zur Sicherheit endet der Modus nach drei Stunden
 * von selbst, falls eine Uebung nie beendet wird.
 */
object DoNotDisturb {
    private const val PREFS = "clear_focus"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_ACTIVE = "active"      // Hat Clear den Modus selbst eingeschaltet?
    private const val KEY_PREVIOUS = "previous"  // Filter vor dem Einschalten
    private const val WORK_NAME = "dnd_release"
    private const val RELEASE_AFTER_HOURS = 3L

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun manager(context: Context): NotificationManager =
        context.applicationContext.getSystemService(NotificationManager::class.java)

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
        if (!enabled) exit(context)
    }

    /** Hat Clear die Erlaubnis "Nicht stoeren" in den Systemeinstellungen bekommen? */
    fun hasAccess(context: Context): Boolean = manager(context).isNotificationPolicyAccessGranted

    /** Eine Uebung laeuft: Modus einschalten (nur wenn Schalter an und Erlaubnis da). */
    fun enter(context: Context) {
        if (!isEnabled(context) || !hasAccess(context)) return
        val prefs = prefs(context)
        val manager = manager(context)
        if (!prefs.getBoolean(KEY_ACTIVE, false)) {
            prefs.edit()
                .putBoolean(KEY_ACTIVE, true)
                .putInt(KEY_PREVIOUS, manager.currentInterruptionFilter)
                .apply()
            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
        }
        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<DndReleaseWorker>()
                .setInitialDelay(RELEASE_AFTER_HOURS, TimeUnit.HOURS)
                .build()
        )
    }

    /** Die Uebung ist vorbei: den vorherigen Zustand wiederherstellen. */
    fun exit(context: Context) {
        val prefs = prefs(context)
        if (prefs.getBoolean(KEY_ACTIVE, false)) {
            prefs.edit().putBoolean(KEY_ACTIVE, false).apply()
            val manager = manager(context)
            // Nur zurueckstellen, wenn der Nutzer in der Zwischenzeit nichts selbst geaendert hat
            if (manager.isNotificationPolicyAccessGranted &&
                manager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALARMS
            ) {
                var previous = prefs.getInt(KEY_PREVIOUS, NotificationManager.INTERRUPTION_FILTER_ALL)
                if (previous == NotificationManager.INTERRUPTION_FILTER_UNKNOWN) {
                    previous = NotificationManager.INTERRUPTION_FILTER_ALL
                }
                manager.setInterruptionFilter(previous)
            }
        }
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(WORK_NAME)
    }
}

/** Sicherheitsnetz: beendet "Nicht stoeren" nach drei Stunden. */
class DndReleaseWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        DoNotDisturb.exit(applicationContext)
        return Result.success()
    }
}
