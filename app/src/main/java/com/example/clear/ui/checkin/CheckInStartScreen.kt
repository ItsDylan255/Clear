package com.example.clear.ui.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.domain.CategoryLevel
import com.example.clear.domain.CategoryLevelInfo
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.akut.ClearWarningSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearFillScreen
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/**
 * Acht Bereiche als Karten und unten der Button. Alles fuellt die Hoehe bis zur unteren Leiste,
 * ohne zu scrollen. Ist heute schon eingecheckt, oeffnet der Button den Verlauf.
 * Auf kleinen Bildschirmen scrollt die Seite als Rueckfall.
 */
@Composable
fun CheckInStartScreen(
    onStartCheckIn: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenQuestionnaires: () -> Unit,
    onOpenCategory: (CheckInCategory) -> Unit,
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val levels by warningsViewModel.levels.collectAsState()
    val checkedInToday by warningsViewModel.checkedInToday.collectAsState()
    fun infoOf(category: CheckInCategory): CategoryLevelInfo? = levels?.get(category.id)

    // Acht Bereichskarten, Fragebogen und Button: neun Abstaende
    val gaps = 9

    // Gewichte: acht Karten je 1 + Fragebogen 0,9 + Button 0,85
    ClearFillScreen(
        title = "Check In",
        weightSum = 9.75f,
        gaps = gaps,
        itemSpacing = 8.dp,
        fixedHeight = 0.dp,
        minUnit = 48.dp
    ) { unit ->
        CheckInCategory.entries.forEach { category ->
            CategoryCard(category, info = infoOf(category), minHeight = unit) {
                onOpenCategory(category)
            }
        }

        QuestionnairesCard(minHeight = unit * 0.9f, onClick = onOpenQuestionnaires)

        // Button ganz unten
        Button(
            onClick = if (checkedInToday) onOpenHistory else onStartCheckIn,
            modifier = Modifier.fillMaxWidth().heightIn(min = maxOf(unit * 0.85f, 48.dp)),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                if (checkedInToday) "Verlauf ansehen" else "Check-in starten",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: CheckInCategory,
    info: CategoryLevelInfo?,
    minHeight: Dp,
    onClick: () -> Unit
) {
    val container = when (info?.level) {
        CategoryLevel.Low -> ClearWarningSoft
        CategoryLevel.Medium -> ClearLevelMedium
        else -> ClearCardColor
    }
    val iconCircle = (minHeight * 0.6f).coerceIn(32.dp, 48.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container, contentColor = ClearTextDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(iconCircle)
                    .clip(CircleShape)
                    .background(ClearSurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, tint = ClearTextDark)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(category.title, style = MaterialTheme.typography.titleMedium)
                levelLabel(info)?.let { label ->
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = ClearTextDark
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Karte zu den monatlichen Frageboegen (PHQ-9 und WHO-5). Der Status steht in der Uebersicht dort. */
@Composable
private fun QuestionnairesCard(minHeight: Dp, onClick: () -> Unit) {
    val iconCircle = (minHeight * 0.6f).coerceIn(32.dp, 48.dp)
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = ClearSurfaceSoft, contentColor = ClearTextDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(iconCircle).clip(CircleShape).background(ClearCardColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = ClearTextDark)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Fragebögen", style = MaterialTheme.typography.titleMedium)
                Text(
                    "PHQ-9, WHO-5 und WSAS",
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
