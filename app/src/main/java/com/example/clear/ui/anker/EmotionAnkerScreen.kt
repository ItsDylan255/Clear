package com.example.clear.ui.anker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.AppLanguage
import coil.compose.AsyncImage
import com.example.clear.data.AnchorImage
import com.example.clear.data.FileStore
import com.example.clear.data.Letter
import com.example.clear.data.MusicTrack
import com.example.clear.data.Quote
import com.example.clear.model.MusicMode
import com.example.clear.model.Situation
import com.example.clear.playback.MusicPlayer
import com.example.clear.tr
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearTextDark
import java.text.DateFormat
import java.util.Date

// Hoehenanteile der Bereiche, von oben nach unten: Musik, Bilder, Zitate, Briefe.
// Fehlt ein Typ, bleibt sein Platz frei (die anderen behalten ihre Groesse).
private const val W_MUSIC = 1.8f
private const val W_IMAGE = 1.4f
private const val W_QUOTE = 1.0f
private const val W_LETTER = 0.8f

private fun formatDate(timestamp: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, AppLanguage.locale).format(Date(timestamp))

/**
 * Ruhige Ansicht der persoenlichen Inhalte zu einer Emotion. Kein Scrollen: jeder vorhandene
 * Inhaltstyp zeigt ein Element als Vorschau, per Wischen wechselt man zum naechsten.
 * Bei Musik waehlt man zwischen "Gefuehle zulassen" und "Stimmung veraendern" und stellt den
 * Timer. Hinzufuegen, Bearbeiten und Loeschen gibt es hier nicht.
 * Die Oeffnen-Rueckrufe bekommen die Reihenfolge aller Elemente mit (zum Wischen in der Einzelansicht).
 */
