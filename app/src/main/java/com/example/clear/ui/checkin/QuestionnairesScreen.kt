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

    ClearDetailScreen(title = "Fragebögen", onBack = onBack) {
        Text(
            "Drei kurze Fragebögen. Du kannst sie ausfüllen, wenn mindestens zwei Wochen seit dem letzten Mal vergangen sind. Alle sind freiwillig und unabhängig voneinander.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        QuestionnaireCard("PHQ-9", "Neun Fragen zu den letzten zwei Wochen", phqAvailable, onOpenPhq)
        QuestionnaireCard("WHO-5", "Fünf Fragen zu deinem Wohlbefinden", who5Available, onOpenWho5)
        QuestionnaireCard("WSAS", "Fünf Fragen dazu, wie sehr dich deine Probleme im Alltag beeinträchtigen", wsasAvailable, onOpenWsas)
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
                    if (available) "Bereit" else "Gerade erst ausgefüllt",
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
