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
import com.example.clear.AppLanguage
import com.example.clear.data.ThoughtRecord
import com.example.clear.model.HelpQuestions
import com.example.clear.model.ThinkingTrap
import com.example.clear.model.ThoughtEmotion
import com.example.clear.tr
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ConfirmDeleteDialog
import com.example.clear.ui.components.OptionsMenu
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import java.text.DateFormat
import java.util.Date
import kotlin.math.roundToInt

private fun formatDate(timestamp: Long): String =
    DateFormat.getDateInstance(DateFormat.MEDIUM, AppLanguage.locale).format(Date(timestamp))

private fun emotionTitle(id: String): String = ThoughtEmotion.fromId(id)?.title ?: tr("Gefühl", "Feeling")

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

    ClearDetailScreen(title = tr("Gedankenprotokoll", "Thought record"), onBack = onBack) {
        AddButton(tr("Neues Protokoll", "New record"), onNew)
        when {
            list == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            list.isEmpty() -> Text(
                tr(
                    "Hier ist noch nichts gespeichert. Du kannst jederzeit ein Protokoll anlegen.",
                    "Nothing saved here yet. You can create a record at any time."
                ),
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
                                tr(
                                    "${emotionTitle(record.emotion)}: von ${record.intensityBefore} auf ${record.intensityAfter}",
                                    "${emotionTitle(record.emotion)}: from ${record.intensityBefore} to ${record.intensityAfter}"
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = ClearSpacing.small)
                            )
                        }
                        OptionsMenu(
                            contentDescription = tr("Optionen für dieses Protokoll", "Options for this record"),
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
            title = tr("Protokoll löschen?", "Delete record?"),
            message = tr("Dieses Protokoll wird dauerhaft gelöscht.", "This record will be deleted permanently."),
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

    ClearDetailScreen(title = tr("Gedankenprotokoll", "Thought record"), onBack = onBack) {
        if (r == null) {
            CircularProgressIndicator(modifier = Modifier.size(32.dp))
        } else {
            Text(
                formatDate(r.createdAt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            DetailBlock(tr("Was ist passiert?", "What happened?"), r.situation)
            DetailBlock(tr("Was ging mir durch den Kopf?", "What went through my mind?"), r.thought)
            DetailBlock(
                tr("Wie habe ich mich gefühlt?", "How did I feel?"),
                tr(
                    "${emotionTitle(r.emotion)}, Stärke ${r.intensityBefore} von 10",
                    "${emotionTitle(r.emotion)}, intensity ${r.intensityBefore} of 10"
                )
            )
            val traps = ThinkingTrap.parse(r.traps)
            if (traps.isNotEmpty()) {
                DetailBlock(
                    tr("Welche Denkfalle steckte drin?", "Which thinking trap was in it?"),
                    ThinkingTrap.entries.filter { it in traps }.joinToString(", ") { it.title }
                )
            }
            DetailBlock(
                HelpQuestions.all.getOrElse(r.helpQuestion) { tr("Neue Sichtweise", "New perspective") },
                r.newView
            )
            ClearCard(containerColor = ClearSurfaceSoft) {
                Text(
                    tr(
                        "Von ${r.intensityBefore} auf ${r.intensityAfter}",
                        "From ${r.intensityBefore} to ${r.intensityAfter}"
                    ),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            TextButton(
                onClick = { confirmDelete = true },
                modifier = Modifier.heightIn(min = 48.dp)
            ) { Text(tr("Löschen", "Delete")) }
        }
    }

    if (confirmDelete && r != null) {
        ConfirmDeleteDialog(
            title = tr("Protokoll löschen?", "Delete record?"),
            message = tr("Dieses Protokoll wird dauerhaft gelöscht.", "This record will be deleted permanently."),
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
                    Icon(Icons.Filled.Close, contentDescription = tr("Protokoll beenden", "Close record"))
                }
                Text(
                    tr("Schritt ${s.step + 1} von $THOUGHT_STEPS", "Step ${s.step + 1} of $THOUGHT_STEPS"),
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
                        Text(tr("Was ist passiert?", "What happened?"), style = MaterialTheme.typography.headlineSmall)
                        OutlinedTextField(
                            value = s.situation,
                            onValueChange = viewModel::onSituation,
                            label = { Text(tr("Ein Satz genügt", "One sentence is enough")) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    1 -> {
                        Text(tr(
                            "Was ging mir durch den Kopf?",
                            "What went through my mind?"
                        ), style = MaterialTheme.typography.headlineSmall)
                        OutlinedTextField(
                            value = s.thought,
                            onValueChange = viewModel::onThought,
                            label = { Text(tr("Der Gedanke als Satz", "The thought as a sentence")) },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        Text(tr(
                            "Wie fühle ich mich?",
                            "How do I feel?"
                        ), style = MaterialTheme.typography.headlineSmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                            ThoughtEmotion.entries.forEach { emotion ->
                                FilterChip(
                                    selected = s.emotion == emotion,
                                    onClick = { viewModel.onEmotion(emotion) },
                                    label = { Text(emotion.title) }
                                )
                            }
                        }
                        IntensitySlider(tr("Stärke", "Intensity"), s.before, viewModel::onBefore)
                    }
                    3 -> {
                        Text(tr(
                            "Welche Denkfalle steckt drin?",
                            "Which thinking trap is in it?"
                        ), style = MaterialTheme.typography.headlineSmall)
                        Text(
                            tr(
                                "Tippe an, was passt. Mehrere sind möglich, und du kannst auch nichts wählen.",
                                "Tap what fits. You can choose several, or none at all."
                            ),
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
                        Text(tr("Neue Sichtweise", "New perspective"), style = MaterialTheme.typography.headlineSmall)
                        Text(
                            tr("Such dir eine Frage aus und beantworte sie.", "Pick a question and answer it."),
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
                                    tr("Das hast du zuletzt aufgeschrieben", "What you wrote down last"),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (recent.isEmpty()) {
                                    Text(
                                        tr("Hier ist noch nichts gespeichert.", "Nothing saved here yet."),
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
                                label = { Text(tr("Deine Antwort", "Your answer")) },
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    else -> {
                        Text(tr(
                            "Wie fühlst du dich jetzt?",
                            "How do you feel now?"
                        ), style = MaterialTheme.typography.headlineSmall)
                        Text(
                            tr(
                                "${emotionTitle(s.emotion?.id.orEmpty())} vorher: ${s.before} von 10",
                                "${emotionTitle(s.emotion?.id.orEmpty())} before: ${s.before} of 10"
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IntensitySlider(tr("Stärke", "Intensity"), s.afterValue, viewModel::onAfter)
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
                    enabled = s.canContinue,
                    modifier = Modifier.heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text(if (s.isLast) tr("Fertig", "Done") else tr("Weiter", "Next")) }
            }
        }
    }
}

/** Regler von 0 bis 10. */
@Composable
private fun IntensitySlider(label: String, value: Int, onChange: (Int) -> Unit) {
    Column {
        Text(tr("$label: $value von 10", "$label: $value of 10"), style = MaterialTheme.typography.titleMedium)
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
        Text(tr("Gespeichert", "Saved"), style = MaterialTheme.typography.headlineMedium)
        ClearCard(containerColor = ClearSurfaceSoft) {
            Text(emotion, style = MaterialTheme.typography.titleMedium)
            Text(tr(
                "Von ${result.first} auf ${result.second}",
                "From ${result.first} to ${result.second}"
            ), style = MaterialTheme.typography.headlineMedium)
        }
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text(tr("Fertig", "Done")) }
    }
}
