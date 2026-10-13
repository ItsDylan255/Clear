package com.example.clear.data

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Loescht ALLE gespeicherten Daten wirklich: Datenbank, kopierte Bilder und Musik,
 * geplante Erinnerungen, Benachrichtigungen, Zwischenspeicher und Einstellungen.
 */
object DataWiper {
    suspend fun deleteEverything(context: Context) = withContext(Dispatchers.IO) {
        val app = context.applicationContext

        // 1. Geplante Erinnerungen und angezeigte Benachrichtigungen stoppen
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
    }
}
