package com.example.clear.watch

import com.example.clear.ui.checkin.IdeaSessions
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking

/**
 * Nimmt Nachrichten der Uhr an: eine Idee beenden ("ideaId:stimmung"), abbrechen ("ideaId") oder
 * den Stand neu anfordern. Android startet den Dienst dafuer bei Bedarf, auch wenn die App geschlossen ist.
 */
class WatchMessageService : WearableListenerService() {

    override fun onMessageReceived(event: MessageEvent) {
        when (event.path) {
            WatchSync.PATH_END_IDEA -> {
                val parts = String(event.data, Charsets.UTF_8).split(":")
                val ideaId = parts.getOrNull(0)?.toLongOrNull() ?: return
                val mood = parts.getOrNull(1)?.toIntOrNull()?.takeIf { it in 1..10 } ?: return
                // Laeuft auf einem Hintergrund-Thread des Dienstes
                runBlocking {
                    IdeaSessions.end(applicationContext, ideaId, mood)
                    WatchSync.publish(applicationContext)
                }
            }
            WatchSync.PATH_CANCEL_IDEA -> {
                val ideaId = String(event.data, Charsets.UTF_8).toLongOrNull() ?: return
                runBlocking {
                    IdeaSessions.cancel(applicationContext, ideaId)
                    WatchSync.publish(applicationContext)
                }
            }
            WatchSync.PATH_REQUEST_STATUS -> runBlocking { WatchSync.publish(applicationContext) }
        }
    }
}
