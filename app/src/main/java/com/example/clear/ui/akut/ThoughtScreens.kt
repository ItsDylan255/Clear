package com.example.clear.ui.akut

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.ThoughtRecord
import com.example.clear.model.HelpQuestions
import com.example.clear.model.ThinkingTrap
import com.example.clear.model.ThoughtEmotion
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private fun formatDate(timestamp: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.GERMANY).format(Date(timestamp))

private fun emotionTitle(id: String): String = ThoughtEmotion.fromId(id)?.title ?: "Gefühl"

// ---------- Liste ----------

@Composable
fun ThoughtListScreen(
    onBack: () -> Unit,
    onNew: () -> Unit,
    onOpen: (Long) -> Unit,
    viewModel: ThoughtListViewModel = viewModel()
) {
    val records by viewModel.records.collectAsState()
    var toDelete by remember { mutableStateOf<ThoughtRecord?>(null) }
    val list = records

    ClearDetailScreen(title = "Gedankenprotokoll", onBack = onBack) {
        AddButton("Neues Protokoll", onNew)
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> Text(
                "Hier ist noch nichts gespeichert. Du kannst jederzeit ein Protokoll anlegen.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> list.forEach { record ->
                ClearCard(onClick = { onOpen(record.id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(top = ClearSpacing.small)) {
                            Text(
                                formatDate(record.createdAt),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                record.thought,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                "${emotionTitle(record.emotion)}: von ${record.intensityBefore} auf ${record.intensityAfter}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = ClearSpacing.small)
                            )
                        }
                        OptionsMenu(
                            contentDescription = "Optionen für dieses Protokoll",
                            onEdit = { onOpen(record.id) },
                            onDelete = { toDelete = record }
                        )
                    }
                }
            }
        }
    }

    toDelete?.let { record ->
        ConfirmDeleteDialog(
            title = "Protokoll löschen?",
            message = "Dieses Protokoll wird dauerhaft gelöscht.",
            onConfirm = { viewModel.delete(record); toDelete = null },
            onDismiss = { toDelete = null }
        )
    }
}

// ---------- Ansicht eines Protokolls ----------

