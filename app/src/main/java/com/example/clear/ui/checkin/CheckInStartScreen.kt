package com.example.clear.ui.checkin

import com.example.clear.ui.components.pressScale
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.domain.CategoryLevel
import com.example.clear.domain.CategoryLevelInfo
import com.example.clear.domain.LevelCalculator
import com.example.clear.model.CheckInCategory
import com.example.clear.tr
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearLevelHighAccent
import com.example.clear.ui.theme.ClearLevelLow
import com.example.clear.ui.theme.ClearLevelLowAccent
import com.example.clear.ui.theme.ClearLevelMedium
import com.example.clear.ui.theme.ClearLevelMediumAccent
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearTextDark

/** Durchschnitt aller Bereiche, die in den letzten 14 Tagen Werte haben (oder null). */
fun overallAverage(levels: Map<String, CategoryLevelInfo?>?): Double? {
    val averages = levels?.values?.filterNotNull()?.map { it.average }.orEmpty()
    return if (averages.isEmpty()) null else LevelCalculator.round1(averages.average())
}

/** Kurze Zeile fuer den Gesamtstand aller Bereiche, z. B. "Alle Bereiche: Ø 6,2 · Stabil". */
@Composable
fun OverallLevelLine(levels: Map<String, CategoryLevelInfo?>?, modifier: Modifier = Modifier) {
    val overall = overallAverage(levels)
    val overallLevel = overall?.let { LevelCalculator.levelOf(it) }
    if (overall != null && overallLevel != null) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            LevelDot(overallLevel)
            Text(
                tr(
                    "Alle Bereiche: Ø ${formatAverage(overall)} · ${levelName(overallLevel)}",
                    "All areas: avg ${formatAverage(overall)} · ${levelName(overallLevel)}"
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Text(
            tr("Noch keine Werte in den letzten 14 Tagen", "No values in the last 14 days"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
    }
}

/**
 * Die acht Bereiche mit ihrem Level, erreichbar ueber die Startseite. Unter dem Titel steht klein
 * der Durchschnitt aller Bereiche. Die Karten haben eine feste Hoehe, die Seite scrollt.
 */
@Composable
fun CheckInStartScreen(
    onBack: () -> Unit,
    onOpenCategory: (CheckInCategory) -> Unit,
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val levels by warningsViewModel.levels.collectAsState()

    ClearDetailScreen(title = tr("Bereiche", "Areas"), onBack = onBack) {
        OverallLevelLine(levels)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CheckInCategory.entries.forEach { category ->
                CategoryCard(
                    category = category,
                    info = levels?.get(category.id),
                    compact = false,
                    modifier = Modifier.fillMaxWidth()
                ) { onOpenCategory(category) }
            }
        }
        Spacer(Modifier.height(ClearSpacing.small))
    }
}

@Composable
internal fun LevelDot(level: CategoryLevel) {
    val color = when (level) {
        CategoryLevel.Low -> ClearLevelLowAccent
        CategoryLevel.Medium -> ClearLevelMediumAccent
        CategoryLevel.High -> ClearLevelHighAccent
    }
    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
}

@Composable
private fun CategoryCard(
    category: CheckInCategory,
    info: CategoryLevelInfo?,
    compact: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val container = when (info?.level) {
        CategoryLevel.Low -> ClearLevelLow
        CategoryLevel.Medium -> ClearLevelMedium
        else -> ClearCardColor
    }
    val interaction = remember { MutableInteractionSource() }
    Card(
        onClick = onClick,
        modifier = modifier.pressScale(interaction),
        interactionSource = interaction,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container, contentColor = ClearTextDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 76.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 32.dp else 44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(if (compact) 18.dp else 24.dp)
                )
            }
            if (compact) {
                // Eng: Name und Level in einer Zeile
                Text(
                    category.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                levelLabel(info)?.let { label ->
                    LevelLine(info?.level, label)
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        category.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    levelLabel(info)?.let { label -> LevelLine(info?.level, label) }
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

@Composable
private fun LevelLine(level: CategoryLevel?, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (level != null) LevelDot(level)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = ClearTextDark, maxLines = 1)
    }
}
