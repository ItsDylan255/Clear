package com.example.clear.ui.home

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.ClearDatabase
import com.example.clear.data.PersonalIdeaRepository
import com.example.clear.ui.checkin.ActiveIdeas
import com.example.clear.ui.checkin.CheckInWarningsViewModel
import com.example.clear.ui.checkin.Phq9HubViewModel
import com.example.clear.ui.checkin.Who5HubViewModel
import com.example.clear.ui.checkin.WsasHubViewModel
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime

/** Laufende Ideen mit Titel, aelteste zuerst. */
class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repository =
        PersonalIdeaRepository(ClearDatabase.getInstance(app).personalIdeaDao())

    init {
        ActiveIdeas.load(app)
    }

    val running: StateFlow<List<Pair<Long, String>>> = ActiveIdeas.running
        .map { map ->
            map.values.sortedBy { it.startedAt }.mapNotNull { run ->
                repository.get(run.ideaId)?.let { run.ideaId to it.title }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}

/**
 * Startseite: Begruessung, der Check-in (starten oder Verlauf), laufende Ideen und die Frageboegen.
 * Die acht Bereiche mit ihren Ideen findest du im Reiter "Bereiche".
 */
@Composable
fun HomeScreen(
    onStartCheckIn: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenQuestionnaires: () -> Unit,
    onOpenIdea: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel(),
    warningsViewModel: CheckInWarningsViewModel = viewModel(),
    phqViewModel: Phq9HubViewModel = viewModel(),
    who5ViewModel: Who5HubViewModel = viewModel(),
    wsasViewModel: WsasHubViewModel = viewModel()
) {
    val running by viewModel.running.collectAsState()
    val checkedInToday by warningsViewModel.checkedInToday.collectAsState()
    val phqReady by phqViewModel.available.collectAsState()
    val who5Ready by who5ViewModel.available.collectAsState()
    val wsasReady by wsasViewModel.available.collectAsState()

    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 11 -> "Guten Morgen"
            hour < 18 -> "Guten Tag"
            else -> "Guten Abend"
        }
    }
    val ready = listOfNotNull(
        "PHQ-9".takeIf { phqReady }, "WHO-5".takeIf { who5Ready }, "WSAS".takeIf { wsasReady }
    )

    ClearScreen(title = greeting) {
        // Check-in
        ClearCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Text(
                "Dein Check-in",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (checkedInToday) {
                Text(
                    "Du hast heute schon eingecheckt.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (checkedInToday) {
                Button(
                    onClick = onOpenHistory,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text("Verlauf ansehen") }
            } else {
                Button(
                    onClick = onStartCheckIn,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text("Check-in starten") }
                TextButton(
                    onClick = onOpenHistory,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) { Text("Verlauf ansehen") }
            }
        }

        // Laufende Ideen
        if (running.isNotEmpty()) {
            Text("Läuft gerade", style = MaterialTheme.typography.titleMedium)
            running.forEach { (id, title) ->
                ClearCard(onClick = { onOpenIdea(id) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Tippen zum Ansehen oder Beenden",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        }

        // Frageboegen
        ClearCard(onClick = onOpenQuestionnaires) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fragebögen", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (ready.isEmpty()) "Gerade ist alles erledigt"
                        else "Bereit: ${ready.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
}
