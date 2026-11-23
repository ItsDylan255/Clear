package com.example.clear.ui.home

import com.example.clear.ui.theme.ClearNoticeSoft
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.Close
import android.content.Intent
import android.content.ActivityNotFoundException
import androidx.compose.ui.platform.LocalContext
import com.example.clear.data.EarlyWarningStore
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.clear.AppLanguage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.tr
import com.example.clear.ui.checkin.CheckInWarningsViewModel
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.filled.Check
import com.example.clear.ui.checkin.Phq9HubViewModel
import com.example.clear.ui.checkin.Who5HubViewModel
import com.example.clear.ui.checkin.WsasHubViewModel
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearSpacing
import java.time.LocalTime

/** Ein Fragebogen auf der Startseite. */
enum class Questionnaire(val title: String) { Phq9("PHQ-9"), Who5("WHO-5"), Wsas("WSAS") }

/**
 * Reiter Check-in: Begruessung, der grosse Knopf fuer den Check-in, die drei Teile einzeln,
 * und die Frageboegen. Uebersicht, Verlauf und Einstellungen oben als Symbole. Heute schon Erledigtes ist ausgegraut.
 */
@Composable
fun HomeScreen(
    onStartCheckIn: (parts: String) -> Unit,
    onOpenQuestionnaire: (Questionnaire, due: Boolean) -> Unit,
    onOpenAreas: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEmergencyCard: () -> Unit,
    onOpenBadDayPlan: () -> Unit,
    warningsViewModel: CheckInWarningsViewModel = viewModel(),
    phqViewModel: Phq9HubViewModel = viewModel(),
    who5ViewModel: Who5HubViewModel = viewModel(),
    wsasViewModel: WsasHubViewModel = viewModel()
) {
    val today by warningsViewModel.todayParts.collectAsState()
    val phqReady by phqViewModel.available.collectAsState()
    val who5Ready by who5ViewModel.available.collectAsState()
    val wsasReady by wsasViewModel.available.collectAsState()

    val greeting = remember {
        val hour = LocalTime.now().hour
        when {
            hour < 11 -> tr("Guten Morgen", "Good morning")
            hour < 18 -> tr("Guten Tag", "Good afternoon")
            else -> tr("Guten Abend", "Good evening")
        }
    }

    val streak by warningsViewModel.streakDays.collectAsState()
    val warningVisible by EarlyWarningStore.visible.collectAsState()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Kleine Statuszeile: Datum und, wenn vorhanden, die Check-in-Serie
    val dateText = remember {
        val pattern = tr("EEEE, d. MMMM", "EEEE, MMMM d")
        LocalDate.now().format(DateTimeFormatter.ofPattern(pattern, AppLanguage.locale))
    }
    val streakText = when {
        streak <= 0 -> null
        streak == 1 -> tr("1 Tag eingecheckt", "1 day checked in")
        else -> tr("$streak Tage in Folge", "$streak days in a row")
    }

    ClearScreen(
        title = greeting,
        subtitle = listOfNotNull(dateText, streakText).joinToString(" · "),
        actions = {
            IconButton(onClick = onOpenAreas) {
                Icon(Icons.Outlined.GridView, contentDescription = tr("Übersicht", "Overview"))
            }
            IconButton(onClick = onOpenHistory) {
                Icon(Icons.Outlined.Insights, contentDescription = tr("Verlauf", "History"))
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Outlined.Settings, contentDescription = tr("Einstellungen", "Settings"))
            }
        }
    ) {
        // Fruehwarnung: sachlich und freundlich, mit drei moeglichen naechsten Schritten
        if (warningVisible) {
            EarlyWarningCard(
                onOpenEmergencyCard = onOpenEmergencyCard,
                onOpenBadDayPlan = onOpenBadDayPlan,
                onDismiss = { EarlyWarningStore.dismiss(context) }
            )
        }

        // Grosser Knopf: startet die noch offenen Teile nacheinander. Alles erledigt: ausgegraut.
        val missing = today.missing
        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                onStartCheckIn(missing)
            },
            enabled = missing.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
            shape = CircleShape
        ) { Text(tr("Check-in", "Check-in"), style = MaterialTheme.typography.titleMedium) }

        // Die drei Teile einzeln. Erledigt: ausgegraut, Antippen zeigt und bearbeitet den Eintrag.
        listOf(
            1 to tr("Wie geht es mir?", "How am I doing?"),
            2 to tr("Was habe ich gemacht?", "What did I do?"),
            3 to tr("Was war gut?", "What was good?")
        ).forEach { (part, title) ->
            HomeRow(title = title, done = today.isDone(part)) { onStartCheckIn(part.toString()) }
        }

        // Frageboegen: noch nicht wieder faellig = ausgegraut, Antippen zeigt Ergebnis und Verlauf
        SectionLabel(tr("FRAGEBÖGEN", "QUESTIONNAIRES"))
        listOf(
            Questionnaire.Phq9 to phqReady,
            Questionnaire.Who5 to who5Ready,
            Questionnaire.Wsas to wsasReady
        ).forEach { (questionnaire, due) ->
            HomeRow(title = questionnaire.title, done = !due) { onOpenQuestionnaire(questionnaire, due) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = ClearSpacing.small)
    )
}

/** Eine Zeile zum Antippen. `done` = ausgegraut (bleibt antippbar). */
@Composable
private fun HomeRow(title: String, done: Boolean, onClick: () -> Unit) {
    ClearCard(onClick = onClick, modifier = Modifier.alpha(if (done) 0.5f else 1f)) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Icon(
                if (done) Icons.Filled.Check else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (done) tr("Erledigt", "Done") else null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Karte der Fruehwarnung auf der Startseite. */
@Composable
private fun EarlyWarningCard(
    onOpenEmergencyCard: () -> Unit,
    onOpenBadDayPlan: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    ClearCard(containerColor = ClearNoticeSoft) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                tr("Deine Werte sinken seit zwei Wochen", "Your values have been dropping for two weeks"),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f).padding(top = 8.dp)
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = tr("Schließen", "Close"))
            }
        }
        Button(
            onClick = onOpenEmergencyCard,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = CircleShape
        ) { Text(tr("Notfallkarte öffnen", "Open emergency card")) }
        OutlinedButton(
            onClick = onOpenBadDayPlan,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = CircleShape
        ) { Text(tr("Plan für schlechte Tage öffnen", "Open plan for bad days")) }
        OutlinedButton(
            onClick = {
                // Telefon-App ohne Nummer oeffnen, gewaehlt wird selbst
                try {
                    context.startActivity(Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } catch (_: ActivityNotFoundException) { }
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = CircleShape
        ) { Text(tr("Mach einen Termin bei deinem Hausarzt", "Make an appointment with your doctor")) }
    }
}