@Composable
fun ThoughtDetailScreen(onBack: () -> Unit, viewModel: ThoughtDetailViewModel = viewModel()) {
    val record by viewModel.record.collectAsState()
    var confirmDelete by remember { mutableStateOf(false) }
    val r = record

    ClearDetailScreen(title = "Gedankenprotokoll", onBack = onBack) {
        if (r == null) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else {
            Text(
                formatDate(r.createdAt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            DetailBlock("Was ist passiert?", r.situation)
            DetailBlock("Was ging mir durch den Kopf?", r.thought)
            DetailBlock(
                "Wie habe ich mich gefühlt?",
                "${emotionTitle(r.emotion)}, Stärke ${r.intensityBefore} von 10"
            )
            val traps = ThinkingTrap.parse(r.traps)
            if (traps.isNotEmpty()) {
                DetailBlock(
                    "Welche Denkfalle steckte drin?",
                    ThinkingTrap.entries.filter { it in traps }.joinToString(", ") { it.title }
                )
            }
            DetailBlock(
                HelpQuestions.all.getOrElse(r.helpQuestion) { "Neue Sichtweise" },
                r.newView
            )
            ClearCard(containerColor = ClearSurfaceSoft) {
                Text(
                    "Von ${r.intensityBefore} auf ${r.intensityAfter}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            TextButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.heightIn(min = 48.dp)
            ) { Text("Löschen") }
        }
    }

    if (confirmDelete && r != null) {
        ConfirmDeleteDialog(
            title = "Protokoll löschen?",
            message = "Dieses Protokoll wird dauerhaft gelöscht.",
            onConfirm = { confirmDelete = false; viewModel.delete(r, onDone = onBack) },
            onDismiss = { confirmDelete = false }
        )
    }
}

@Composable
private fun DetailBlock(title: String, text: String) {
    ClearCard {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

// ---------- Neues Protokoll ----------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThoughtFlowScreen(
    onClose: () -> Unit,
    onFinished: () -> Unit,
    viewModel: ThoughtFlowViewModel = viewModel()
) {
    val s by viewModel.state.collectAsState()
    val recent by viewModel.recentEntries.collectAsState()
    val result = s.result

    BackHandler {
        when {
            result != null -> onFinished()
            s.isFirst -> onClose()
            else -> viewModel.previous()
        }
    }

    if (result != null) {
        ThoughtResult(emotionTitle(s.emotion?.id.orEmpty()), result, onDone = onFinished)
        return
    }

    Box(modifier = Modifier.imePadding()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = ClearSpacing.large)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Protokoll beenden")
                }
                Text(
                    "Schritt ${s.step + 1} von $THOUGHT_STEPS",
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
                when (s.step) {
                    0 -> {
                        Text("Was ist passiert?", style = MaterialTheme.typography.headlineSmall)
                        OutlinedTextField(
                            value = s.situation,
                            onValueChange = viewModel::onSituation,
                            label = { Text("Ein Satz genügt") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    1 -> {
                        Text("Was ging mir durch den Kopf?", style = MaterialTheme.typography.headlineSmall)
                        OutlinedTextField(
                            value = s.thought,
                            onValueChange = viewModel::onThought,
                            label = { Text("Der Gedanke als Satz") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        Text("Wie fühle ich mich?", style = MaterialTheme.typography.headlineSmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                            ThoughtEmotion.entries.forEach { emotion ->
                                FilterChip(
                                    selected = s.emotion == emotion,
                                    onClick = { viewModel.onEmotion(emotion) },
                                    label = { Text(emotion.title) }
                                )
                            }
                        }
                        IntensitySlider("Stärke", s.before, viewModel::onBefore)
                    }
                    3 -> {
                        Text("Welche Denkfalle steckt drin?", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Tippe an, was passt. Mehrere sind möglich, und du kannst auch nichts wählen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ThinkingTrap.entries.forEach { trap ->
                            val selected = trap in s.traps
                            ClearCard(
                                onClick = { viewModel.toggleTrap(trap) },
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else null
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Checkbox(checked = selected, onCheckedChange = null)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(trap.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            trap.explanation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        Text("Neue Sichtweise", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "Such dir eine Frage aus und beantworte sie.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HelpQuestions.all.forEachIndexed { index, question ->
                            val selected = s.helpQuestion == index
                            ClearCard(
                                onClick = { viewModel.onHelpQuestion(index) },
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else null
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    RadioButton(selected = selected, onClick = null)
                                    Text(question, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                        if (s.helpQuestion == HelpQuestions.AGAINST) {
                            ClearCard {
                                Text(
                                    "Das hast du zuletzt aufgeschrieben",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (recent.isEmpty()) {
                                    Text(
                                        "Hier ist noch nichts gespeichert.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    recent.forEach { entry ->
                                        Text("• ${entry.text}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                        if (s.helpQuestion != null) {
                            OutlinedTextField(
                                value = s.newView,
                                onValueChange = viewModel::onNewView,
                                label = { Text("Deine Antwort") },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    else -> {
                        Text("Wie fühlst du dich jetzt?", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            "${emotionTitle(s.emotion?.id.orEmpty())} vorher: ${s.before} von 10",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IntensitySlider("Stärke", s.afterValue, viewModel::onAfter)
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
                    enabled = s.canContinue,
                    modifier = Modifier.heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text(if (s.isLast) "Fertig" else "Weiter") }
            }
        }
    }
}

/** Regler von 0 bis 10. */
@Composable
private fun IntensitySlider(label: String, value: Int, onChange: (Int) -> Unit) {
    Column {
        Text("$label: $value von 10", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.roundToInt()) },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("10", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ThoughtResult(emotion: String, result: Pair<Int, Int>, onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
    ) {
        Text("Gespeichert", style = MaterialTheme.typography.headlineMedium)
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text(emotion, style = MaterialTheme.typography.titleMedium)
            Text("Von ${result.first} auf ${result.second}", style = MaterialTheme.typography.headlineMedium)
        }
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text("Fertig") }
    }
}
