package com.example.clear.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.clear.data.ReminderSettings
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import androidx.work.workDataOf

/**
 * Plant die naechste Erinnerung mit WorkManager.
 * Die Uhrzeit ist "ungefaehr": Android darf sie um einige Minuten verschieben (spart Akku).
 */
object ReminderScheduler {
    const val QUOTE_ID = 1
    const val MEDITATION_ID = 2
    const val CHECKIN_ID = 3
    const val KEY_SETTINGS_ID = "settingsId"

    private fun workName(settingsId: Int) = when (settingsId) {
        MEDITATION_ID -> "meditation_reminder"
        CHECKIN_ID -> "checkin_reminder"
        else -> "quote_reminder"
    }

    fun reschedule(context: Context, settings: ReminderSettings, fromWorker: Boolean = false) {
        val workManager = WorkManager.getInstance(context)
        val name = workName(settings.id)
        if (!settings.enabled || settings.daysMask == 0) {
            workManager.cancelUniqueWork(name)
            return
        }
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(millisUntilNext(settings, ZonedDateTime.now()), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(KEY_SETTINGS_ID to settings.id))
            .build()
        // Aus dem Worker selbst darf der laufende Auftrag nicht ersetzt werden
        val policy = if (fromWorker) ExistingWorkPolicy.APPEND_OR_REPLACE else ExistingWorkPolicy.REPLACE
        workManager.enqueueUniqueWork(name, policy, request)
    }

    /** Millisekunden bis zum naechsten passenden Zeitpunkt (mindestens eine Minute in der Zukunft). */
    fun millisUntilNext(settings: ReminderSettings, now: ZonedDateTime): Long {
        val earliest = now.plusMinutes(1)
        for (offset in 0..7) {
            val date = earliest.toLocalDate().plusDays(offset.toLong())
            val dayBit = 1 shl (date.dayOfWeek.value - 1)   // Montag = Bit 0
            if (settings.daysMask and dayBit == 0) continue
            val candidate = date.atTime(settings.hour, settings.minute).atZone(now.zone)
            if (candidate.isAfter(earliest)) return Duration.between(now, candidate).toMillis()
        }
        return TimeUnit.DAYS.toMillis(1)
    }
}
