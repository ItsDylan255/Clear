package com.example.clear.ui.settings

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.ClearDatabase
import com.example.clear.data.ExtraChip
import com.example.clear.data.ExtraChipRepository
import com.example.clear.tr
import com.example.clear.ui.components.AddButton
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.components.ClearEmptyState
import com.example.clear.ui.components.TextInputDialog
import com.example.clear.ui.theme.ClearSpacing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ExtrasSettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = ExtraChipRepository(ClearDatabase.getInstance(app).extraChipDao())

    val chips: StateFlow<List<ExtraChip>?> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun add(name: String) = viewModelScope.launch { repository.add(name) }
    fun rename(chip: ExtraChip, name: String) = viewModelScope.launch { repository.rename(chip, name) }
    fun setActive(chip: ExtraChip, active: Boolean) = viewModelScope.launch { repository.setActive(chip, active) }
}

/**
 * Eigene Extras-Chips fuer den Faktenteil des Check-ins. Antippen = umbenennen,
 * Schalter = aktiv. Deaktivierte Chips verschwinden aus der Auswahl, alte Eintraege behalten sie.
 */
@Composable
fun ExtrasSettingsScreen(onBack: () -> Unit, viewModel: ExtrasSettingsViewModel = viewModel()) {
    val chips by viewModel.chips.collectAsState()
    var adding by remember { mutableStateOf(false) }
    var renaming by remember { mutableStateOf<ExtraChip?>(null) }

    ClearDetailScreen(title = tr("Extras", "Extras"), onBack = onBack) {
        AddButton(tr("Extra anlegen", "Add extra")) { adding = true }
        val list = chips
        when {
            list == null -> Unit
            list.isEmpty() -> ClearEmptyState(
                icon = Icons.Outlined.Label,
                title = tr("Noch keine Extras", "No extras yet")
            )
            else -> ClearCard {
                list.forEachIndexed { index, chip ->
                    if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clip(MaterialTheme.shapes.small)
                            .clickable { renaming = chip },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                    ) {
                        Text(
                            chip.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (chip.active) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = chip.active,
                            onCheckedChange = { viewModel.setActive(chip, it) }
                        )
                    }
                }
            }
        }
    }

    if (adding) {
        TextInputDialog(
            title = tr("Neues Extra", "New extra"),
            label = tr("Name", "Name"),
            initial = "",
            onConfirm = { viewModel.add(it); adding = false },
            onDismiss = { adding = false }
        )
    }
    renaming?.let { chip ->
        TextInputDialog(
            title = tr("Extra umbenennen", "Rename extra"),
            label = tr("Name", "Name"),
            initial = chip.name,
            onConfirm = { viewModel.rename(chip, it); renaming = null },
            onDismiss = { renaming = null }
        )
    }
}
