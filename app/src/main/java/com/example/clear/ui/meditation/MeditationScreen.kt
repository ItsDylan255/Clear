package com.example.clear.ui.meditation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.clear.model.Meditation
import com.example.clear.model.Meditations
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

@Composable
fun MeditationScreen(onOpenMeditation: (Meditation) -> Unit) {
    ClearScreen(title = "Meditieren", intro = "Ein Moment nur für dich.") {
        // Ruhige Einfuehrungskarte
        ClearCard(containerColor = ClearSurfaceSoft) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
            ) {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(ClearCardColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Spa, contentDescription = null, tint = ClearTextDark)
                }
                Column {
                    Text("Finde deine Ruhe", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Zehn Minuten, in deinem eigenen Tempo.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Meditations.all.forEach { meditation ->
            MeditationCard(meditation) { onOpenMeditation(meditation) }
        }
    }
}

@Composable
private fun MeditationCard(meditation: Meditation, onClick: () -> Unit) {
    ClearCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(ClearSurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(meditation.icon, contentDescription = null, tint = ClearTextDark)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(meditation.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    meditation.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${meditation.durationMinutes} Min.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
