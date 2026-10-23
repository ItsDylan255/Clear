package com.example.clear.ui.anker

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/**
 * Inhalte verwalten, Schritt 1: den Typ waehlen. Danach folgt die Emotion, dann die Liste
 * zum Hinzufuegen, Bearbeiten und Loeschen.
 */
@Composable
fun AnkerManageScreen(
    onBack: () -> Unit,
    onOpenType: (AnkerType) -> Unit
) {
    ClearDetailScreen(title = "Inhalte verwalten", onBack = onBack) {
        ManageCard(Icons.Outlined.FormatQuote, AnkerType.Quotes.title,
            "Worte, die dir guttun") { onOpenType(AnkerType.Quotes) }
        ManageCard(Icons.Outlined.Image, AnkerType.Images.title,
            "Bilder von sicheren Orten und Menschen") { onOpenType(AnkerType.Images) }
        ManageCard(Icons.Outlined.MusicNote, AnkerType.Music.title,
            "Musik, die dich beruhigt") { onOpenType(AnkerType.Music) }
        ManageCard(Icons.Outlined.Mail, AnkerType.Letters.title,
            "Texte, die du dir selbst schreibst") { onOpenType(AnkerType.Letters) }
    }
}

@Composable
private fun ManageCard(icon: ImageVector, title: String, description: String, onClick: () -> Unit) {
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
                Icon(icon, contentDescription = null, tint = ClearTextDark)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    description,
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
