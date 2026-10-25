package com.example.clear.ui.checkin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.Phq9Result
import com.example.clear.model.Phq9
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

internal fun monthLabel(yearMonth: Int): String =
    YearMonth.of(yearMonth / 100, yearMonth % 100)
        .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMANY))

internal fun changeLabel(previous: Int, current: Int): String = when {
    current > previous -> "↑ gestiegen"
    current < previous -> "↓ gesunken"
    else -> "→ gleich geblieben"
}

// ---------- Uebersicht ----------

@Composable
fun Phq9HubScreen(
    onBack: () -> Unit,
    onStart: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: Phq9HubViewModel = viewModel()
) {
    val results by viewModel.results.collectAsState()
    val available by viewModel.available.collectAsState()
    val list = results
    val latest = list?.firstOrNull()

    ClearDetailScreen(title = "PHQ-9", onBack = onBack) {
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text(
                if (available || latest == null) "Der PHQ-9 ist bereit. Er hat neun Fragen."
                else "Du hast den PHQ-9 vor Kurzem ausgefüllt. Der nächste ist " +
                    "frühestens am ${nextAvailableLabel(latest.createdAt)} möglich (mindestens zwei Wochen Abstand).",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Button(
            onClick = onStart,
            enabled = available,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text("Fragebogen starten") }

        if (latest != null) {
            ClearCard {
                Text(
                    "Letztes Ergebnis · ${dateLabel(latest.createdAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text("${latest.total} von ${Phq9.MAX_TOTAL}", style = MaterialTheme.typography.titleLarge)
                Text(Phq9.band(latest.total), style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (!list.isNullOrEmpty()) {
            OutlinedButton(
                onClick = onOpenHistory,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) { Text("Verlauf ansehen") }
        }
    }
}

// ---------- Fragebogen ----------

@Composable
fun Phq9TestScreen(
    onClose: () -> Unit,
    onFinished: () -> Unit,
    viewModel: Phq9TestViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()
    val summary = s.summary

    BackHandler {
        when {
            summary != null -> onFinished()
            s.isFirst -> onClose()
            else -> viewModel.previous()
        }
    }

    if (summary != null) {
        Phq9ResultContent(summary, onDone = onFinished)
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = ClearSpacing.large)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Fragebogen beenden")
            }
            Text(
                "Frage ${s.index + 1} von ${Phq9.COUNT}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { s.progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = ClearSurfaceSoft
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = ClearSpacing.large),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Text(
                Phq9.INTRO,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(Phq9.questions[s.index], style = MaterialTheme.typography.titleLarge)
            Phq9.options.forEachIndexed { value, label ->
                val selected = s.selected == value
                ClearCard(
                    onClick = { viewModel.select(value) },
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else null
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(selected = selected, onClick = null)
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = ClearSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (s.isFirst) {
                Spacer(Modifier.size(1.dp))
            } else {
                TextButton(
                    onClick = viewModel::previous,
                    modifier = Modifier.heightIn(min = 56.dp)
                ) { Text("Zurück") }
            }
            Button(
                onClick = { if (s.isLast) viewModel.finish() else viewModel.next() },
                enabled = s.selected != null,
                modifier = Modifier.heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) { Text(if (s.isLast) "Fertig" else "Weiter") }
        }
    }
}

@Composable
private fun Phq9ResultContent(summary: Phq9Summary, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
    ) {
        Text("Dein Ergebnis", style = MaterialTheme.typography.headlineMedium)
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text("${summary.total} von ${Phq9.MAX_TOTAL}", style = MaterialTheme.typography.headlineMedium)
            Text(summary.band, style = MaterialTheme.typography.titleMedium)
        }

        val changes = summary.changes
        if (changes == null) {
            Text(
                "Das war dein erster Fragebogen. Ab dem nächsten Monat siehst du hier, was sich verändert hat.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text("Seit dem letzten Mal", style = MaterialTheme.typography.titleMedium)
            ClearCard {
                changes.forEach { change ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            Phq9.shortTitles[change.question],
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            changeLabel(change.previous, change.current),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text("Fertig") }
    }
}

// ---------- Verlauf ----------

@Composable
fun Phq9HistoryScreen(onBack: () -> Unit, viewModel: Phq9HistoryViewModel = viewModel()) {
    val results by viewModel.results.collectAsState()
    val list = results

    ClearDetailScreen(title = "Verlauf Fragebogen", onBack = onBack) {
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> Text(
                "Hier ist noch nichts gespeichert.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> {
                ClearCard {
                    Text("Gesamtpunktzahl", style = MaterialTheme.typography.titleMedium)
                    PhqChart(
                        values = list.map { it.total },
                        maxValue = Phq9.MAX_TOTAL,
                        modifier = Modifier.fillMaxWidth().height(72.dp)
                    )
                    list.reversed().forEach { result ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                        ) {
                            Text(
                                dateLabel(result.createdAt),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${result.total} · ${Phq9.band(result.total).substringBefore(' ')}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text("Einzelne Fragen", style = MaterialTheme.typography.titleMedium)
                Phq9.shortTitles.forEachIndexed { index, title ->
                    QuestionHistoryCard(title, list, index)
                }
            }
        }
    }
}

@Composable
private fun QuestionHistoryCard(title: String, results: List<Phq9Result>, index: Int) {
    val values = results.map { it.answers()[index] }
    val latest = values.last()
    val trend = if (values.size >= 2) changeLabel(values[values.size - 2], latest) else null
    ClearCard {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    Phq9.options[latest],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (trend != null) {
                    Text(trend, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
            PhqChart(
                values = values,
                maxValue = 3,
                modifier = Modifier.width(100.dp).height(56.dp)
            )
        }
    }
}

/** Kleiner Linienverlauf. Die Werte stehen gleichmaessig verteilt (ein Wert pro Monat). */
@Composable
internal fun PhqChart(values: List<Int>, maxValue: Int, modifier: Modifier) {
    val lineColor: Color = MaterialTheme.colorScheme.primary
    val guideColor: Color = ClearSurfaceSoft
    Canvas(modifier = modifier) {
        val pad = 6.dp.toPx()
        val w = size.width - pad * 2
        val h = size.height - pad * 2
        fun x(i: Int): Float =
            if (values.size == 1) size.width / 2 else pad + w * i / (values.size - 1).toFloat()
        fun y(v: Int): Float = pad + (1f - v / maxValue.toFloat()) * h

        drawLine(
            color = guideColor,
            start = Offset(pad, size.height - pad),
            end = Offset(size.width - pad, size.height - pad),
            strokeWidth = 1.dp.toPx()
        )
        if (values.size >= 2) {
            val path = Path()
            values.forEachIndexed { i, v ->
                if (i == 0) path.moveTo(x(i), y(v)) else path.lineTo(x(i), y(v))
            }
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        values.forEachIndexed { i, v ->
            drawCircle(color = lineColor, radius = 3.dp.toPx(), center = Offset(x(i), y(v)))
        }
    }
}
