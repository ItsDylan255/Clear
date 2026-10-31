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
import com.example.clear.tr
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
    ClearFitScreen(title = tr("Mein Anker", "My Anchor"), weightSum = 7f, gaps = 6) { unit ->
        val compact = unit < 66.dp
        val item = Modifier.fillMaxWidth().weight(1f)
        ClearFitCard(Icons.Outlined.WbSunny, tr("Erfolge und gute Dinge", "Successes and good things"), tr("Dein Abend-Archiv", "Your evening archive"), compact, onOpenEvening, item)
        ClearFitCard(Icons.Outlined.Lightbulb, tr("Ideen", "Ideas"), tr("Für deine Bereiche", "For your areas"), compact, onOpenIdeas, item)
        ClearFitCard(Icons.Outlined.Phone, tr("Notfallkarte", "Emergency card"), tr("Person, Plan und Warnzeichen", "Contact, plan and warning signs"), compact, onOpenEmergency, item)
        ClearFitCard(Icons.Outlined.FormatQuote, tr("Zitate", "Quotes"), tr("Gedanken, die dir helfen", "Thoughts that help you"), compact, { onOpenType(AnkerType.Quotes) }, item)
        ClearFitCard(Icons.Outlined.Image, tr("Bilder", "Images"), tr("Bilder, die dir Halt geben", "Images that give you support"), compact, { onOpenType(AnkerType.Images) }, item)
        ClearFitCard(Icons.Outlined.MusicNote, tr("Musik", "Music"), tr("Deine eigene Musik", "Your own music"), compact, { onOpenType(AnkerType.Music) }, item)
        ClearFitCard(Icons.Outlined.Email, tr("Briefe", "Letters"), tr("Briefe an dich selbst", "Letters to yourself"), compact, { onOpenType(AnkerType.Letters) }, item)
    }
}

@Composable
internal fun EmotionTile(situation: Situation, onClick: () -> Unit, modifier: Modifier) {
    ClearTile(icon = situation.icon, title = situation.name, onClick = onClick, modifier = modifier)
}
