package com.example.clear.data

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.example.clear.reminder.ActiveIdeasService
import com.example.clear.reminder.DoNotDisturb
import com.example.clear.reminder.ExerciseNotifier
import com.example.clear.ui.checkin.ActiveIdeas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Loescht ALLE gespeicherten Daten wirklich: Datenbank (alle Tabellen, auch Frageboegen und
 * Warnzeichen), kopierte Bilder und Musik, laufende Ideen, geplante Erinnerungen, Benachrichtigungen, Zwischenspeicher und Einstellungen.
 */
object DataWiper {
    suspend fun deleteEverything(context: Context) = withContext(Dispatchers.IO) {
        val app = context.applicationContext

        // 1. Laufende Ideen beenden, "Nicht stoeren" zuruecknehmen, Erinnerungen und
        //    Benachrichtigungen stoppen
        DoNotDisturb.exit(app)
        app.stopService(Intent(app, ActiveIdeasService::class.java))
        ActiveIdeas.clearAll(app)
        ExerciseNotifier.reset()
        WorkManager.getInstance(app).cancelAllWork()
        NotificationManagerCompat.from(app).cancelAll()

        // 2. Alle Tabellen leeren (Room raeumt die Datenbankdatei danach auf)
        ClearDatabase.getInstance(app).clearAllTables()

        // 3. Kopierte Dateien entfernen
        listOf(FileStore.IMAGES, FileStore.MUSIC).forEach { folder ->
            File(app.filesDir, folder).deleteRecursively()
        }
        app.cacheDir.listFiles()?.forEach { it.deleteRecursively() }

        // 4. Kleine Einstellungen zuruecksetzen
        ThemePreferences.reset()
        DoNotDisturb.setEnabled(app, false)
        // "clear_active_exercise" und "clear_defaults" gibt es nur noch bei Installationen aelterer Versionen
        listOf("clear_active_ideas", "clear_active_exercise", "clear_defaults").forEach { name ->
            app.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().apply()
        }
    }
}
