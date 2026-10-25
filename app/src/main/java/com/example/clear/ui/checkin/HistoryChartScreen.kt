package com.example.clear.ui.checkin

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearLevelHigh
import com.example.clear.ui.theme.ClearLevelLow
import com.example.clear.ui.theme.ClearLevelMedium
import com.example.clear.ui.theme.ClearSpacing
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale

private val rangeOptions = listOf(7 to "7 Tage", 30 to "30 Tage", 90 to "90 Tage", 365 to "Jahr")
private const val DAY_MS = 86_400_000L

/**
 * Eigene Seite fuer einen Bereich oder Fragebogen: ein Diagramm ueber mindestens ein Jahr.
 * Die Auswahl (7 Tage, 30 Tage, 90 Tage, Jahr) bestimmt, wie viel du auf einmal siehst.
 * In jeder Ansicht kannst du nach links wischen, um aeltere Werte zu sehen.
 */
@Composable
fun HistoryChartScreen(onBack: () -> Unit, viewModel: HistoryChartViewModel = viewModel()) {
    val data by viewModel.data.collectAsState()
    var rangeDays by rememberSaveable { mutableIntStateOf(30) }
    val d = data

    ClearDetailScreen(title = d?.title ?: "Verlauf", onBack = onBack) {
        if (d != null) {
            if (d.allowRange) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rangeOptions.forEach { (days, label) ->
                        FilterChip(
                            selected = rangeDays == days,
                            onClick = { rangeDays = days },
                            label = { Text(label, maxLines = 1) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (d.points.isEmpty()) {
                ClearCard {
                    Text(
                        "Hier ist noch nichts gespeichert. Sobald du Werte einträgst, erscheint hier ihr Verlauf.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                ClearCard {
                    HistoryChart(d, if (d.allowRange) rangeDays else 365)
                }

                Text("Werte", style = MaterialTheme.typography.titleMedium)
                ClearCard {
                    d.points.asReversed().take(30).forEach { point ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                dateLabel(point.timeMillis),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(point.note, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${point.value.toInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.width(36.dp)
                            )
                        }
                    }
                    if (d.points.size > 30) {
                        Text(
                            "Es werden die letzten 30 Werte aufgelistet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryChart(data: ChartData, rangeDays: Int) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val scroll = rememberScrollState()

    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val dotInner = MaterialTheme.colorScheme.surface
    val bandLow = ClearLevelLow.copy(alpha = 0.8f)
    val bandMedium = ClearLevelMedium.copy(alpha = 0.8f)
    val bandHigh = ClearLevelHigh.copy(alpha = 0.8f)
    val labelStyle = TextStyle(fontSize = 11.sp, color = labelColor)

    // Zeitachse: vom ersten bis zum letzten Eintrag, mindestens so lang wie die gewaehlte Ansicht.
    // Weiter nach links oder rechts kann man nicht wischen.
    val zone = remember { ZoneId.systemDefault() }
    val windowMs = rangeDays * DAY_MS
    val marginMs = (windowMs * 0.04).toLong().coerceAtLeast(DAY_MS / 4)
    val first = data.points.first().timeMillis
    val last = data.points.last().timeMillis
    val endMs = last + marginMs
    val totalMs = (endMs - (first - marginMs)).coerceIn(windowMs, 1095 * DAY_MS)
    val startMs = endMs - totalMs

    val chartHeight = 230.dp
    val axisWidth = 36.dp
    val topPad = 12.dp
    val bottomPad = 30.dp

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val viewport = maxWidth - axisWidth
        val viewportPx = with(density) { viewport.toPx() }
        val canvasWidth = viewport * (totalMs.toFloat() / windowMs)

        // Beim Wechsel der Ansicht startet das Diagramm ganz rechts (heute)
        LaunchedEffect(rangeDays, totalMs, viewportPx) {
            withFrameNanos { }
            scroll.scrollTo(scroll.maxValue)
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            // Feste Beschriftung der Hoehe
            Canvas(modifier = Modifier.width(axisWidth).height(chartHeight)) {
                val top = topPad.toPx()
                val bottom = size.height - bottomPad.toPx()
                data.ticks.forEach { tick ->
                    val y = bottom - (tick - data.yMin) / (data.yMax - data.yMin) * (bottom - top)
                    val layout = textMeasurer.measure("${tick.toInt()}", labelStyle)
                    drawText(
                        layout,
                        topLeft = Offset(size.width - layout.size.width - 6.dp.toPx(), y - layout.size.height / 2f)
                    )
                }
            }

            // Scrollbarer Teil
            Box(modifier = Modifier.weight(1f).horizontalScroll(scroll)) {
                Canvas(modifier = Modifier.width(canvasWidth).height(chartHeight)) {
                    val top = topPad.toPx()
                    val bottom = size.height - bottomPad.toPx()
                    val spanMs = (endMs - startMs).toFloat()
                    fun yOf(v: Float) = bottom - (v - data.yMin) / (data.yMax - data.yMin) * (bottom - top)
                    fun xOf(ms: Long) = (ms - startMs) / spanMs * size.width

                    // Level-Hintergrund (nur Bereiche)
                    if (data.levelBands) {
                        drawRect(bandLow, Offset(0f, yOf(4f)), Size(size.width, bottom - yOf(4f)))
                        drawRect(bandMedium, Offset(0f, yOf(7f)), Size(size.width, yOf(4f) - yOf(7f)))
                        drawRect(bandHigh, Offset(0f, top), Size(size.width, yOf(7f) - top))
                    }
                    // Hilfslinien
                    data.ticks.forEach { tick ->
                        drawLine(gridColor, Offset(0f, yOf(tick)), Offset(size.width, yOf(tick)), 1.dp.toPx())
                    }

                    // Beschriftung der Zeit, nur fuer den sichtbaren Ausschnitt
                    val scrollPx = scroll.value.toFloat()
                    val firstMs = startMs + (scrollPx / size.width * spanMs).toLong() - DAY_MS
                    val lastMs = startMs + ((scrollPx + viewportPx) / size.width * spanMs).toLong() + DAY_MS
                    var date = Instant.ofEpochMilli(firstMs).atZone(zone).toLocalDate()
                    val lastDate = Instant.ofEpochMilli(lastMs).atZone(zone).toLocalDate()
                    while (!date.isAfter(lastDate)) {
                        val label = timeLabel(date, rangeDays)
                        if (label != null) {
                            val dayStart = date.atStartOfDay(zone).toInstant().toEpochMilli()
                            val x = if (rangeDays == 7) xOf(dayStart + DAY_MS / 2) else xOf(dayStart)
                            drawLine(
                                gridColor.copy(alpha = 0.6f),
                                Offset(x, top), Offset(x, bottom), 1.dp.toPx()
                            )
                            val layout = textMeasurer.measure(label, labelStyle)
                            drawText(layout, topLeft = Offset(x - layout.size.width / 2f, bottom + 8.dp.toPx()))
                        }
                        date = date.plusDays(1)
                    }

                    // Linie und Punkte
                    val points = data.points
                    if (points.size >= 2) {
                        val path = Path()
                        points.forEachIndexed { index, p ->
                            val x = xOf(p.timeMillis)
                            val y = yOf(p.value)
                            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path, lineColor,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                    points.forEach { p ->
                        val center = Offset(xOf(p.timeMillis), yOf(p.value))
                        drawCircle(lineColor, 4.5.dp.toPx(), center)
                        drawCircle(dotInner, 2.dp.toPx(), center)
                    }
                }
            }
        }
    }
}

/** Beschriftung eines Tages auf der Zeitachse, oder null, wenn dort keine steht. */
private fun timeLabel(date: LocalDate, rangeDays: Int): String? = when (rangeDays) {
    7 -> {
        val weekday: DayOfWeek = date.dayOfWeek
        "${weekday.getDisplayName(DateTextStyle.SHORT, Locale.GERMANY).take(2)} ${date.dayOfMonth}."
    }
    30 -> if (date.toEpochDay() % 5L == 0L) "${date.dayOfMonth}.${date.monthValue}." else null
    90 -> if (date.toEpochDay() % 14L == 0L) "${date.dayOfMonth}.${date.monthValue}." else null
    else -> if (date.dayOfMonth == 1 && date.monthValue % 2 == 1) {
        val month = date.month.getDisplayName(DateTextStyle.SHORT, Locale.GERMANY).removeSuffix(".")
        if (date.monthValue == 1) "$month ${date.year % 100}" else month
    } else null
}

/** Uebersicht: alle Bereiche und Frageboegen. Jeder Eintrag oeffnet sein eigenes Diagramm. */
@Composable
fun HistoryOverviewScreen(onBack: () -> Unit, onOpen: (kind: String) -> Unit) {
    ClearDetailScreen(title = "Verlauf", onBack = onBack) {
        Text(
            "Wähle einen Bereich oder einen Fragebogen, um den Verlauf als Diagramm zu sehen.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text("Bereiche", style = MaterialTheme.typography.titleMedium)
        CheckInCategory.entries.forEach { category ->
            OverviewRow(category.title) { onOpen(category.id) }
        }
        Text("Fragebögen", style = MaterialTheme.typography.titleMedium)
        OverviewRow("PHQ-9") { onOpen("phq9") }
        OverviewRow("WHO-5") { onOpen("who5") }
        OverviewRow("WSAS") { onOpen("wsas") }
    }
}

@Composable
private fun OverviewRow(title: String, onClick: () -> Unit) {
    ClearCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
