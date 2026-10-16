package com.example.clear.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.clear.data.ReminderSettings
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Random
import java.util.concurrent.TimeUnit

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
            val times = if (settings.randomWindow) randomTimes(date, settings)
            else listOf(LocalTime.of(settings.hour, settings.minute))
            for (time in times) {
                val candidate = date.atTime(time).atZone(now.zone)
                if (candidate.isAfter(earliest)) return Duration.between(now, candidate).toMillis()
            }
        }
        return TimeUnit.DAYS.toMillis(1)
    }

    /**
     * Die zufaelligen Zeiten eines Tages: Das Zeitfenster wird in gleich lange Abschnitte
     * geteilt, in jedem Abschnitt wird ein zufaelliger Zeitpunkt gewaehlt. So sind die Zeiten
     * jeden Tag anders, aber nie alle dicht beieinander. Pro Tag und Einstellung stabil.
     */
    fun randomTimes(date: LocalDate, settings: ReminderSettings): List<LocalTime> {
        val startMin = (settings.hour * 60 + settings.minute).coerceIn(0, 24 * 60 - 2)
        val endMin = (settings.endHour * 60 + settings.endMinute)
            .coerceAtLeast(startMin + 1)
            .coerceAtMost(24 * 60 - 1)
        val count = settings.timesPerDay.coerceIn(1, 6)
        val seed = date.toEpochDay() * 1_000_003L + settings.id * 7919L +
            startMin * 31L + endMin * 17L + count
        val random = Random(seed)
        val segment = (endMin - startMin).toDouble() / count
        return (0 until count).map { i ->
            val minute = (startMin + segment * i + random.nextDouble() * segment).toInt()
                .coerceIn(startMin, endMin)
            LocalTime.of(minute / 60, minute % 60)
        }
    }
}
