package com.example.clear.ui.checkin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.WsasResult
import com.example.clear.model.Wsas
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

// ---------- Uebersicht ----------

@Composable
fun WsasHubScreen(
    onBack: () -> Unit,
    onStart: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: WsasHubViewModel = viewModel()
) {
    val results by viewModel.results.collectAsState()
    val available by viewModel.available.collectAsState()
    val list = results
    val latest = list?.firstOrNull()

    ClearDetailScreen(title = "WSAS", onBack = onBack) {
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text(
                if (available || latest == null)
                    tr(
                        "Die WSAS ist bereit. Sie hat fünf Fragen dazu, wie stark deine Probleme dich im Alltag beeinträchtigen.",
                        "The WSAS is ready. It has five questions about how much your problems affect your daily life."
                    )
                else tr("Du hast die WSAS vor Kurzem ausgefüllt. ", "You filled in the WSAS recently. ") +
                    tr(
                        "Die nächste ist frühestens am ${nextAvailableLabel(latest.createdAt)} möglich " +
                            "(mindestens zwei Wochen Abstand).",
                        "The next one is available on ${nextAvailableLabel(latest.createdAt)} at the earliest " +
                            "(at least two weeks apart)."
                    ),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Button(
            onClick = onStart,
            enabled = available,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text(tr("WSAS starten", "Start WSAS")) }

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
                    "${latest.total} von ${Wsas.MAX_TOTAL}",
                    "${latest.total} of ${Wsas.MAX_TOTAL}"
                ), style = MaterialTheme.typography.titleLarge)
                Text(Wsas.band(latest.total), style = MaterialTheme.typography.bodyLarge)
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
fun WsasTestScreen(
    onClose: () -> Unit,
    onFinished: () -> Unit,
    viewModel: WsasTestViewModel = viewModel()
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
        WsasResultContent(summary, onDone = onFinished)
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = ClearSpacing.large)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = tr("WSAS beenden", "Close WSAS"))
            }
            Text(
                tr("Frage ${s.index + 1} von ${Wsas.COUNT}", "Question ${s.index + 1} of ${Wsas.COUNT}"),
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
                Wsas.INTRO,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(Wsas.questions[s.index], style = MaterialTheme.typography.titleLarge)
            NumberScale(selected = s.selected, onSelect = viewModel::select)
            Text(
                Wsas.SCALE_HINT,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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

/** Zahlen 0 bis 8 zum Antippen, in zwei Reihen. */
@Composable
private fun NumberScale(selected: Int?, onSelect: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        (0..Wsas.MAX_ANSWER).chunked(5).forEach { rowValues ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowValues.forEach { value -> NumberButton(value, value == selected, onSelect) }
            }
        }
    }
}

@Composable
private fun NumberButton(value: Int, isSelected: Boolean, onSelect: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else ClearSurfaceSoft)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(value) })
            .semantics { contentDescription = tr("$value von 8", "$value of 8") },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$value",
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else ClearTextDark,
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun WsasResultContent(summary: WsasSummary, onDone: () -> Unit) {
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
                "${summary.total} von ${Wsas.MAX_TOTAL}",
                "${summary.total} of ${Wsas.MAX_TOTAL}"
            ), style = MaterialTheme.typography.headlineMedium)
            Text(summary.band, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            tr(
                "Ein niedrigerer Wert bedeutet weniger Beeinträchtigung. Das ist eine Momentaufnahme und " +
                    "keine Diagnose. Bei Fragen oder wenn es dir schlecht geht, sprich gern mit einer " +
                    "vertrauten Person oder einer Fachperson.",
                "A lower score means less impairment. This is a snapshot and " +
                    "not a diagnosis. If you have questions or are not doing well, feel free to talk to " +
                    "someone you trust or a professional."
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val changes = summary.changes
        if (changes == null) {
            Text(
                tr(
                    "Das war deine erste WSAS. Beim nächsten Mal siehst du hier, was sich verändert hat.",
                    "This was your first WSAS. Next time you'll see here what has changed."
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
                            Wsas.shortTitles[change.question],
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
fun WsasHistoryScreen(onBack: () -> Unit, viewModel: WsasHistoryViewModel = viewModel()) {
    val results by viewModel.results.collectAsState()
    val list = results

    ClearDetailScreen(title = tr("Verlauf WSAS", "WSAS history"), onBack = onBack) {
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
                        "Beeinträchtigung (0 bis 40)",
                        "Impairment (0 to 40)"
                    ), style = MaterialTheme.typography.titleMedium)
                    PhqChart(
                        values = list.map { it.total },
                        maxValue = Wsas.MAX_TOTAL,
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
                                "${result.total}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Text(tr("Einzelne Bereiche", "Individual areas"), style = MaterialTheme.typography.titleMedium)
                Wsas.shortTitles.forEachIndexed { index, title ->
                    WsasQuestionHistoryCard(title, list, index)
                }
            }
        }
    }
}

@Composable
private fun WsasQuestionHistoryCard(title: String, results: List<WsasResult>, index: Int) {
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
                    tr("Zuletzt: $latest von ${Wsas.MAX_ANSWER}", "Last: $latest of ${Wsas.MAX_ANSWER}"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (trend != null) {
                    Text(trend, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }
            PhqChart(
                values = values,
                maxValue = Wsas.MAX_ANSWER,
                modifier = Modifier.width(100.dp).height(56.dp)
            )
        }
    }
}
