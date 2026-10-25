package com.example.clear.ui.checkin

import androidx.compose.foundation.BorderStroke
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
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearLevelHighAccent
import com.example.clear.ui.theme.ClearLevelLow
import com.example.clear.ui.theme.ClearLevelLowAccent
import com.example.clear.ui.theme.ClearLevelMedium
import com.example.clear.ui.theme.ClearLevelMediumAccent
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearTextDark

/**
 * Die acht Bereiche mit ihrem Level. Unter dem Titel steht klein der Durchschnitt aller Bereiche.
 * Alles passt immer auf eine Seite, ohne zu scrollen: Die Karten teilen sich die freie Hoehe zu
 * gleichen Teilen. Wird es sehr eng, steht das Level neben dem Namen statt darunter.
 * Der Check-in selbst startet auf der Home Page.
 */
@Composable
fun CheckInStartScreen(
    onOpenCategory: (CheckInCategory) -> Unit,
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val levels by warningsViewModel.levels.collectAsState()

    // Durchschnitt aller Bereiche, die in den letzten 14 Tagen Werte haben
    val averages = levels?.values?.filterNotNull()?.map { it.average }.orEmpty()
    val overall = if (averages.isEmpty()) null else LevelCalculator.round1(averages.average())
    val overallLevel = overall?.let { LevelCalculator.levelOf(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ClearSpacing.large)
            .padding(top = ClearSpacing.medium, bottom = ClearSpacing.medium)
    ) {
        Text("Bereiche", style = MaterialTheme.typography.headlineMedium)
        if (overall != null && overallLevel != null) {
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LevelDot(overallLevel)
                Text(
                    "Alle Bereiche: Ø ${formatAverage(overall)} · ${levelName(overallLevel)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Text(
                "Noch keine Werte in den letzten 14 Tagen",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Spacer(Modifier.height(ClearSpacing.medium))

        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val gap = 8.dp
            val count = CheckInCategory.entries.size
            val cardHeight = (maxHeight - gap * (count - 1)) / count
            val compact = cardHeight < 66.dp
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                CheckInCategory.entries.forEach { category ->
                    CategoryCard(
                        category = category,
                        info = levels?.get(category.id),
                        compact = compact,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) { onOpenCategory(category) }
                }
            }
        }
    }
}

@Composable
private fun LevelDot(level: CategoryLevel) {
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
    val tinted = info?.level == CategoryLevel.Low || info?.level == CategoryLevel.Medium
    val container = when (info?.level) {
        CategoryLevel.Low -> ClearLevelLow
        CategoryLevel.Medium -> ClearLevelMedium
        else -> ClearCardColor
    }
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = container, contentColor = ClearTextDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (tinted) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 4.dp),
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
