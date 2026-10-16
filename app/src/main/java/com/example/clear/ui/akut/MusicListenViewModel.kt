package com.example.clear.ui.akut

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.clear.data.ClearDatabase
import com.example.clear.data.MusicTrack
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Liefert die gespeicherten Titel. Die Wiedergabe selbst steuert MusicPlayer. */
class MusicListenViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    val situationId: String = handle.get<String>("id") ?: ""

    /** null = wird noch geladen. */
    val tracks: StateFlow<List<MusicTrack>?> = ClearDatabase.getInstance(app).musicDao()
        .observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
