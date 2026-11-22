package com.example.clear.ui.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.clear.data.CheckInFactAnswers
import com.example.clear.data.ExtraChip
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import com.example.clear.tr
import com.example.clear.ui.components.TextInputDialog
import com.example.clear.ui.theme.ClearSpacing

/**
 * Teil 2 des Check-ins "Was habe ich gemacht?": nur Antipp-Chips, alles optional.
 * Oben "Letzte Nacht / gestern" (Schlaf, Alkohol), darunter "Heute".
 * Einzelauswahl: nochmal antippen = wieder leer. Tag und Extras: Mehrfachauswahl.
 */
@Composable
internal fun CheckInFactsStep(
    facts: CheckInFactAnswers,
    chips: List<ExtraChip>,
    onToggleFact: (CheckInFact, String) -> Unit,
    onToggleDayType: (DayType) -> Unit,
    onToggleExtra: (Long) -> Unit,
    onAddExtra: (String) -> Unit
) {
    var adding by rememberSaveable { mutableStateOf(false) }

    Text(tr("Was habe ich gemacht?", "What did I do?"), style = MaterialTheme.typography.headlineMedium)

    FactSection(tr("Letzte Nacht / gestern", "Last night / yesterday")) {
        CheckInFact.entries.filter { it.aboutYesterday }.forEach { fact ->
            SingleFact(fact, facts.levels[fact], onToggleFact)
        }
    }

    FactSection(tr("Heute", "Today")) {
        CheckInFact.entries.filter { !it.aboutYesterday }.forEach { fact ->
            SingleFact(fact, facts.levels[fact], onToggleFact)
        }
        FactGroup(tr("Tag", "Day")) {
            DayType.entries.forEach { day ->
                FilterChip(
                    selected = day in facts.dayTypes,
                    onClick = { onToggleDayType(day) },
                    label = { Text(day.label) }
                )
            }
        }
        FactGroup(tr("Extras", "Extras")) {
            chips.forEach { chip ->
                FilterChip(
                    selected = chip.id in facts.extraChipIds,
                    onClick = { onToggleExtra(chip.id) },
                    label = { Text(chip.name) }
                )
            }
            AssistChip(
                onClick = { adding = true },
                label = { Icon(Icons.Filled.Add, contentDescription = tr("Extra anlegen", "Add extra")) }
            )
        }
    }

    if (adding) {
        TextInputDialog(
            title = tr("Neues Extra", "New extra"),
            label = tr("Name", "Name"),
            initial = "",
            onConfirm = { name ->
                onAddExtra(name)
                adding = false
            },
            onDismiss = { adding = false }
        )
    }
}

@Composable
private fun FactSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = ClearSpacing.small)
        )
        content()
    }
}

@Composable
private fun SingleFact(fact: CheckInFact, selectedId: String?, onToggle: (CheckInFact, String) -> Unit) {
    FactGroup(fact.title) {
        fact.options.forEach { option ->
            FilterChip(
                selected = option.id == selectedId,
                onClick = { onToggle(fact, option.id) },
                label = { Text(option.label) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FactGroup(title: String, chips: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
            chips()
        }
    }
}
