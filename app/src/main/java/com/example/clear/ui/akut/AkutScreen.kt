package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clear.model.Situation
import com.example.clear.model.Situations
import com.example.clear.tr
import com.example.clear.ui.components.ClearNavCard
import com.example.clear.ui.components.ClearTile
import com.example.clear.ui.theme.ClearSpacing

/**
 * Akut: Alles passt auf eine Seite, nichts scrollt. "Allgemein", vier Reihen Gefuehle, das
 * Gedankenprotokoll und ganz unten die Notfallkarte, damit sie in einer Krise immer sichtbar ist.
 * Die Kacheln teilen sich die freie Hoehe.
 */
@Composable
fun AkutScreen(
    onOpenSituation: (Situation) -> Unit,
    onOpenThoughts: () -> Unit,
    onOpenEmergency: () -> Unit
) {
    val unknown = Situations.all.firstOrNull { it.id == "unknown" }
    val rows = Situations.all.filter { it.id != "unknown" }.chunked(2)
    val tileRows = (if (unknown != null) 1 else 0) + rows.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium)
    ) {
        Text(tr("Akut", "Acute"), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(ClearSpacing.medium))
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Hoehe einer Kachelreihe: freie Hoehe minus Abstaende und die zwei Karten unten
            val tileHeight = (maxHeight - TILE_GAP * (tileRows + 1) - CARD_HEIGHT * 2) / tileRows
            val compact = tileHeight < 104.dp
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(TILE_GAP)
            ) {
                if (unknown != null) {
                    ClearTile(
                        icon = unknown.icon,
                        title = unknown.name,
                        onClick = { onOpenSituation(unknown) },
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        compact = compact
                    )
                }
                rows.forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(TILE_GAP)
                    ) {
                        rowItems.forEach { situation ->
                            ClearTile(
                                icon = situation.icon,
                                title = situation.name,
                                onClick = { onOpenSituation(situation) },
                                modifier = Modifier.weight(1f),
                                compact = compact
                            )
                        }
                        if (rowItems.size < 2) Spacer(Modifier.weight(1f))
                    }
                }
                ClearNavCard(
                    icon = Icons.Outlined.Psychology,
                    title = tr("Gedankenprotokoll", "Thought record"),
                    subtitle = null,
                    onClick = onOpenThoughts
                )
                EmergencyCardBar(onOpenEmergency)
            }
        }
    }
}

private val TILE_GAP = 12.dp
private val CARD_HEIGHT = 76.dp
