package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clear.model.Situation
import com.example.clear.model.Situations
import com.example.clear.tr
import com.example.clear.ui.components.ClearFitCard
import com.example.clear.ui.components.ClearFitScreen
import com.example.clear.ui.components.ClearTile
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearNoticeSoft
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

@Composable
fun AkutScreen(
    onOpenSituation: (Situation) -> Unit,
    onOpenThoughts: () -> Unit,
    onOpenEmergency: () -> Unit
) {
    val unknown = Situations.all.firstOrNull { it.id == "unknown" }
    val others = Situations.all.filter { it.id != "unknown" }
    val rows = others.chunked(2)

    // Alles passt auf eine Seite: "Allgemein", vier Reihen Emotionen, zwei Karten unten
    val tileRows = (if (unknown != null) 1 else 0) + rows.size
    ClearFitScreen(
        title = tr("Akut", "Acute"),
        weightSum = tileRows + 2 * 0.9f,
        gaps = tileRows + 2 - 1
    ) { unit ->
        val compactTiles = unit < 100.dp
        val compactCards = unit * 0.9f < 66.dp

        if (unknown != null) {
            SituationTile(
                situation = unknown,
                compact = compactTiles,
                onClick = { onOpenSituation(unknown) },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
        }
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { situation ->
                    SituationTile(
                        situation = situation,
                        compact = compactTiles,
                        onClick = { onOpenSituation(situation) },
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }

        ClearFitCard(
            icon = Icons.Outlined.Psychology,
            title = tr("Gedankenprotokoll", "Thought record"),
            subtitle = null,
            compact = compactCards,
            onClick = onOpenThoughts,
            modifier = Modifier.fillMaxWidth().weight(0.9f)
        )
        ClearFitCard(
            icon = Icons.Filled.Phone,
            title = tr("Notfallkarte", "Emergency card"),
            subtitle = null,
            compact = compactCards,
            onClick = onOpenEmergency,
            modifier = Modifier.fillMaxWidth().weight(0.9f),
            containerColor = ClearNoticeSoft
        )
    }
}

@Composable
private fun SituationTile(
    situation: Situation,
    compact: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    ClearTile(
        icon = situation.icon,
        title = situation.name,
        onClick = onClick,
        modifier = modifier,
        fill = true,
        compact = compact
    )
}
