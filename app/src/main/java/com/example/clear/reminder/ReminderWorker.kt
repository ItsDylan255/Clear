package com.example.clear.reminder

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.clear.MainActivity
import com.example.clear.R
import com.example.clear.data.ClearDatabase
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/** Laeuft zur geplanten Zeit: zeigt die passende Erinnerung und plant die naechste. */
class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = ClearDatabase.getInstance(applicationContext).reminderDao()
        val id = inputData.getInt(ReminderScheduler.KEY_SETTINGS_ID, ReminderScheduler.QUOTE_ID)
        val settings = dao.getSettingsById(id)
        if (settings == null || !settings.enabled) return Result.success()

        when (id) {
            ReminderScheduler.MEDITATION_ID -> showNotification(
                channelId = "meditation_reminders",
                channelName = "Meditations-Erinnerungen",
                channelDescription = "Freiwillige Erinnerungen ans Meditieren",
                notificationId = 2,
                text = "Ein ruhiger Moment für dich? Wenn du magst, nimm dir Zeit zum Meditieren."
            )
            ReminderScheduler.PHQ_ID -> {
                // Taeglich, sobald seit dem letzten Ausfuellen mindestens 14 Tage vergangen sind
                val latest = ClearDatabase.getInstance(applicationContext).phq9Dao().getLatest()?.createdAt
                if (isDue(latest)) showNotification(
                    channelId = "phq_reminders",
                    channelName = "PHQ-9-Erinnerungen",
                    channelDescription = "Freiwillige Erinnerung an den PHQ-9",
                    notificationId = 5,
                    text = "Dein PHQ-9 ist wieder bereit. Wenn du magst, nimm dir einen Moment dafür."
                )
            }
            ReminderScheduler.WHO5_ID -> {
                // Taeglich, sobald seit dem letzten Ausfuellen mindestens 14 Tage vergangen sind
                val latest = ClearDatabase.getInstance(applicationContext).who5Dao().getLatest()?.createdAt
                if (isDue(latest)) showNotification(
                    channelId = "who5_reminders",
                    channelName = "WHO-5-Erinnerungen",
                    channelDescription = "Freiwillige Erinnerung an den WHO-5",
                    notificationId = 7,
                    text = "Dein WHO-5 ist wieder bereit. Wenn du magst, nimm dir einen Moment dafür."
                )
            }
            ReminderScheduler.WSAS_ID -> {
                val latest = ClearDatabase.getInstance(applicationContext).wsasDao().getLatest()?.createdAt
                if (isDue(latest)) showNotification(
                    channelId = "wsas_reminders",
                    channelName = "WSAS-Erinnerungen",
                    channelDescription = "Freiwillige Erinnerung an die WSAS",
                    notificationId = 8,
                    text = "Deine WSAS ist wieder bereit. Wenn du magst, nimm dir einen Moment dafür."
                )
            }
            ReminderScheduler.EVENING_ID -> {
                // Hat man heute schon etwas eingetragen, bleibt die Erinnerung aus
                val today = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
                val entriesToday = ClearDatabase.getInstance(applicationContext)
                    .eveningDao().countForDay(today)
                if (entriesToday == 0) showNotification(
                    channelId = "evening_reminders",
                    channelName = "Abend-Erinnerungen",
                    channelDescription = "Freiwillige Erinnerungen an Erfolge und gute Dinge",
                    notificationId = 4,
                    text = "Was war heute gut oder okay? Wenn du magst, trag es kurz ein."
                )
            }
            ReminderScheduler.CHECKIN_ID -> {
                if (!checkedInToday()) showNotification(
                    channelId = "checkin_reminders",
                    channelName = "Check-in-Erinnerungen",
                    channelDescription = "Freiwillige Erinnerungen an den Check-in",
                    notificationId = 3,
                    text = "Wenn du magst: Nimm dir einen Moment für deinen Check-in."
                )
            }
            else -> {
                val pool = dao.getPoolQuotes()
                if (pool.isNotEmpty()) {
                    val quote = pool.random()
                    val text = if (quote.author.isNullOrBlank()) quote.text
                    else "${quote.text}\n– ${quote.author}"
                    showNotification(
                        channelId = "quote_reminders",
                        channelName = "Zitat-Erinnerungen",
                        channelDescription = "Freiwillige Erinnerungen an deine Zitate",
                        notificationId = 1,
                        text = text
                    )
                }
            }
        }

        ReminderScheduler.reschedule(applicationContext, settings, fromWorker = true)
        return Result.success()
    }

    /** Faellig: noch nie ausgefuellt oder das letzte Mal ist mindestens 14 Tage her. */
    private fun isDue(latestCreatedAt: Long?): Boolean {
        if (latestCreatedAt == null) return true
        val zone = ZoneId.systemDefault()
        val last = Instant.ofEpochMilli(latestCreatedAt).atZone(zone).toLocalDate()
        return ChronoUnit.DAYS.between(last, LocalDate.now(zone)) >= 14
    }

    private suspend fun checkedInToday(): Boolean {
        val latest = ClearDatabase.getInstance(applicationContext).checkInDao()
            .observeAll().first().firstOrNull() ?: return false
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(latest.timestamp).atZone(zone).toLocalDate() ==
                LocalDate.now(zone)
    }

    @SuppressLint("MissingPermission")  // areNotificationsEnabled() prueft die Erlaubnis
    private fun showNotification(
        channelId: String,
        channelName: String,
        channelDescription: String,
        notificationId: Int,
        text: String
    ) {
        val manager = NotificationManagerCompat.from(applicationContext)
        if (!manager.areNotificationsEnabled()) return

        val channel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_LOW
        ).apply { description = channelDescription }
        applicationContext.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        val openApp = PendingIntent.getActivity(
            applicationContext, notificationId,
            Intent(applicationContext, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Clear")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId, notification)
    }
}