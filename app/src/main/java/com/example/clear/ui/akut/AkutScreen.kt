package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clear.model.Situation
import com.example.clear.model.Situations
import com.example.clear.tr
import com.example.clear.ui.components.ClearNavCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.components.ClearTile
import com.example.clear.ui.theme.ClearNoticeSoft

@Composable
fun AkutScreen(
    onOpenSituation: (Situation) -> Unit,
    onOpenThoughts: () -> Unit,
    onOpenEmergency: () -> Unit
) {
    val unknown = Situations.all.firstOrNull { it.id == "unknown" }
    val others = Situations.all.filter { it.id != "unknown" }

    // Gefuehle als Raster und das Protokoll scrollen. Die Notfallkarte steht fest unten
    // ueber der Tab-Leiste, damit sie in einer Krise immer ohne Suchen sichtbar ist.
    ClearScreen(
        title = tr("Akut", "Acute"),
        bottomBar = {
            ClearNavCard(
                icon = Icons.Filled.Phone,
                title = tr("Notfallkarte", "Emergency card"),
                subtitle = tr("Person, Plan und Warnzeichen", "Contact, plan and warning signs"),
                onClick = onOpenEmergency,
                containerColor = ClearNoticeSoft
            )
        }
    ) {
        if (unknown != null) {
            ClearTile(
                icon = unknown.icon,
                title = unknown.name,
                onClick = { onOpenSituation(unknown) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(TILE_GAP)) {
            others.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(TILE_GAP)
                ) {
                    rowItems.forEach { situation ->
                        ClearTile(
                            icon = situation.icon,
                            title = situation.name,
                            onClick = { onOpenSituation(situation) },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                    if (rowItems.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }
        ClearNavCard(
            icon = Icons.Outlined.Psychology,
            title = tr("Gedankenprotokoll", "Thought record"),
            subtitle = tr("Einen belastenden Gedanken prüfen", "Examine a distressing thought"),
            onClick = onOpenThoughts
        )
    }
}

private val TILE_GAP = 12.dp
