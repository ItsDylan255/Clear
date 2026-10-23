package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clear.model.Situation
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/**
 * Mein Anker: Erfolge und gute Dinge, Ideen, Notfallkarte und die vier Inhalte
 * Zitate, Bilder, Musik und Briefe. Die Emotions-Ansichten erreichst du ueber den Akut-Tab.
 */
@Composable
fun AnkerScreen(
    onOpenEvening: () -> Unit,
    onOpenIdeas: () -> Unit,
    onOpenEmergency: () -> Unit,
    onOpenType: (AnkerType) -> Unit
) {
    ClearScreen(title = "Mein Anker") {
        AnkerEntryCard(Icons.Outlined.WbSunny, "Erfolge und gute Dinge", "Dein Abend-Archiv", onOpenEvening)
        AnkerEntryCard(Icons.Outlined.Lightbulb, "Ideen", "Für deine Check-in-Bereiche", onOpenIdeas)
        AnkerEntryCard(Icons.Outlined.Phone, "Notfallkarte", "Person, Plan und Warnzeichen", onOpenEmergency)
        AnkerEntryCard(Icons.Outlined.FormatQuote, "Zitate", "Gedanken, die dir helfen") {
            onOpenType(AnkerType.Quotes)
        }
        AnkerEntryCard(Icons.Outlined.Image, "Bilder", "Bilder, die dir Halt geben") {
            onOpenType(AnkerType.Images)
        }
        AnkerEntryCard(Icons.Outlined.MusicNote, "Musik", "Deine eigene Musik") {
            onOpenType(AnkerType.Music)
        }
        AnkerEntryCard(Icons.Outlined.Email, "Briefe", "Briefe an dich selbst") {
            onOpenType(AnkerType.Letters)
        }
    }
}

@Composable
private fun AnkerEntryCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    ClearCard(onClick = onClick, containerColor = ClearSurfaceSoft) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Icon(icon, contentDescription = null, tint = ClearTextDark)
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
internal fun EmotionTile(situation: Situation, onClick: () -> Unit, modifier: Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().heightIn(min = 84.dp).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
        ) {
            Icon(
                situation.icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                situation.name,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
