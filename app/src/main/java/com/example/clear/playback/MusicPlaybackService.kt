package com.example.clear.playback

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.collect.ImmutableList

/**
 * Hintergrunddienst fuer Musik und Meditationen. Er haelt den Player, damit die Wiedergabe
 * auch bei ausgeschaltetem Bildschirm weiterlaeuft. Android zeigt dafuer eine Benachrichtigung
 * mit Pause/Play und einer Stopp-Taste. Ein Tipp auf die Benachrichtigung oeffnet die App.
 */
class MusicPlaybackService : MediaSessionService() {
    private var session: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            .setHandleAudioBecomingNoisy(true)   // Kopfhoerer ab = Pause
            .build()

        // Stopp-Taste in der Benachrichtigung (nutzt den normalen Player-Befehl "stop")
        val stopButton = CommandButton.Builder(CommandButton.ICON_STOP)
            .setDisplayName("Stopp")
            .setPlayerCommand(Player.COMMAND_STOP)
            .build()

        val builder = MediaSession.Builder(this, player)
            .setMediaButtonPreferences(ImmutableList.of(stopButton))

        // Tipp auf die Benachrichtigung oeffnet die App
        packageManager.getLaunchIntentForPackage(packageName)?.let { launch ->
            builder.setSessionActivity(
                PendingIntent.getActivity(
                    this, 0, launch,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
        }
        session = builder.build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onDestroy() {
        session?.let {
            it.player.release()
            it.release()
        }
        session = null
        super.onDestroy()
    }
}
