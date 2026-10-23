package com.example.clear.reminder

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Haelt die Sammel-Benachrichtigung der laufenden Ideen als Vordergrunddienst. Solche
 * Benachrichtigungen lassen sich auch ab Android 14 nicht wegwischen. Der Dienst endet,
 * sobald die letzte Idee beendet ist.
 */
class ActiveIdeasService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        ExerciseNotifier.serviceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = ExerciseNotifier.buildSummary(applicationContext)
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                ExerciseNotifier.SUMMARY_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(ExerciseNotifier.SUMMARY_ID, notification)
        }
        // Nach einem Neustart der App kennt der Notifier die Titel noch nicht: neu laden
        if (!ExerciseNotifier.hasTitles()) {
            scope.launch {
                ExerciseNotifier.sync(applicationContext)
                if (com.example.clear.ui.checkin.ActiveIdeas.running.value.isEmpty()) stopSelfSafely()
            }
        }
        return START_STICKY
    }

    private fun stopSelfSafely() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        ExerciseNotifier.serviceRunning = false
        scope.cancel()
        super.onDestroy()
    }
}
