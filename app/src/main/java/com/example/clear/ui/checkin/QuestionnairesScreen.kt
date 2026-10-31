package com.example.clear.ui.checkin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

/** Uebersicht der monatlichen Frageboegen. Hier steht auch, ob ein Fragebogen schon erledigt ist. */
@Composable
fun QuestionnairesScreen(
    onBack: () -> Unit,
    onOpenPhq: () -> Unit,
    onOpenWho5: () -> Unit,
    onOpenWsas: () -> Unit,
    phqViewModel: Phq9HubViewModel = viewModel(),
    who5ViewModel: Who5HubViewModel = viewModel(),
    wsasViewModel: WsasHubViewModel = viewModel()
) {
    val phqAvailable by phqViewModel.available.collectAsState()
    val who5Available by who5ViewModel.available.collectAsState()
    val wsasAvailable by wsasViewModel.available.collectAsState()

    ClearDetailScreen(title = tr("Fragebögen", "Questionnaires"), onBack = onBack) {
        QuestionnaireCard("PHQ-9", tr(
            "Neun Fragen zu den letzten zwei Wochen",
            "Nine questions about the last two weeks"
        ), phqAvailable, onOpenPhq)
        QuestionnaireCard("WHO-5", tr(
            "Fünf Fragen zu deinem Wohlbefinden",
            "Five questions about your well-being"
        ), who5Available, onOpenWho5)
        QuestionnaireCard("WSAS", tr(
            "Fünf Fragen dazu, wie sehr dich deine Probleme im Alltag beeinträchtigen",
            "Five questions about how much your problems affect your daily life"
        ), wsasAvailable, onOpenWsas)
    }
}

@Composable
private fun QuestionnaireCard(title: String, description: String, available: Boolean, onClick: () -> Unit) {
    ClearCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    if (available) tr("Bereit", "Ready") else tr("Gerade erst ausgefüllt", "Just filled in"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
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
