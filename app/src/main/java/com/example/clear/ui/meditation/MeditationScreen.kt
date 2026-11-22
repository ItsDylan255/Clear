package com.example.clear.ui.meditation

import com.example.clear.ui.components.pressScale
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clear.model.Meditation
import com.example.clear.model.Meditations
import com.example.clear.tr
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/** Die Meditationen als Raster mit zwei Spalten. Kacheln haben eine feste Hoehe, die Seite scrollt. */
@Composable
fun MeditationScreen(onOpenMeditation: (Meditation) -> Unit) {
    ClearScreen(title = tr("Meditieren", "Meditate")) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Meditations.all.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(TILE_HEIGHT),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { meditation ->
                        MeditationTile(
                            meditation = meditation,
                            height = TILE_HEIGHT,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) { onOpenMeditation(meditation) }
                    }
                    if (pair.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

private val TILE_HEIGHT = 128.dp

@Composable
private fun MeditationTile(
    meditation: Meditation,
    height: Dp,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val iconCircle = (height * 0.42f).coerceIn(34.dp, 50.dp)
    val interaction = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        modifier = modifier.pressScale(interaction),
        interactionSource = interaction,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = ClearTextDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(iconCircle).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    meditation.icon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                meditation.shortTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, lineHeight = 20.sp),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
