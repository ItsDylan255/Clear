package com.example.clear.ui.anker

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.clear.data.AnchorImage
import com.example.clear.data.ClearDatabase
import com.example.clear.data.FileStore
import com.example.clear.data.Letter
import com.example.clear.data.MusicTrack
import com.example.clear.data.Quote
import com.example.clear.playback.MusicPlayer
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.DateFormat
import java.util.Date
import java.util.Locale

/**
 * Liefert die Elemente einer Einzelansicht in der Reihenfolge der Uebersicht.
 * "itemId" ist das angetippte Element, "ids" die Reihenfolge (z. B. "12-5-9").
 * Fehlt "ids", gibt es nur dieses eine Element. null = wird noch geladen.
 */
class AnkerItemViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val itemId: Long = handle.get<Long>("itemId") ?: -1L
    private val ids: List<Long> = handle.get<String>("ids").orEmpty()
        .split('-').mapNotNull { it.toLongOrNull() }.ifEmpty { listOf(itemId) }
    private val db = ClearDatabase.getInstance(app)
    private val keep = SharingStarted.WhileSubscribed(5_000)

    /** Position des angetippten Elements in der Reihenfolge. */
    val startIndex: Int = ids.indexOf(itemId).coerceAtLeast(0)

    private fun <T> ordered(source: Flow<List<T>>, idOf: (T) -> Long): StateFlow<List<T>?> =
        source.map { all -> ids.mapNotNull { id -> all.firstOrNull { idOf(it) == id } } }
            .stateIn(viewModelScope, keep, null)

    val quotes: StateFlow<List<Quote>?> = ordered(db.quoteDao().observeAll()) { it.id }
    val images: StateFlow<List<AnchorImage>?> = ordered(db.imageDao().observeAll()) { it.id }
    val tracks: StateFlow<List<MusicTrack>?> = ordered(db.musicDao().observeAll()) { it.id }
    val letters: StateFlow<List<Letter>?> = ordered(db.letterDao().observeAll()) { it.id }
}

private fun formatDate(timestamp: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).format(Date(timestamp))

/**
 * Gemeinsamer Rahmen: Zurueck-Pfeil und "2 von 5", darunter die Elemente zum Wischen.
 * Keine Verwaltungsfunktionen.
 */
@Composable
private fun <T> PagedViewer(
    onBack: () -> Unit,
    items: List<T>?,
    startIndex: Int,
    page: @Composable (T) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (items.isNullOrEmpty()) {
            ViewerHeader(onBack, null)
        } else {
            val pager = rememberPagerState(
                initialPage = startIndex.coerceIn(0, items.size - 1),
                pageCount = { items.size }
            )
            ViewerHeader(onBack, if (items.size > 1) "${pager.currentPage + 1} von ${items.size}" else null)
            HorizontalPager(
                state = pager,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { index -> page(items[index]) }
        }
    }
}

@Composable
private fun ViewerHeader(onBack: () -> Unit, position: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
        }
        Spacer(Modifier.weight(1f))
        if (position != null) {
            Text(
                position,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 12.dp)
            )
        }
    }
}

/** Zitate gross, mittig und mit viel Freiraum. Nach links und rechts wischen = naechstes Zitat. */
@Composable
fun QuoteViewScreen(onBack: () -> Unit, viewModel: AnkerItemViewModel = viewModel()) {
    val list by viewModel.quotes.collectAsState()
    PagedViewer(onBack, list, viewModel.startIndex) { q ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    q.text,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                if (!q.author.isNullOrBlank()) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "– ${q.author}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/** Bilder moeglichst gross, ohne Zuschnitt. */
@Composable
fun ImageViewScreen(onBack: () -> Unit, viewModel: AnkerItemViewModel = viewModel()) {
    val list by viewModel.images.collectAsState()
    val context = LocalContext.current
    PagedViewer(onBack, list, viewModel.startIndex) { current ->
        val file = FileStore.file(context, FileStore.IMAGES, current.fileName)
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = current.caption ?: "Dein Bild",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium)
                    )
                } else {
                    Text(
                        "Diese Bilddatei wurde nicht gefunden.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            val caption = current.caption
            if (!caption.isNullOrBlank()) {
                Text(
                    caption,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        }
    }
}

/** Kompakte Wiedergabesteuerung. Wischen wechselt den angezeigten Titel, laufende Musik bleibt. */
@Composable
fun TrackViewScreen(onBack: () -> Unit, viewModel: AnkerItemViewModel = viewModel()) {
    val list by viewModel.tracks.collectAsState()
    val player by MusicPlayer.state.collectAsState()
    val minutes by MusicChoice.timerMinutes.collectAsState()
    val context = LocalContext.current
    PagedViewer(onBack, list, viewModel.startIndex) { t ->
        val isCurrent = player.trackId == t.id
        val isPlaying = isCurrent && player.isPlaying
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                t.title,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledIconButton(
                    onClick = {
                        if (isCurrent) MusicPlayer.togglePause()
                        else MusicPlayer.play(context, t, minutes.takeIf { it > 0 })
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Abspielen",
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(
                    onClick = { MusicPlayer.stop() },
                    enabled = isCurrent,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Filled.Stop, contentDescription = "Stopp")
                }
            }
            player.message?.let { text ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(8.dp))
            TimerMenuButton()
        }
    }
}

/** Briefe lesbar und ohne Bedienelemente. */
@Composable
fun LetterViewScreen(onBack: () -> Unit, viewModel: AnkerItemViewModel = viewModel()) {
    val list by viewModel.letters.collectAsState()
    PagedViewer(onBack, list, viewModel.startIndex) { l ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.small),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Text(l.title, style = MaterialTheme.typography.headlineSmall)
            Text(
                formatDate(l.createdAt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ClearCard { Text(l.text, style = MaterialTheme.typography.bodyLarge) }
        }
    }
}
