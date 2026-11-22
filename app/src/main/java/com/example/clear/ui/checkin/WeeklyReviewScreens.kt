package com.example.clear.ui.checkin

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingFlat
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.AppLanguage
import com.example.clear.data.CheckInEntry
import com.example.clear.data.ClearDatabase
import com.example.clear.data.EveningEntry
import com.example.clear.data.EveningType
import com.example.clear.data.ExtraChip
import com.example.clear.domain.ReviewDay
import com.example.clear.domain.WeekTrend
import com.example.clear.domain.WeeklyReview
import com.example.clear.domain.WeeklyReviewCalculator
import com.example.clear.model.CheckInFact
import com.example.clear.model.DayType
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ClearEmptyState
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Alles fuer den Wochenrueckblick: die Rechnung plus Abend-Eintraege und Chip-Namen. */
data class WeeklyReviewData(
    val review: WeeklyReview?,
    val evening: List<EveningEntry>,
    val chips: Map<Long, ExtraChip>
)

class WeeklyReviewViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ClearDatabase.getInstance(app)

    /** null = laedt noch. review == null = diese Woche hat zu wenig Check-ins. */
    val data: StateFlow<WeeklyReviewData?> = combine(
        db.checkInDao().observeAll(),
        db.eveningDao().observeAll(),
        db.extraChipDao().observeAll()
    ) { entries, evening, chips ->
        val zone = ZoneId.systemDefault()
        val start = WeeklyReviewCalculator.reviewWeekStart(LocalDate.now(zone))
        WeeklyReviewData(
            review = WeeklyReviewCalculator.calculate(entries, start, zone),
            evening = evening,
            chips = chips.associateBy { it.id }
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

private val shortDate: DateTimeFormatter
    get() = DateTimeFormatter.ofPattern(tr("d. MMMM", "MMMM d"), AppLanguage.locale)

private fun fullDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(AppLanguage.locale))

/** Bildschirm 1: Durchschnitt pro Kategorie mit Richtung zur Vorwoche. */
@Composable
fun WeeklyReviewScreen(
    onBack: () -> Unit,
    onOpenDays: () -> Unit,
    viewModel: WeeklyReviewViewModel = viewModel()
) {
    val data by viewModel.data.collectAsState()
    ClearDetailScreen(title = tr("Wochenrückblick", "Weekly review"), onBack = onBack) {
        val d = data
        val review = d?.review
        when {
            d == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            review == null -> ClearEmptyState(
                icon = Icons.Outlined.CalendarMonth,
                title = tr("Noch zu wenige Check-ins diese Woche", "Not enough check-ins this week yet")
            )
            else -> {
                Text(
                    "${review.weekStart.format(shortDate)} – ${review.weekEnd.format(shortDate)} · " +
                        tr("${review.checkInCount} Check-ins", "${review.checkInCount} check-ins"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ClearCard {
                    review.categories.forEachIndexed { index, week ->
                        if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                        ) {
                            Text(week.category.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Text(
                                week.average?.let { "Ø ${formatAverage(it)}" } ?: "–",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            TrendIcon(week.trend)
                        }
                    }
                }
                Button(
                    onClick = onOpenDays,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    shape = CircleShape
                ) { Text(tr("Bester und schwierigster Tag", "Best and hardest day")) }
            }
        }
    }
}

@Composable
private fun TrendIcon(trend: WeekTrend?) {
    val (icon, label) = when (trend) {
        WeekTrend.Up -> Icons.AutoMirrored.Filled.TrendingUp to tr("gestiegen", "up")
        WeekTrend.Down -> Icons.AutoMirrored.Filled.TrendingDown to tr("gesunken", "down")
        WeekTrend.Same -> Icons.AutoMirrored.Filled.TrendingFlat to tr("gleich", "same")
        null -> null to null
    }
    if (icon != null) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
    } else {
        // Platz halten, damit die Zahlen untereinander stehen
        Box24()
    }
}

@Composable
private fun Box24() = androidx.compose.foundation.layout.Spacer(Modifier.size(24.dp))

/** Bildschirm 2: bester und schwierigster Tag mit Faktenteil, Extras und Teil 3. */
@Composable
fun WeeklyReviewDaysScreen(onBack: () -> Unit, viewModel: WeeklyReviewViewModel = viewModel()) {
    val data by viewModel.data.collectAsState()
    ClearDetailScreen(title = tr("Bester und schwierigster Tag", "Best and hardest day"), onBack = onBack) {
        val d = data
        val review = d?.review
        when {
            d == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            review == null -> ClearEmptyState(
                icon = Icons.Outlined.CalendarMonth,
                title = tr("Noch zu wenige Check-ins diese Woche", "Not enough check-ins this week yet")
            )
            else -> {
                review.bestDay?.let { DayCard(tr("Bester Tag", "Best day"), it, d) }
                val worst = review.worstDay
                if (worst != null && worst.date != review.bestDay?.date) {
                    DayCard(tr("Schwierigster Tag", "Hardest day"), worst, d)
                }
            }
        }
    }
}

@Composable
private fun DayCard(label: String, day: ReviewDay, data: WeeklyReviewData) {
    ClearCard {
        Text(
            label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(fullDate(day.date), style = MaterialTheme.typography.titleLarge)
        Text("Ø ${formatAverage(day.average)}", style = MaterialTheme.typography.bodyLarge)
        CheckInDayDetails(day.entries, data.chips, data.evening.filter { it.day == day.date.toEpochDay() })
    }
}

/**
 * Faktenteil, Tag, Extras und Teil 3 eines Tages als ruhige Liste. Felder ohne Eintrag fehlen.
 * Mehrere Check-ins an einem Tag: der neueste Eintrag pro Feld zaehlt.
 */
@Composable
internal fun CheckInDayDetails(entries: List<CheckInEntry>, chips: Map<Long, ExtraChip>, evening: List<EveningEntry>) {
    val newestFirst = entries.sortedByDescending { it.timestamp }
    val factLines = CheckInFact.entries.mapNotNull { fact ->
        val option = newestFirst.firstNotNullOfOrNull { fact.option(it.fact(fact)) } ?: return@mapNotNull null
        fact.title to option.label
    }
    val dayTypes = DayType.entries.filter { day -> entries.any { day in it.dayTypeSet } }
    val extras = entries.flatMap { it.extraIdSet }.distinct().mapNotNull { chips[it]?.name }
    val successes = evening.filter { it.type == EveningType.SUCCESS }
    val goods = evening.filter { it.type == EveningType.GOOD }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        factLines.forEach { (title, value) -> DetailLine(title, value) }
        if (dayTypes.isNotEmpty()) DetailLine(tr("Tag", "Day"), dayTypes.joinToString(", ") { it.label })
        if (extras.isNotEmpty()) DetailLine(tr("Extras", "Extras"), extras.joinToString(", "))
    }
    if (successes.isNotEmpty() || goods.isNotEmpty()) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        successes.forEach { DetailLine(tr("Geschafft", "Done"), it.text) }
        goods.forEach { good ->
            DetailLine(tr("Gut", "Good"), good.text)
            good.reason?.takeIf { it.isNotBlank() }?.let { reason ->
                Text(
                    tr("Warum: $reason", "Why: $reason"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailLine(title: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(0.6f))
    }
}
