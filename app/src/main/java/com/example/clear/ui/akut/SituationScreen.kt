package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.clear.model.Exercise
import com.example.clear.model.Situation
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.clear.data.FileStore

@Composable
fun SituationScreen(
    situation: Situation,
    onBack: () -> Unit,
    onOpenExercise: (Exercise) -> Unit,
    onOpenMusic: () -> Unit,
    viewModel: SituationContentViewModel = viewModel()
) {
    val content by viewModel.state.collectAsState()
    val context = LocalContext.current

    ClearDetailScreen(title = situation.name, onBack = onBack) {
        situation.intro?.let { intro ->
            ClearCard(containerColor = ClearSurfaceSoft) {
                Text(intro, style = MaterialTheme.typography.bodyLarge)
            }
        }
        situation.hint?.let { hint ->
            ClearCard(containerColor = ClearWarningSoft) {
                Text(hint, style = MaterialTheme.typography.bodyLarge)
            }
        }

        // Persoenliche Inhalte (alles freiwillig, nichts startet von selbst)
        content.quote?.let { quote ->
            ClearCard(containerColor = ClearSurfaceSoft) {
                Text(quote.text, style = MaterialTheme.typography.bodyLarge)
                if (!quote.author.isNullOrBlank()) {
                    Text(
                        "– ${quote.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        content.image?.let { image ->
            val file = remember(image.fileName) {
                FileStore.file(context, FileStore.IMAGES, image.fileName)
            }
            if (file.exists()) {
                ClearCard {
                    AsyncImage(
                        model = file,
                        contentDescription = image.caption ?: "Dein Bild",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                            .clip(MaterialTheme.shapes.small)
                    )
                }
            }
        }
        // Eigene Musik hoeren (Seite mit zwei Wegen und Timer)
        ClearCard(onClick = onOpenMusic) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
            ) {
                Icon(Icons.Outlined.MusicNote, contentDescription = null)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Musik hören", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Mit deiner eigenen Musik",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        Text(
            "Wähle eine Übung. Du kannst jederzeit abbrechen.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        situation.exercises.forEach { exercise ->
            ClearCard(onClick = { onOpenExercise(exercise) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(exercise.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            exercise.duration,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
