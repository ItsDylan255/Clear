package com.example.clear.ui.checkin

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.AppLanguage
import com.example.clear.data.ClearDatabase
import com.example.clear.domain.ThenAndNow
import com.example.clear.domain.ThenAndNowCalculator
import com.example.clear.domain.ThenNowValue
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId

class ThenAndNowViewModel(app: Application) : AndroidViewModel(app) {
    private val db = ClearDatabase.getInstance(app)

    /** null = laedt noch oder weniger als 28 Check-ins. */
    val data: StateFlow<ThenAndNow?> = combine(
        db.checkInDao().observeAll(),
        db.phq9Dao().observeAll(),
        db.who5Dao().observeAll()
    ) { entries, phq, who ->
        ThenAndNowCalculator.calculate(
            entries = entries,
            phq9 = phq.map { it.createdAt to it.total },
            who5 = who.map { it.createdAt to it.percent },
            now = Instant.now(),
            zone = ZoneId.systemDefault()
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

/** Karte "Damals und heute" im Verlauf. Erscheint erst ab 28 Check-ins. */
@Composable
internal fun ThenAndNowCard(viewModel: ThenAndNowViewModel = viewModel()) {
    val data by viewModel.data.collectAsState()
    val d = data ?: return
    ClearCard {
        Text(tr("Damals und heute", "Then and now"), style = MaterialTheme.typography.titleLarge)
        ValueRow(
            label = "",
            values = listOf(tr("Damals", "Then"), tr("Heute", "Now"), tr("Differenz", "Change")),
            style = MaterialTheme.typography.labelMedium,
            muted = true
        )
        d.categories.forEach { (category, value) ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ValueRow(category.title, value.cells(decimals = true))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ValueRow("PHQ-9", d.phq9.cells(decimals = false))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        ValueRow("WHO-5", d.who5.cells(decimals = false))
    }
}

private fun ThenNowValue.cells(decimals: Boolean): List<String> {
    fun fmt(v: Double?) = when {
        v == null -> "–"
        decimals -> formatAverage(v)
        else -> v.toInt().toString()
    }
    val diff = difference?.let {
        val sign = if (it > 0) "+" else ""
        if (decimals) sign + String.format(AppLanguage.locale, "%.1f", it) else sign + it.toInt()
    } ?: "–"
    return listOf(fmt(then), fmt(now), diff)
}

@Composable
private fun ValueRow(
    label: String,
    values: List<String>,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    muted: Boolean = false
) {
    val color = if (muted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = color, modifier = Modifier.weight(1.4f))
        values.forEach { value ->
            Text(
                value,
                style = style.copy(fontFeatureSettings = "tnum"),
                color = color,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
