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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Phone
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
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

@Composable
fun AnkerScreen(
    onOpenQuotes: () -> Unit,
    onOpenLetters: () -> Unit,
    onOpenImages: () -> Unit,
    onOpenMusic: () -> Unit
) {
    ClearScreen(title = "Mein Anker", intro = "Was dir Halt geben kann.") {
        AnkerCard(Icons.Outlined.FormatQuote, "Zitate und Gedanken",
            "Worte, die dir guttun", onOpenQuotes)
        AnkerCard(Icons.Outlined.Image, "Meine Bilder",
            "Bilder von sicheren Orten und Menschen", onOpenImages)
        AnkerCard(Icons.Outlined.MusicNote, "Meine Musik",
            "Musik, die dich beruhigt", onOpenMusic)
        AnkerCard(Icons.Outlined.Mail, "Briefe an mich selbst",
            "Texte, die du dir selbst schreibst", onOpenLetters)
    }
}

/** onClick == null bedeutet: kommt in einem spaeteren Teil. */
@Composable
private fun AnkerCard(icon: ImageVector, title: String, description: String, onClick: (() -> Unit)?) {
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
                    if (onClick == null) "Folgt bald" else description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onClick != null) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
