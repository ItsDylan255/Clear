package com.example.clear.ui.anker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

/**
 * Notfallkarte im Anker-Tab: Hier traegst du Notfallperson, Notfallplan und Warnzeichen ein und
 * aenderst sie. Im Akut-Tab wird die Notfallkarte nur angezeigt.
 */
@Composable
fun EmergencyManageScreen(
    onBack: () -> Unit,
    onEditPerson: () -> Unit,
    onEditPlan: () -> Unit,
    onEditWarningSigns: () -> Unit,
    viewModel: EmergencyCardViewModel = viewModel(),
    warningViewModel: WarningSignsViewModel = viewModel()
) {
    val card by viewModel.card.collectAsState()
    val signs by warningViewModel.items.collectAsState()
    val c = card
    val signList = signs.orEmpty()

    val personSummary = c?.let {
        listOf(it.contactName, it.contactPhone).filter { part -> part.isNotBlank() }.joinToString(" · ")
    }.orEmpty()
    val planSummary = c?.safetyPlan.orEmpty()

    ClearDetailScreen(title = "Notfallkarte", onBack = onBack) {
        EmergencyEntryCard(
            title = "Notfallperson",
            summary = personSummary.ifBlank { "Noch niemand eingetragen" },
            onClick = onEditPerson
        )
        EmergencyEntryCard(
            title = "Notfallplan",
            summary = planSummary.ifBlank { "Noch kein Plan eingetragen" },
            onClick = onEditPlan
        )
        EmergencyEntryCard(
            title = "Warnzeichen",
            summary = if (signList.isEmpty()) "Noch keine Warnzeichen eingetragen"
            else signList.joinToString(", ") { it.text },
            onClick = onEditWarningSigns
        )
    }
}

@Composable
private fun EmergencyEntryCard(title: String, summary: String, onClick: () -> Unit) {
    ClearCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
