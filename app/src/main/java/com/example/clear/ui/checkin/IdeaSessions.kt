package com.example.clear.ui.checkin

import android.content.Context
import com.example.clear.data.ClearDatabase
import com.example.clear.reminder.DoNotDisturb
import com.example.clear.reminder.ExerciseNotifier

/** Beenden oder Abbrechen einer laufenden Idee. Gemeinsam fuer das Handy und die Uhr. */
object IdeaSessions {

    /**
     * Speichert die Stimmung danach, beendet die Idee und entfernt ihre Benachrichtigung.
     * Gibt "Stimmung davor zu danach" zurueck, oder null, wenn es keinen offenen Durchlauf gab.
     */
    suspend fun end(context: Context, ideaId: Long, moodAfter: Int): Pair<Int, Int>? {
        val app = context.applicationContext
        val dao = ClearDatabase.getInstance(app).exerciseSessionDao()
        var result: Pair<Int, Int>? = null
        val open = dao.getOpen(ActiveIdeas.key(ideaId))
        if (open != null) {
            dao.update(open.copy(endedAt = System.currentTimeMillis(), moodAfter = moodAfter))
            result = open.moodBefore to moodAfter
        }
        stop(app, ideaId)
        return result
    }

    /**
     * Bricht eine laufende Idee ohne Bewertung ab. Der offene Durchlauf wird geloescht,
     * er zaehlt also nicht fuer den Durchschnitt.
     */
    suspend fun cancel(context: Context, ideaId: Long) {
        val app = context.applicationContext
        val dao = ClearDatabase.getInstance(app).exerciseSessionDao()
        dao.getOpen(ActiveIdeas.key(ideaId))?.let { dao.delete(it) }
        stop(app, ideaId)
    }

    private fun stop(app: Context, ideaId: Long) {
        ActiveIdeas.end(app, ideaId)
        ExerciseNotifier.cancel(app, ideaId)
        if (ActiveIdeas.running.value.isEmpty()) DoNotDisturb.exit(app)
    }
}
