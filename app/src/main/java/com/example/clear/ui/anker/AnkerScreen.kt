package com.example.clear.ui.anker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
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
import com.example.clear.ui.components.ClearFitCard
import com.example.clear.ui.components.ClearFitScreen
import com.example.clear.ui.components.ClearTile
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/**
 * Mein Anker: Erfolge und gute Dinge, Ideen, Notfallkarte und die vier Inhalte Zitate, Bilder,
 * Musik und Briefe. Alles passt auf eine Seite, ohne Scrollen. Die Emotions-Ansichten erreichst
 * du ueber den Akut-Tab.
 */
@Composable
fun AnkerScreen(
    onOpenEvening: () -> Unit,
    onOpenIdeas: () -> Unit,
    onOpenEmergency: () -> Unit,
    onOpenType: (AnkerType) -> Unit
) {
    ClearFitScreen(title = "Mein Anker", weightSum = 7f, gaps = 6) { unit ->
        val compact = unit < 66.dp
        val item = Modifier.fillMaxWidth().weight(1f)
        ClearFitCard(Icons.Outlined.WbSunny, "Erfolge und gute Dinge", "Dein Abend-Archiv", compact, onOpenEvening, item)
        ClearFitCard(Icons.Outlined.Lightbulb, "Ideen", "Für deine Bereiche", compact, onOpenIdeas, item)
        ClearFitCard(Icons.Outlined.Phone, "Notfallkarte", "Person, Plan und Warnzeichen", compact, onOpenEmergency, item)
        ClearFitCard(Icons.Outlined.FormatQuote, "Zitate", "Gedanken, die dir helfen", compact, { onOpenType(AnkerType.Quotes) }, item)
        ClearFitCard(Icons.Outlined.Image, "Bilder", "Bilder, die dir Halt geben", compact, { onOpenType(AnkerType.Images) }, item)
        ClearFitCard(Icons.Outlined.MusicNote, "Musik", "Deine eigene Musik", compact, { onOpenType(AnkerType.Music) }, item)
        ClearFitCard(Icons.Outlined.Email, "Briefe", "Briefe an dich selbst", compact, { onOpenType(AnkerType.Letters) }, item)
    }
}

@Composable
internal fun EmotionTile(situation: Situation, onClick: () -> Unit, modifier: Modifier) {
    ClearTile(icon = situation.icon, title = situation.name, onClick = onClick, modifier = modifier)
}
