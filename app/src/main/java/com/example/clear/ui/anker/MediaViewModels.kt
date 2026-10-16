package com.example.clear.ui.anker

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.AnchorImage
import com.example.clear.data.ClearDatabase
import com.example.clear.data.FileStore
import com.example.clear.data.MusicTrack
import com.example.clear.data.Quote
import com.example.clear.data.ReminderPoolEntry
import com.example.clear.data.ReminderSettings
import com.example.clear.model.EmotionTag
import com.example.clear.model.MoodGoal
import com.example.clear.reminder.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---------- Bilder ----------

class ImagesViewModel(app: Application) : AndroidViewModel(app) {
    private val context: Application get() = getApplication()
    private val dao = ClearDatabase.getInstance(app).imageDao()

    val images: StateFlow<List<AnchorImage>?> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun addImage(uri: Uri) {
        viewModelScope.launch {
            val fileName = FileStore.import(context, uri, FileStore.IMAGES)
            if (fileName == null) {
                _message.value = "Das Bild konnte nicht gespeichert werden."
            } else {
                _message.value = null
                dao.insert(AnchorImage(fileName = fileName, caption = null, createdAt = System.currentTimeMillis()))
            }
        }
    }

    fun updateCaption(id: Long, caption: String) {
        viewModelScope.launch { dao.updateCaption(id, caption.trim().ifBlank { null }) }
    }

    fun updateTags(image: AnchorImage, tags: Set<EmotionTag>) {
        viewModelScope.launch { dao.updateTags(image.id, EmotionTag.serialize(tags)) }
    }

    fun delete(image: AnchorImage) {
        viewModelScope.launch {
            FileStore.delete(context, FileStore.IMAGES, image.fileName)
            dao.delete(image)
        }
    }
}

class ImageViewerViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val imageId: Long = handle.get<Long>("imageId") ?: -1L

    val image: StateFlow<AnchorImage?> = ClearDatabase.getInstance(app).imageDao()
        .observeById(imageId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

// ---------- Musik ----------

data class PlaybackState(
    val trackId: Long? = null,
    val isPlaying: Boolean = false,
    val message: String? = null
)

/**
 * Einfache Wiedergabe mit dem eingebauten MediaPlayer.
 * Sie laeuft nur, solange die Musik-Seite offen ist. Hintergrund-Wiedergabe folgt spaeter.
 */
class MusicViewModel(app: Application) : AndroidViewModel(app) {
    private val context: Application get() = getApplication()
    private val dao = ClearDatabase.getInstance(app).musicDao()

    val tracks: StateFlow<List<MusicTrack>?> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _playback = MutableStateFlow(PlaybackState())
    val playback: StateFlow<PlaybackState> = _playback.asStateFlow()

    private var player: MediaPlayer? = null
    private var prepared = false

    fun addTrack(uri: Uri) {
        viewModelScope.launch {
            val fileName = FileStore.import(context, uri, FileStore.MUSIC)
            if (fileName == null) {
                _playback.update { it.copy(message = "Die Musik konnte nicht gespeichert werden.") }
            } else {
                val title = FileStore.displayName(context, uri)?.substringBeforeLast('.') ?: "Musik"
                dao.insert(MusicTrack(fileName = fileName, title = title, createdAt = System.currentTimeMillis()))
                _playback.update { it.copy(message = null) }
            }
        }
    }

    fun togglePlay(track: MusicTrack) {
        val current = player
        if (current != null && _playback.value.trackId == track.id) {
            if (!prepared) return   // laedt noch
            if (_playback.value.isPlaying) {
                current.pause()
                _playback.update { it.copy(isPlaying = false) }
            } else {
                current.start()
                _playback.update { it.copy(isPlaying = true) }
            }
            return
        }
        stopPlayback()
        val file = FileStore.file(context, FileStore.MUSIC, track.fileName)
        if (!file.exists()) {
            _playback.value = PlaybackState(message = "Diese Datei wurde nicht gefunden. Du kannst den Eintrag löschen.")
            return
        }
        try {
            val mp = MediaPlayer()
            mp.setDataSource(file.absolutePath)
            mp.setOnPreparedListener {
                prepared = true
                it.start()
                _playback.value = PlaybackState(trackId = track.id, isPlaying = true)
            }
            mp.setOnCompletionListener { stopPlayback() }
            mp.setOnErrorListener { _, _, _ ->
                stopPlayback()
                _playback.value = PlaybackState(message = "Die Datei konnte nicht abgespielt werden.")
                true
            }
            player = mp
            _playback.value = PlaybackState(trackId = track.id, isPlaying = false)
            mp.prepareAsync()
        } catch (e: Exception) {
            stopPlayback()
            _playback.value = PlaybackState(message = "Die Datei konnte nicht abgespielt werden.")
        }
    }

    fun delete(track: MusicTrack) {
        if (_playback.value.trackId == track.id) stopPlayback()
        viewModelScope.launch {
            FileStore.delete(context, FileStore.MUSIC, track.fileName)
            dao.delete(track)
        }
    }

    fun updateTags(track: MusicTrack, tags: Set<EmotionTag>) {
        viewModelScope.launch { dao.updateTags(track.id, EmotionTag.serialize(tags)) }
    }

    fun updateMoodGoals(track: MusicTrack, goals: Set<MoodGoal>) {
        viewModelScope.launch { dao.updateMoodGoals(track.id, MoodGoal.serialize(goals)) }
    }

    private fun stopPlayback() {
        player?.release()
        player = null
        prepared = false
        _playback.value = PlaybackState()
    }

    override fun onCleared() {
        player?.release()
        player = null
    }
}

// ---------- Zitat-Erinnerungen ----------

data class RemindersUiState(
    val isLoading: Boolean = true,
    val settings: ReminderSettings = ReminderSettings(),
    val quotes: List<Quote> = emptyList(),
    val selectedIds: Set<Long> = emptySet()
)

class RemindersViewModel(app: Application) : AndroidViewModel(app) {
    private val context: Application get() = getApplication()
    private val db = ClearDatabase.getInstance(app)
    private val dao = db.reminderDao()

    val state: StateFlow<RemindersUiState> = combine(
        dao.observeSettings(), db.quoteDao().observeAll(), dao.observePoolIds()
    ) { settings, quotes, ids ->
        RemindersUiState(false, settings ?: ReminderSettings(), quotes, ids.toSet())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RemindersUiState())

    fun setEnabled(enabled: Boolean) = update { it.copy(enabled = enabled) }
    fun setTime(hour: Int, minute: Int) = update { it.copy(hour = hour, minute = minute) }
    fun setRandomWindow(enabled: Boolean) = update { it.copy(randomWindow = enabled) }
    fun setEndTime(hour: Int, minute: Int) = update { it.copy(endHour = hour, endMinute = minute) }
    fun setTimesPerDay(count: Int) = update { it.copy(timesPerDay = count) }

    /** dayIndex: 0 = Montag ... 6 = Sonntag */
    fun toggleDay(dayIndex: Int) = update { it.copy(daysMask = it.daysMask xor (1 shl dayIndex)) }

    fun togglePool(quoteId: Long, selected: Boolean) {
        viewModelScope.launch {
            if (selected) dao.addToPool(ReminderPoolEntry(quoteId)) else dao.removeFromPool(quoteId)
        }
    }

    private fun update(change: (ReminderSettings) -> ReminderSettings) {
        viewModelScope.launch {
            val newSettings = change(dao.getSettings() ?: ReminderSettings())
            dao.saveSettings(newSettings)
            ReminderScheduler.reschedule(context, newSettings)
        }
    }
}
