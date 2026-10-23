package com.example.clear.ui.checkin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

private val ranges = listOf(7, 30, 90)

/**
 * Verlauf der acht Bereiche. Alles auf einer Seite: die acht Zeilen teilen sich die Hoehe.
 * Auf sehr kleinen Bildschirmen scrollt die Seite als Rueckfall.
 */
@Composable
fun CheckInHistoryScreen(
    onBack: () -> Unit,
    viewModel: CheckInHistoryViewModel = viewModel()
) {
    val days by viewModel.days.collectAsState()
    val rows by viewModel.rows.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }
        }
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val titleHeight = with(LocalDensity.current) {
                MaterialTheme.typography.headlineMedium.lineHeight.toDp()
            }
            val gap = 8.dp
            // Titel + Zeitraum-Auswahl + 9 Abstaende + oberer/unterer Rand, acht Zeilen
            val rowHeight = ((maxHeight - titleHeight - 48.dp - gap * 9 - 16.dp) / 8)
                .coerceAtLeast(52.dp)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ClearSpacing.large, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                Text("Verlauf", style = MaterialTheme.typography.headlineMedium)
                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ranges.forEach { option ->
                        FilterChip(
                            selected = option == days,
                            onClick = { viewModel.selectDays(option) },
                            label = { Text("$option Tage") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                rows?.forEach { item -> TrendCard(item, rowHeight) }
            }
        }
    }
}

@Composable
private fun TrendCard(item: CategoryTrend, height: Dp) {
    val lineColor = MaterialTheme.colorScheme.primary
    val guideColor = ClearSurfaceSoft
    Card(
        modifier = Modifier.fillMaxWidth().height(height),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = ClearCardColor, contentColor = ClearTextDark)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                item.category.icon, contentDescription = null,
                tint = ClearTextDark, modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.category.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp, lineHeight = 18.sp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    trendLabel(item.trend),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 17.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Sparkline(
                points = item.points,
                lineColor = lineColor,
                guideColor = guideColor,
                modifier = Modifier.width(100.dp).fillMaxHeight()
            )
        }
    }
}

private fun trendLabel(trend: Trend): String = when (trend) {
    Trend.None -> "Noch keine Werte"
    Trend.Single -> "Ein Wert"
    Trend.Up -> "↑ gestiegen"
    Trend.Down -> "↓ gesunken"
    Trend.Flat -> "→ gleich geblieben"
}

/** Kleiner Linienverlauf. Skala fest 1 bis 10, damit die Bereiche vergleichbar bleiben. */
@Composable
private fun Sparkline(
    points: List<TrendPoint>,
    lineColor: Color,
    guideColor: Color,
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        val pad = 6.dp.toPx()
        val w = size.width - pad * 2
        val h = size.height - pad * 2
        fun x(p: TrendPoint) = pad + p.fraction * w
        fun y(p: TrendPoint) = pad + (10 - p.value) / 9f * h

        // Dezente Hilfslinie in der Mitte
        drawLine(
            color = guideColor,
            start = Offset(pad, size.height / 2),
            end = Offset(size.width - pad, size.height / 2),
            strokeWidth = 1.dp.toPx()
        )
        if (points.size >= 2) {
            val path = Path()
            points.forEachIndexed { i, p ->
                if (i == 0) path.moveTo(x(p), y(p)) else path.lineTo(x(p), y(p))
            }
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        // Bei wenigen Werten alle Punkte, sonst nur der letzte
        val dots = if (points.size <= 14) points else points.takeLast(1)
        dots.forEach { p ->
            drawCircle(color = lineColor, radius = 3.dp.toPx(), center = Offset(x(p), y(p)))
        }
    }
}
