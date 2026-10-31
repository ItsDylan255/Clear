package com.example.clear.wear

import android.content.Context
import android.net.Uri
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

/** Verbindung zur Clear-App auf dem Handy. Die Pfade muessen zu WatchSync auf dem Handy passen. */
object PhoneLink {
    const val PATH_STATUS = "/clear/status"
    private const val PATH_END_IDEA = "/clear/end_idea"
    private const val PATH_CANCEL_IDEA = "/clear/cancel_idea"
    private const val PATH_REQUEST_STATUS = "/clear/request_status"

    /** Liest den zuletzt geschickten Stand und bittet das Handy um einen frischen. */
    suspend fun refresh(context: Context) {
        val app = context.applicationContext
        try {
            val buffer = Wearable.getDataClient(app).getDataItems(Uri.parse("wear://*$PATH_STATUS")).await()
            try {
                buffer.firstOrNull()?.let { StatusStore.save(app, WatchStatus.from(DataMapItem.fromDataItem(it).dataMap)) }
            } finally {
                buffer.release()
            }
        } catch (e: Exception) {
            // Noch nichts da oder keine Verbindung: der gespeicherte Stand bleibt
        }
        send(app, PATH_REQUEST_STATUS, ByteArray(0))
    }

    /** Beendet eine Idee auf dem Handy. false, wenn das Handy gerade nicht erreichbar ist. */
    suspend fun endIdea(context: Context, ideaId: Long, moodAfter: Int): Boolean {
        val sent = send(context.applicationContext, PATH_END_IDEA, "$ideaId:$moodAfter".toByteArray(Charsets.UTF_8))
        if (sent) StatusStore.removeIdea(context, ideaId)
        return sent
    }

    /** Bricht eine Idee auf dem Handy ohne Bewertung ab. false, wenn das Handy gerade nicht erreichbar ist. */
    suspend fun cancelIdea(context: Context, ideaId: Long): Boolean {
        val sent = send(context.applicationContext, PATH_CANCEL_IDEA, ideaId.toString().toByteArray(Charsets.UTF_8))
        if (sent) StatusStore.removeIdea(context, ideaId)
        return sent
    }

    private suspend fun send(context: Context, path: String, data: ByteArray): Boolean = try {
        val nodes = Wearable.getNodeClient(context).connectedNodes.await()
        val phone = nodes.firstOrNull { it.isNearby } ?: nodes.firstOrNull()
        if (phone == null) false
        else {
            Wearable.getMessageClient(context).sendMessage(phone.id, path, data).await()
            true
        }
    } catch (e: Exception) {
        false
    }
}
