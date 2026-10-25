package com.example.clear.ui.meditation

import androidx.compose.foundation.BorderStroke
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
import com.example.clear.ui.components.ClearFillScreen
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/**
 * Die zehn Meditationen als Raster (2 Spalten x 5 Reihen). Die Reihen fuellen die Hoehe bis
 * zur unteren Leiste, ohne zu scrollen. Auf kleinen Bildschirmen scrollt die Seite als Rueckfall.
 */
@Composable
fun MeditationScreen(onOpenMeditation: (Meditation) -> Unit) {
    val rows = Meditations.all.chunked(2)
    ClearFillScreen(
        title = "Meditieren",
        weightSum = rows.size.toFloat(),
        gaps = rows.size - 1,
        itemSpacing = 10.dp,
        fixedHeight = 0.dp,
        minUnit = 84.dp
    ) { unit ->
        rows.forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth().height(unit),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { meditation ->
                    MeditationTile(
                        meditation = meditation,
                        height = unit,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) { onOpenMeditation(meditation) }
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MeditationTile(
    meditation: Meditation,
    height: Dp,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val iconCircle = (height * 0.42f).coerceIn(34.dp, 50.dp)
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = ClearCardColor, contentColor = ClearTextDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
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
