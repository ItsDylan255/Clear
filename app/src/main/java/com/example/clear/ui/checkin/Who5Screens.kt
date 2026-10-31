package com.example.clear.ui.checkin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.Who5Result
import com.example.clear.model.Who5
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

// ---------- Uebersicht ----------

@Composable
fun Who5HubScreen(
    onBack: () -> Unit,
    onStart: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: Who5HubViewModel = viewModel()
) {
    val results by viewModel.results.collectAsState()
    val available by viewModel.available.collectAsState()
    val list = results
    val latest = list?.firstOrNull()

    ClearDetailScreen(title = "WHO-5", onBack = onBack) {
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text(
                if (available || latest == null) tr(
                    "Der WHO-5 ist bereit. Er hat fünf Fragen zu deinem Wohlbefinden.",
                    "The WHO-5 is ready. It has five questions about your well-being."
                )
                else tr(
                    "Du hast den WHO-5 vor Kurzem ausgefüllt. Der nächste ist ",
                    "You filled in the WHO-5 recently. The next one is "
                ) +
                    tr(
                        "frühestens am ${nextAvailableLabel(latest.createdAt)} möglich (mindestens zwei Wochen Abstand).",
                        "available on ${nextAvailableLabel(latest.createdAt)} at the earliest (at least two weeks apart)."
                    ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Button(
            onClick = onStart,
            enabled = available,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text(tr("WHO-5 starten", "Start WHO-5")) }

        if (latest != null) {
            ClearCard {
                Text(
                    tr(
                        "Letztes Ergebnis · ${dateLabel(latest.createdAt)}",
                        "Last result · ${dateLabel(latest.createdAt)}"
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(tr(
                    "${latest.percent} von ${Who5.MAX_PERCENT}",
                    "${latest.percent} of ${Who5.MAX_PERCENT}"
                ), style = MaterialTheme.typography.titleLarge)
                Text(Who5.band(latest.percent), style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (!list.isNullOrEmpty()) {
            OutlinedButton(
                onClick = onOpenHistory,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) { Text(tr("Verlauf ansehen", "View history")) }
        }
    }
}

// ---------- Fragebogen ----------

@Composable
fun Who5TestScreen(
    onClose: () -> Unit,
    onFinished: () -> Unit,
    viewModel: Who5TestViewModel = viewModel()
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
        Who5ResultContent(summary, onDone = onFinished)
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = ClearSpacing.large)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = tr("WHO-5 beenden", "Close WHO-5"))
            }
            Text(
                tr("Frage ${s.index + 1} von ${Who5.COUNT}", "Question ${s.index + 1} of ${Who5.COUNT}"),
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
                Who5.INTRO,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(Who5.questions[s.index], style = MaterialTheme.typography.titleLarge)
            // Von "Die ganze Zeit" (5) bis "Zu keinem Zeitpunkt" (0)
            Who5.options.indices.reversed().forEach { value ->
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
                        Text(Who5.options[value], style = MaterialTheme.typography.bodyLarge)
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
                ) { Text(tr("Zurück", "Back")) }
            }
            Button(
                onClick = { if (s.isLast) viewModel.finish() else viewModel.next() },
                enabled = s.selected != null,
                modifier = Modifier.heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) { Text(if (s.isLast) tr("Fertig", "Done") else tr("Weiter", "Next")) }
        }
    }
}

@Composable
private fun Who5ResultContent(summary: Who5Summary, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
    ) {
        Text(tr("Dein Ergebnis", "Your result"), style = MaterialTheme.typography.headlineMedium)
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text(tr(
                "${summary.percent} von ${Who5.MAX_PERCENT}",
                "${summary.percent} of ${Who5.MAX_PERCENT}"
            ), style = MaterialTheme.typography.headlineMedium)
            Text(summary.band, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            tr(
                "Das ist eine Momentaufnahme und keine Diagnose. Bei Fragen oder wenn es dir schlecht geht, " +
                    "sprich gern mit einer vertrauten Person oder einer Fachperson.",
                "This is a snapshot, not a diagnosis. If you have questions or are not doing well, " +
                    "feel free to talk to someone you trust or a professional."
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val changes = summary.changes
        if (changes == null) {
            Text(
                tr(
                    "Das war dein erster WHO-5. Ab dem nächsten Monat siehst du hier, was sich verändert hat.",
                    "This was your first WHO-5. Next time you'll see here what has changed."
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(tr("Seit dem letzten Mal", "Since last time"), style = MaterialTheme.typography.titleMedium)
            ClearCard {
                changes.forEach { change ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            Who5.shortTitles[change.question],
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
        ) { Text(tr("Fertig", "Done")) }
    }
}

// ---------- Verlauf ----------

@Composable
fun Who5HistoryScreen(onBack: () -> Unit, viewModel: Who5HistoryViewModel = viewModel()) {
    val results by viewModel.results.collectAsState()
    val list = results

    ClearDetailScreen(title = tr("Verlauf WHO-5", "WHO-5 history"), onBack = onBack) {
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> Text(
                tr("Hier ist noch nichts gespeichert.", "Nothing saved here yet."),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> {
                ClearCard {
                    Text(tr(
                        "Wohlbefinden (0 bis 100)",
                        "Well-being (0 to 100)"
                    ), style = MaterialTheme.typography.titleMedium)
                    PhqChart(
                        values = list.map { it.percent },
                        maxValue = Who5.MAX_PERCENT,
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
                                "${result.percent}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(tr("Einzelne Fragen", "Individual questions"), style = MaterialTheme.typography.titleMedium)
                Who5.shortTitles.forEachIndexed { index, title ->
                    Who5QuestionHistoryCard(title, list, index)
                }
            }
        }
    }
}

@Composable
private fun Who5QuestionHistoryCard(title: String, results: List<Who5Result>, index: Int) {
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
                    Who5.options[latest],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (trend != null) {
                    Text(trend, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
            PhqChart(
                values = values,
                maxValue = 5,
                modifier = Modifier.width(100.dp).height(56.dp)
            )
        }
    }
}
