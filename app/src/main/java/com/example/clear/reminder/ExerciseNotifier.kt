package com.example.clear.reminder

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import android.app.Notification
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.clear.MainActivity
import com.example.clear.R
import com.example.clear.data.ClearDatabase
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.ui.checkin.ActiveIdeas

/**
 * Zeigt jede laufende Idee als eigene Benachrichtigung, zusammengefasst in einer Gruppe.
 * Sie sind als "laufend" markiert und bleiben, bis die Idee beendet ist. Android erlaubt dem
 * System und manchmal auch dir, laufende Benachrichtigungen wegzuwischen. Beim naechsten
 * Oeffnen der App (sync) erscheinen sie dann wieder.
 * Antippen oeffnet die Beschreibung, "Beenden" fuehrt direkt zur Bewertung "Wie fuehlst du dich jetzt?".
 */
object ExerciseNotifier {
    const val EXTRA_IDEA_ID = "clear_idea_id"
    const val EXTRA_ACTION = "clear_idea_action"
    const val ACTION_OPEN = "open"
    const val ACTION_END = "end"

    private const val CHANNEL_ID = "ideas_running"
    private const val GROUP_KEY = "clear_active_ideas"
    const val SUMMARY_ID = 5999
    private const val ID_BASE = 6000
    private const val LEGACY_ID = 6

    private val titles = linkedMapOf<Long, String>()

    /** Wird vom ActiveIdeasService gesetzt, solange der Dienst laeuft. */
    @Volatile var serviceRunning = false

    fun hasTitles(): Boolean = titles.isNotEmpty()

    private fun idFor(ideaId: Long) = ID_BASE + (ideaId % 3000).toInt()

    private fun ensureChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID, "Laufende Ideen", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Zeigt, welche Ideen gerade laufen" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun intentFor(context: Context, ideaId: Long, action: String, requestCode: Int): PendingIntent =
        PendingIntent.getActivity(
            context, requestCode,
            Intent(context, MainActivity::class.java)
                .putExtra(EXTRA_IDEA_ID, ideaId)
                .putExtra(EXTRA_ACTION, action)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    @SuppressLint("MissingPermission")  // areNotificationsEnabled() prueft die Erlaubnis
    fun show(context: Context, ideaId: Long, title: String) {
        val app = context.applicationContext
        val manager = NotificationManagerCompat.from(app)
        titles[ideaId] = title
        if (!manager.areNotificationsEnabled()) return
        ensureChannel(app)

        val id = idFor(ideaId)
        val notification = NotificationCompat.Builder(app, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText("Läuft gerade")
            .setContentIntent(intentFor(app, ideaId, ACTION_OPEN, id * 2))
            .addAction(0, "Beenden", intentFor(app, ideaId, ACTION_END, id * 2 + 1))
            .setGroup(GROUP_KEY)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .build()
        manager.notify(id, notification)
        refreshSummary(app)
    }

    @SuppressLint("MissingPermission")
    fun cancel(context: Context, ideaId: Long) {
        val app = context.applicationContext
        titles.remove(ideaId)
        NotificationManagerCompat.from(app).cancel(idFor(ideaId))
        refreshSummary(app)
    }

    /** Die Sammel-Benachrichtigung. Sie gehoert zum Vordergrunddienst und bleibt, bis alles beendet ist. */
    fun buildSummary(app: Context): Notification {
        ensureChannel(app)
        val style = NotificationCompat.InboxStyle()
        titles.values.forEach { style.addLine(it) }
        val count = titles.size
        val openApp = PendingIntent.getActivity(
            app, SUMMARY_ID,
            Intent(app, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(app, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                when (count) {
                    0 -> "Ideen laufen"
                    1 -> "1 Idee läuft"
                    else -> "$count Ideen laufen"
                }
            )
            .setStyle(style)
            .setContentIntent(openApp)
            .setGroup(GROUP_KEY)
            .setGroupSummary(true)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun refreshSummary(app: Context) {
        if (titles.isEmpty() && ActiveIdeas.running.value.isEmpty()) {
            app.stopService(Intent(app, ActiveIdeasService::class.java))
            NotificationManagerCompat.from(app).cancel(SUMMARY_ID)
            return
        }
        if (serviceRunning) {
            // Der Dienst laeuft schon: nur die Sammel-Benachrichtigung aktualisieren
            if (NotificationManagerCompat.from(app).areNotificationsEnabled()) {
                NotificationManagerCompat.from(app).notify(SUMMARY_ID, buildSummary(app))
            }
            return
        }
        try {
            ContextCompat.startForegroundService(app, Intent(app, ActiveIdeasService::class.java))
        } catch (e: Exception) {
            // Start im Hintergrund nicht erlaubt: ohne Dienst anzeigen
            if (NotificationManagerCompat.from(app).areNotificationsEnabled()) {
                NotificationManagerCompat.from(app).notify(SUMMARY_ID, buildSummary(app))
            }
        }
    }

    /** Alte Benachrichtigung der frueheren Uebungen entfernen. */
    fun cancelLegacy(context: Context) {
        NotificationManagerCompat.from(context.applicationContext).cancel(LEGACY_ID)
    }

    /** Stellt die Benachrichtigungen aller laufenden Ideen wieder her (zum Beispiel nach einem Neustart). */
    suspend fun sync(context: Context) {
        val app = context.applicationContext
        ActiveIdeas.load(app)
        val repository = PersonalIdeaRepository(ClearDatabase.getInstance(app).personalIdeaDao())
        ActiveIdeas.running.value.keys.toList().forEach { ideaId ->
            val idea = repository.get(ideaId)
            if (idea == null) {
                ActiveIdeas.end(app, ideaId)
                cancel(app, ideaId)
            } else {
                show(app, ideaId, idea.title)
            }
        }
    }
}
