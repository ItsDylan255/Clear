package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSpacing
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

    ClearScreen(title = "Akut") {
        // 1. "Ich weiß nicht genau" ganz oben, volle Breite
        if (unknown != null) {
            SituationTile(
                situation = unknown,
                onClick = { onOpenSituation(unknown) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 2. Raster mit zwei Spalten (die acht Situationen)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            others.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { situation ->
                        SituationTile(
                            situation = situation,
                            onClick = { onOpenSituation(situation) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Gedankenprotokoll
        ClearCard(onClick = onOpenThoughts, containerColor = ClearSurfaceSoft) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
            ) {
                Icon(Icons.Outlined.Psychology, contentDescription = null, tint = ClearTextDark)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gedankenprotokoll", style = MaterialTheme.typography.titleMedium)
                    Text("Gedanken ordnen und neu betrachten", style = MaterialTheme.typography.bodyMedium)
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        // 4. Notfallkarte ganz unten
        ClearCard(onClick = onOpenEmergency, containerColor = ClearWarningSoft) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
            ) {
                Icon(Icons.Filled.Phone, contentDescription = null, tint = ClearTextDark)
                Column(modifier = Modifier.weight(1f)) {
                    Text("Notfallkarte", style = MaterialTheme.typography.titleMedium)
                    Text("Wichtige Nummern und Kontakte", style = MaterialTheme.typography.bodyMedium)
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
    }
}

@Composable
private fun SituationTile(situation: Situation, onClick: () -> Unit, modifier: Modifier) {
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
