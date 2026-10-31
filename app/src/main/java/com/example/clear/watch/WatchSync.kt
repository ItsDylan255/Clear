package com.example.clear.watch

import android.content.Context
import com.example.clear.AppLanguage
import com.example.clear.data.ClearDatabase
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.ui.checkin.ActiveIdeas
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Schickt der Uhr den aktuellen Stand: welche Ideen laufen (Titel und Startzeit) und wann der
 * letzte Check-in war, dazu die eingestellte Sprache. Mehr verlaesst das Handy nicht. Ohne Uhr oder ohne Google Play-Dienste
 * passiert einfach nichts.
 */
object WatchSync {
    const val PATH_STATUS = "/clear/status"
    const val PATH_END_IDEA = "/clear/end_idea"
    const val PATH_CANCEL_IDEA = "/clear/cancel_idea"
    const val PATH_REQUEST_STATUS = "/clear/request_status"

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Stand im Hintergrund an die Uhr schicken. */
    fun update(context: Context) {
        val app = context.applicationContext
        scope.launch { publish(app) }
    }

    suspend fun publish(context: Context) {
        val app = context.applicationContext
        try {
            val db = ClearDatabase.getInstance(app)
            val repository = PersonalIdeaRepository(db.personalIdeaDao())
            ActiveIdeas.load(app)
            val ideas = ArrayList<DataMap>()
            ActiveIdeas.running.value.values.sortedBy { it.startedAt }.forEach { running ->
                val idea = repository.get(running.ideaId) ?: return@forEach
                ideas += DataMap().apply {
                    putLong("id", running.ideaId)
                    putString("title", idea.title)
                    putLong("startedAt", running.startedAt)
                }
            }
            val request = PutDataMapRequest.create(PATH_STATUS).apply {
                dataMap.putDataMapArrayList("ideas", ideas)
                dataMap.putLong("lastCheckIn", db.checkInDao().getLatest()?.timestamp ?: 0L)
                // Die Uhr zeigt ihre Texte in der Sprache, die in Clear eingestellt ist
                dataMap.putString("lang", if (AppLanguage.isEnglish) "en" else "de")
                // Sorgt dafuer, dass die Uhr auch bei gleichem Inhalt neu benachrichtigt wird
                dataMap.putLong("updatedAt", System.currentTimeMillis())
            }.asPutDataRequest().setUrgent()
            Wearable.getDataClient(app).putDataItem(request).await()
        } catch (e: Exception) {
            // Keine Uhr, keine Play-Dienste oder Verbindung gerade weg: beim naechsten Mal wieder
        }
    }
}
