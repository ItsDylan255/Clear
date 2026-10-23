package com.example.clear.ui.checkin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.ui.anker.EveningEntryViewModel
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

@Composable
fun CheckInFlowScreen(
    onClose: () -> Unit,
    onFinished: () -> Unit,
    viewModel: CheckInViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    // Nach der letzten Frage folgt "Erfolge und gute Dinge" (alles freiwillig)
    var eveningStep by rememberSaveable { mutableStateOf(false) }
    val eveningViewModel: EveningEntryViewModel = viewModel()

    // System-Zurueck: erst einen Schritt zurueck, bei der ersten Frage beenden
    BackHandler {
        when {
            eveningStep -> eveningStep = false
            state.isFirst -> onClose()
            else -> viewModel.previous()
        }
    }

    Column(modifier = Modifier.fillMaxSize().imePadding().padding(horizontal = ClearSpacing.large)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Check-in beenden")
            }
            Text(
                if (eveningStep) "Zum Schluss" else "Frage ${state.index + 1} von ${state.categories.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { if (eveningStep) 1f else state.progress },
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
            if (eveningStep) {
                EveningStepFields(eveningViewModel)
                return@Column
            }
            Text(
                state.current.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(state.current.question, style = MaterialTheme.typography.headlineMedium)
            Text(
                "Freiwillig. Du kannst auch einfach weitergehen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            ScaleSelector(
                selected = state.selected,
                onSelect = viewModel::select,
                lowLabel = state.current.lowLabel,
                highLabel = state.current.highLabel
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = ClearSpacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.isFirst && !eveningStep) {
                Spacer(Modifier.size(1.dp))
            } else {
                TextButton(
                    onClick = { if (eveningStep) eveningStep = false else viewModel.previous() },
                    modifier = Modifier.heightIn(min = 56.dp)
                ) { Text("Zurück") }
            }
            Button(
                onClick = {
                    when {
                        eveningStep -> viewModel.finish { eveningViewModel.save(onFinished) }
                        state.isLast -> eveningStep = true
                        else -> viewModel.next()
                    }
                },
                modifier = Modifier.heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (eveningStep) "Fertig" else "Weiter")
            }
        }
    }
}

/** Skala 1 bis 10 in zwei Reihen mit je fuenf grossen Touch-Flaechen. */
@Composable
internal fun ScaleSelector(
    selected: Int?,
    onSelect: (Int) -> Unit,
    lowLabel: String,
    highLabel: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        (1..10).chunked(5).forEach { rowValues ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                rowValues.forEach { value ->
                    ScaleButton(value, value == selected, onSelect)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "1 = $lowLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                "10 = $highLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ScaleButton(value: Int, isSelected: Boolean, onSelect: (Int) -> Unit) {
    val base = scaleColor(value)
    // Nicht ausgewaehlt: helle Variante. Ausgewaehlt: volle Farbe plus dunkler Rand.
    val background = if (isSelected) base else lerp(base, Color.White, 0.55f)
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(background)
            .then(if (isSelected) Modifier.border(3.dp, ClearTextDark, CircleShape) else Modifier)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(value) })
            .semantics { contentDescription = "$value von 10" },
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$value",
            color = ScaleTextColor,
            fontSize = 18.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Dezenter Verlauf: gedecktes Rot -> neutrales Beige -> gedecktes Gruen
// Die Zahlen liegen auf hellen Kreisen, darum immer dunkle Schrift
private val ScaleTextColor = Color(0xFF34483D)
private val ScaleRed = Color(0xFFCF7466)
private val ScaleNeutral = Color(0xFFE6DFC3)
private val ScaleGreen = Color(0xFF8DAA8F)

private fun scaleColor(value: Int): Color {
    val t = (value - 1) / 9f
    return if (t < 0.5f) lerp(ScaleRed, ScaleNeutral, t / 0.5f)
    else lerp(ScaleNeutral, ScaleGreen, (t - 0.5f) / 0.5f)
}

/** Letzter Schritt des Check-ins: was geschafft wurde und drei gute Dinge. Alles ist freiwillig. */
@Composable
private fun EveningStepFields(viewModel: EveningEntryViewModel) {
    val draft by viewModel.draft.collectAsState()
    Text(
        "Erfolge und gute Dinge",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
        "Alles ist freiwillig. Ein Stichwort reicht, auch winzige Dinge zählen.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    OutlinedTextField(
        value = draft.success,
        onValueChange = viewModel::onSuccessChange,
        label = { Text("Was habe ich heute geschafft?") },
        minLines = 2,
        modifier = Modifier.fillMaxWidth()
    )
    Text("Was war heute gut oder okay?", style = MaterialTheme.typography.titleMedium)
    repeat(3) { index ->
        OutlinedTextField(
            value = draft.goodTexts[index],
            onValueChange = { viewModel.onGoodTextChange(index, it) },
            label = { Text("Gutes ${index + 1}") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = draft.goodReasons[index],
            onValueChange = { viewModel.onGoodReasonChange(index, it) },
            label = { Text("Warum ist das passiert? (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
