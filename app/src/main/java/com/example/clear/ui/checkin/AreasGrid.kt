package com.example.clear.ui.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.clear.domain.CategoryLevel
import com.example.clear.domain.CategoryLevelInfo
import com.example.clear.domain.LevelCalculator
import com.example.clear.model.CheckInCategory
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.ui.components.pressScale
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearLevelHighAccent
import com.example.clear.ui.theme.ClearLevelLow
import com.example.clear.ui.theme.ClearLevelLowAccent
import com.example.clear.ui.theme.ClearLevelMedium
import com.example.clear.ui.theme.ClearLevelMediumAccent
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

/** Durchschnitt aller Bereiche, die in den letzten 14 Tagen Werte haben (oder null). */
fun overallAverage(levels: Map<String, CategoryLevelInfo?>?): Double? {
    val averages = levels?.values?.filterNotNull()?.map { it.average }.orEmpty()
    return if (averages.isEmpty()) null else LevelCalculator.round1(averages.average())
}

/** Pfeil zum Trend, oder null ohne Trend (keine oder nur eine Bewertung). */
private fun trendArrow(trend: Trend?): String? = when (trend) {
    Trend.Up -> "↑"
    Trend.Down -> "↓"
    Trend.Flat -> "→"
    else -> null
}

/** Eigene Seite "Uebersicht", erreichbar ueber den Eintrag im Reiter Check-in. */
@Composable
fun AreasScreen(
    onBack: () -> Unit,
    onOpenCategory: (CheckInCategory) -> Unit,
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val levels by warningsViewModel.levels.collectAsState()
    val trends by warningsViewModel.areaTrends.collectAsState()
    ClearDetailScreen(title = tr("Übersicht", "Overview"), onBack = onBack) {
        AreasGrid(levels = levels, trends = trends, onOpenCategory = onOpenCategory)
    }
}

/**
 * Das Raster der Kategorien: oben der Gesamtdurchschnitt, darunter die acht Bereiche als
 * Raster in zwei Spalten. Jede Kachel zeigt Name, 14-Tage-Durchschnitt, Trendpfeil und Mini-Linie.
 */
@Composable
fun AreasGrid(
    levels: Map<String, CategoryLevelInfo?>?,
    trends: Map<String, AreaTrend>,
    onOpenCategory: (CheckInCategory) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OverallCard(overallAverage(levels), trends[CheckInWarningsViewModel.OVERALL]?.trend)
        CheckInCategory.entries.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { category ->
                    AreaTile(
                        category = category,
                        info = levels?.get(category.id),
                        trend = trends[category.id],
                        modifier = Modifier.weight(1f)
                    ) { onOpenCategory(category) }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun OverallCard(average: Double?, trend: Trend?) {
    ClearCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                tr("Alle Kategorien", "All categories"),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            if (average != null) {
                LevelDot(LevelCalculator.levelOf(average))
                Spacer(Modifier.size(8.dp))
            }
            Text(
                average?.let { "Ø ${formatAverage(it)}" } ?: "–",
                style = MaterialTheme.typography.headlineSmall
            )
            trendArrow(trend)?.let { arrow ->
                Text(
                    arrow,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun AreaTile(
    category: CheckInCategory,
    info: CategoryLevelInfo?,
    trend: AreaTrend?,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(
                category.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    info?.let { formatAverage(it.average) } ?: "–",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )
                trendArrow(trend?.trend)?.let { arrow ->
                    Text(arrow, style = MaterialTheme.typography.headlineSmall, color = ClearTextDark.copy(alpha = 0.7f))
                }
            }
            Sparkline(
                points = trend?.points.orEmpty(),
                lineColor = MaterialTheme.colorScheme.primary,
                guideColor = ClearSurfaceSoft,
                modifier = Modifier.fillMaxWidth().height(32.dp)
            )
        }
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