@Composable
fun EmotionAnkerScreen(
    situation: Situation,
    onBack: () -> Unit,
    onOpenQuote: (Long, List<Long>) -> Unit,
    onOpenImage: (Long, List<Long>) -> Unit,
    onOpenTrack: (Long, List<Long>) -> Unit,
    onOpenLetter: (Long, List<Long>) -> Unit,
    viewModel: EmotionAnkerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val content = state

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Zurück", "Back"))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = ClearSpacing.large)
                .padding(bottom = ClearSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(situation.name, style = MaterialTheme.typography.headlineMedium)
            if (content != null) {
                if (content.isEmpty) {
                    Text(
                        tr(
                            "Hier ist noch nichts gespeichert. Du kannst Inhalte unter Mein Anker hinzufügen.",
                            "Nothing saved here yet. You can add content under My Anchor."
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    var missing = 0f
                    if (content.tracks.isNotEmpty()) {
                        MusicSection(content.tracks, Modifier.weight(W_MUSIC), onOpenTrack)
                    } else missing += W_MUSIC
                    if (content.images.isNotEmpty()) {
                        ImagePreview(content.images, Modifier.weight(W_IMAGE), onOpenImage)
                    } else missing += W_IMAGE
                    if (content.quotes.isNotEmpty()) {
                        QuotePreview(content.quotes, Modifier.weight(W_QUOTE), onOpenQuote)
                    } else missing += W_QUOTE
                    if (content.letters.isNotEmpty()) {
                        LetterPreview(content.letters, Modifier.weight(W_LETTER), onOpenLetter)
                    } else missing += W_LETTER
                    if (missing > 0f) Spacer(Modifier.weight(missing))
                }
            }
        }
    }
}

/** Kopfzeile eines Bereichs: Symbol, Name und "2 von 5". */
@Composable
private fun SectionHeader(icon: ImageVector, label: String, position: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        if (position != null) {
            Text(
                position,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Rahmen eines Bereichs: Kopfzeile, darunter wischbare Vorschau. */
@Composable
private fun <T> PreviewSection(
    icon: ImageVector,
    label: String,
    items: List<T>,
    modifier: Modifier,
    page: @Composable (T) -> Unit
) {
    val pager = rememberPagerState(pageCount = { items.size })
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = ClearCardColor, contentColor = ClearTextDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SectionHeader(
                icon, label,
                if (items.size > 1) tr(
                    "${pager.currentPage + 1} von ${items.size}",
                    "${pager.currentPage + 1} of ${items.size}"
                ) else null
            )
            HorizontalPager(
                state = pager,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                pageSpacing = 12.dp
            ) { index -> page(items[index]) }
        }
    }
}

/** Musik: Wahl "Gefuehle zulassen" / "Stimmung veraendern", Timer und wischbare Titel. */
@Composable
private fun MusicSection(
    all: List<MusicTrack>,
    modifier: Modifier,
    onOpen: (Long, List<Long>) -> Unit
) {
    val context = LocalContext.current
    val player by MusicPlayer.state.collectAsState()
    val mode by MusicChoice.mode.collectAsState()
    val minutes by MusicChoice.timerMinutes.collectAsState()

    // Titel ohne gewaehlte Art gelten fuer beide Wege
    val tracks = all.filter { track ->
        val trackMode = MusicMode.parse(track.moodGoals)
        trackMode == null || trackMode == mode
    }
    val pager = rememberPagerState(pageCount = { tracks.size })
    LaunchedEffect(mode) {
        if (tracks.isNotEmpty()) pager.scrollToPage(0)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = ClearCardColor, contentColor = ClearTextDark)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SectionHeader(
                Icons.Outlined.MusicNote, tr("Musik", "Music"),
                if (tracks.size > 1) tr(
                    "${pager.currentPage + 1} von ${tracks.size}",
                    "${pager.currentPage + 1} of ${tracks.size}"
                ) else null
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MusicMode.entries.forEach { option ->
                    FilterChip(
                        selected = option == mode,
                        onClick = { MusicChoice.mode.value = option },
                        label = {
                            Text(
                                option.shortTitle,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (tracks.isEmpty()) {
                Text(
                    tr("Keine Musik für diese Auswahl.", "No music for this selection."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                HorizontalPager(
                    state = pager,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    pageSpacing = 12.dp
                ) { index ->
                    val track = tracks[index]
                    val isCurrent = player.trackId == track.id
                    val isPlaying = isCurrent && player.isPlaying
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isCurrent) MusicPlayer.togglePause()
                                else MusicPlayer.play(context, track, minutes.takeIf { it > 0 })
                            }
                        ) {
                            Icon(
                                if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription =
                                    if (isPlaying) tr("Pause: ${track.title}", "Pause: ${track.title}") else tr("Abspielen: ${track.title}", "Play: ${track.title}")
                            )
                        }
                        Text(
                            track.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(onClickLabel = tr("Öffnen", "Open")) {
                                    onOpen(track.id, tracks.map { it.id })
                                }
                        )
                        TimerMenuButton()
                    }
                }
            }
        }
    }
}

@Composable
private fun QuotePreview(quotes: List<Quote>, modifier: Modifier, onOpen: (Long, List<Long>) -> Unit) {
    PreviewSection(Icons.Outlined.FormatQuote, tr("Zitate", "Quotes"), quotes, modifier) { quote ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().clickable(onClickLabel = tr("Öffnen", "Open")) {
                onOpen(quote.id, quotes.map { it.id })
            }
        ) {
            val hasAuthor = !quote.author.isNullOrBlank()
            val lineHeight = with(LocalDensity.current) {
                MaterialTheme.typography.bodyLarge.lineHeight.toDp()
            }
            val lines = ((maxHeight - if (hasAuthor) 22.dp else 0.dp) / lineHeight)
                .toInt().coerceAtLeast(1)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    quote.text,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = lines,
                    overflow = TextOverflow.Ellipsis
                )
                if (hasAuthor) {
                    Text(
                        "– ${quote.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ImagePreview(images: List<AnchorImage>, modifier: Modifier, onOpen: (Long, List<Long>) -> Unit) {
    val context = LocalContext.current
    PreviewSection(Icons.Outlined.Image, tr("Bilder", "Images"), images, modifier) { image ->
        val file = remember(image.fileName) {
            FileStore.file(context, FileStore.IMAGES, image.fileName)
        }
        if (file.exists()) {
            AsyncImage(
                model = file,
                contentDescription = image.caption ?: tr("Dein Bild", "Your image"),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClickLabel = tr("Öffnen", "Open")) { onOpen(image.id, images.map { it.id }) }
            )
        } else {
            Text(
                tr("Diese Bilddatei wurde nicht gefunden.", "This image file was not found."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LetterPreview(letters: List<Letter>, modifier: Modifier, onOpen: (Long, List<Long>) -> Unit) {
    PreviewSection(Icons.Outlined.Mail, tr("Briefe", "Letters"), letters, modifier) { letter ->
        Column(
            modifier = Modifier.fillMaxSize().clickable(onClickLabel = tr("Öffnen", "Open")) {
                onOpen(letter.id, letters.map { it.id })
            },
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                letter.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                formatDate(letter.createdAt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
