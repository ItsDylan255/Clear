package com.example.clear.ui.checkin

import android.app.Application
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import com.example.clear.data.ClearDatabase
import com.example.clear.data.ExtraChip
import com.example.clear.domain.Correlation
import com.example.clear.domain.CorrelationCalculator
import com.example.clear.domain.CorrelationSubject
import com.example.clear.model.CheckInCategory
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ClearEmptyState
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.ZoneId

data class CorrelationsData(
    val available: Boolean,
    val items: List<Correlation>,
    val chips: Map<Long, ExtraChip>
)

class CorrelationsViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ClearDatabase.getInstance(app)

    /** Gewaehlte Kategorie, Standard: Stimmung. */
    val category = MutableStateFlow(CheckInCategory.Mood)
    /** false = gleicher Tag, true = naechster Tag. */
    val nextDay = MutableStateFlow(false)

    /** Ist die Ansicht verfuegbar (mindestens 14 Check-ins mit Faktenteil)? */
    val available: StateFlow<Boolean> = db.checkInDao().observeAll()
        .map { CorrelationCalculator.isAvailable(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val data: StateFlow<CorrelationsData?> = combine(
        db.checkInDao().observeAll(),
        db.extraChipDao().observeAll(),
        category,
        nextDay
    ) { entries, chips, cat, next ->
        val available = CorrelationCalculator.isAvailable(entries)
        CorrelationsData(
            available = available,
            items = if (available) {
                CorrelationCalculator.calculate(entries, cat, next, chips.map { it.id }, ZoneId.systemDefault())
            } else emptyList(),
            chips = chips.associateBy { it.id }
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

/** Zusammenhaenge zwischen Faktenteil und einer waehlbaren Kategorie. */
@Composable
fun CorrelationsScreen(onBack: () -> Unit, viewModel: CorrelationsViewModel = viewModel()) {
    val data by viewModel.data.collectAsState()
    val category by viewModel.category.collectAsState()
    val nextDay by viewModel.nextDay.collectAsState()

    ClearDetailScreen(title = tr("Zusammenhänge", "Connections"), onBack = onBack) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
        ) {
            CheckInCategory.entries.forEach { c ->
                FilterChip(
                    selected = c == category,
                    onClick = { viewModel.category.value = c },
                    label = { Text(c.title) }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
            FilterChip(
                selected = !nextDay,
                onClick = { viewModel.nextDay.value = false },
                label = { Text(tr("Gleicher Tag", "Same day")) }
            )
            FilterChip(
                selected = nextDay,
                onClick = { viewModel.nextDay.value = true },
                label = { Text(tr("Nächster Tag", "Next day")) }
            )
        }

        val d = data
        when {
            d == null -> CircularProgressIndicator(modifier = Modifier.size(32.dp))
            !d.available || d.items.isEmpty() -> ClearEmptyState(
                icon = Icons.Outlined.Insights,
                title = tr("Noch zu wenige Daten", "Not enough data yet")
            )
            else -> d.items.forEach { item -> CorrelationCard(item, category, d.chips) }
        }
    }
}

@Composable
private fun CorrelationCard(item: Correlation, category: CheckInCategory, chips: Map<Long, ExtraChip>) {
    val (title, labelOf) = when (val s = item.subject) {
        is CorrelationSubject.Fact -> s.fact.title to { key: String -> s.fact.option(key)?.label ?: key }
        is CorrelationSubject.Day -> "${tr("Tag", "Day")}: ${s.dayType.label}" to ::withLabel
        is CorrelationSubject.Extra -> (chips[s.chipId]?.name ?: "–") to ::withLabel
    }
    ClearCard {
        Text(title, style = MaterialTheme.typography.titleMedium)
        item.groups.forEachIndexed { index, group ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 36.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
            ) {
                Text(labelOf(group.key), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                Text(
                    tr("${group.days} Tage", "${group.days} days"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${category.title} Ø ${formatAverage(group.average)}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontFeatureSettings = "tnum")
                )
            }
        }
    }
}

private fun withLabel(key: String): String =
    if (key == CorrelationCalculator.WITH) tr("mit", "with") else tr("ohne", "without")
