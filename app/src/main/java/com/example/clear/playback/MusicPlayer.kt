package com.example.clear.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.clear.data.FileStore
import com.example.clear.data.MusicTrack
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

data class MusicPlayerState(
    val trackId: Long? = null,
    val title: String? = null,
    val isPlaying: Boolean = false,
    val timerMinutes: Int? = null,
    val message: String? = null
)

/**
 * Steuert die Musik-Wiedergabe ueber den Hintergrunddienst. Gilt fuer die ganze App,
 * damit Timer und Musik beim Seitenwechsel weiterlaufen.
 * Mit Timer wird das Stueck wiederholt, bis der Timer abgelaufen ist. Dann blendet die
 * Musik sanft aus und stoppt. Ohne Timer laeuft das Stueck einmal.
 */
object MusicPlayer {
    private const val FADE_STEPS = 100        // 100 Schritte x 100 ms = 10 Sekunden

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var controller: MediaController? = null
    private var timerJob: Job? = null

    private val _state = MutableStateFlow(MusicPlayerState())
    val state: StateFlow<MusicPlayerState> = _state.asStateFlow()

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.update { if (it.trackId == null) it else it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED && _state.value.trackId != null) {
                timerJob?.cancel()
                _state.value = MusicPlayerState()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            if (_state.value.trackId != null) {
                timerJob?.cancel()
                _state.value = MusicPlayerState(message = "Die Datei konnte nicht abgespielt werden.")
            }
        }
    }

    /** Startet ein Stueck. minutes == null bedeutet: ohne Timer. */
    fun play(context: Context, track: MusicTrack, minutes: Int?) {
        val file = FileStore.file(context, FileStore.MUSIC, track.fileName)
        if (!file.exists()) {
            _state.update { it.copy(message = "Diese Musikdatei wurde nicht gefunden.") }
            return
        }
        timerJob?.cancel()
        MeditationPlayer.stop()
        withController(context) { c ->
            c.volume = 1f
            c.repeatMode = if (minutes != null) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            c.setMediaItem(
                MediaItem.Builder()
                    .setUri(Uri.fromFile(file))
                    .setMediaMetadata(MediaMetadata.Builder().setTitle(track.title).build())
                    .build()
            )
            c.prepare()
            c.play()
            _state.value = MusicPlayerState(
                trackId = track.id, title = track.title, isPlaying = true, timerMinutes = minutes
            )
            if (minutes != null) startTimer(minutes)
        }
    }

    fun togglePause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun stop() {
        timerJob?.cancel()
        if (_state.value.trackId != null) {
            controller?.let {
                it.stop()
                it.clearMediaItems()
                it.volume = 1f
            }
        }
        _state.value = MusicPlayerState()
    }

    private fun startTimer(minutes: Int) {
        timerJob?.cancel()
        timerJob = scope.launch {
            delay(minutes * 60_000L)
            fadeOutAndStop()
        }
    }

    private suspend fun fadeOutAndStop() {
        val c = controller ?: return
        for (step in FADE_STEPS downTo 0) {
            c.volume = step / FADE_STEPS.toFloat()
            delay(100)
        }
        stop()
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
                _state.value = MusicPlayerState(message = "Die Musik konnte nicht gestartet werden.")
            }
        }, ContextCompat.getMainExecutor(app))
    }
}
