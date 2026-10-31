package com.example.clear.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.clear.model.Meditation
import com.example.clear.tr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MeditationPlayerState(
    val meditationId: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    /** 0 = noch unbekannt. Die echte Laenge kommt aus der Audiodatei. */
    val durationMs: Long = 0L,
    val message: String? = null
)

/**
 * Spielt die gefuehrten Meditationen ueber denselben Hintergrunddienst wie die Musik.
 * Die Aufnahmen liegen in app/src/main/res/raw/ und heissen "meditation_<id>"
 * (zum Beispiel meditation_mindfulness.mp3). Die Laenge liest der Player aus der Datei.
 */
object MeditationPlayer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var controller: MediaController? = null
    private var progressJob: Job? = null
    private var startedAt = 0L

    private val _state = MutableStateFlow(MeditationPlayerState())
    val state: StateFlow<MeditationPlayerState> = _state.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { if (it.meditationId == null) it else it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (_state.value.meditationId == null) return
            val ended = playbackState == Player.STATE_ENDED
            // Stopp ueber die Benachrichtigung: Player ist im Leerlauf. In den ersten
            // Sekunden nach dem Start ignorieren (alte Rueckmeldungen vom Vorgaenger).
            val stopped = playbackState == Player.STATE_IDLE &&
                SystemClock.elapsedRealtime() - startedAt > 2000L
            if (ended || stopped) {
                progressJob?.cancel()
                _state.value = MeditationPlayerState()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            if (_state.value.meditationId != null) {
                progressJob?.cancel()
                _state.value =
                    MeditationPlayerState(message = tr(
                        "Die Aufnahme konnte nicht abgespielt werden.",
                        "The recording could not be played."
                    ))
            }
        }
    }

    /** 0 = fuer diese Meditation ist noch keine Aufnahme eingebunden. */
    fun resourceId(context: Context, meditation: Meditation): Int =
        context.resources.getIdentifier("meditation_${meditation.id}", "raw", context.packageName)

    /** Startet die Meditation. Laeuft sie schon, geht es an der Stelle weiter. */
    fun play(context: Context, meditation: Meditation) {
        if (_state.value.meditationId == meditation.id) {
            controller?.play()
            return
        }
        val resId = resourceId(context, meditation)
        if (resId == 0) {
            _state.value =
                MeditationPlayerState(message = tr(
                    "Für diese Meditation ist noch keine Aufnahme vorhanden.",
                    "There is no recording for this meditation yet."
                ))
            return
        }
        MusicPlayer.stop()
        startedAt = SystemClock.elapsedRealtime()
        val packageName = context.applicationContext.packageName
        withController(context) { c ->
            c.volume = 1f
            c.repeatMode = Player.REPEAT_MODE_OFF
            c.setMediaItem(
                MediaItem.Builder()
                    .setUri(Uri.parse("android.resource://$packageName/$resId"))
                    .setMediaMetadata(MediaMetadata.Builder().setTitle(meditation.title).build())
                    .build()
            )
            c.prepare()
            c.play()
            _state.value = MeditationPlayerState(meditationId = meditation.id, isPlaying = true)
            startProgress()
        }
    }

    fun togglePause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun seekBack(seconds: Int) {
        val c = controller ?: return
        c.seekTo((c.currentPosition - seconds * 1000L).coerceAtLeast(0L))
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs.coerceAtLeast(0L))
    }

    fun stop() {
        progressJob?.cancel()
        if (_state.value.meditationId != null) {
            controller?.let {
                it.stop()
                it.clearMediaItems()
            }
        }
        _state.value = MeditationPlayerState()
    }

    private fun startProgress() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                val c = controller
                if (c != null && _state.value.meditationId != null) {
                    val duration = c.duration
                    _state.update {
                        it.copy(
                            positionMs = c.currentPosition.coerceAtLeast(0L),
                            durationMs = if (duration == C.TIME_UNSET) 0L else duration
                        )
                    }
                }
                delay(500)
            }
        }
    }

    private fun withController(context: Context, action: (MediaController) -> Unit) {
        val existing = controller
        if (existing != null && existing.isConnected) {
            action(existing)
            return
        }
        val app = context.applicationContext
        val token = SessionToken(app, ComponentName(app, MusicPlaybackService::class.java))
        val future = MediaController.Builder(app, token).buildAsync()
        future.addListener({
            try {
                val connected = future.get()
                connected.addListener(listener)
                controller = connected
                action(connected)
            } catch (e: Exception) {
                _state.value =
                    MeditationPlayerState(message = tr(
                        "Die Aufnahme konnte nicht gestartet werden.",
                        "The recording could not be started."
                    ))
            }
        }, ContextCompat.getMainExecutor(app))
    }
}
