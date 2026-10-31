package com.example.clear.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.clear.ui.checkin.IdeaSessions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** "Abbrechen" in der Benachrichtigung einer laufenden Idee: bricht sie ab, ohne die App zu oeffnen. */
class IdeaCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val ideaId = intent.getLongExtra(ExerciseNotifier.EXTRA_IDEA_ID, -1L)
        if (ideaId <= 0L) return
        val app = context.applicationContext
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                IdeaSessions.cancel(app, ideaId)
            } finally {
                pending.finish()
            }
        }
    }
}
