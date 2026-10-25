package com.example.clear.ui.settings

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.DataWiper
import com.example.clear.ui.theme.ClearNoticeSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyDataState(
    val isDeleting: Boolean = false,
    val deleted: Boolean = false,
    val failed: Boolean = false
)

class MyDataViewModel(app: Application) : AndroidViewModel(app) {
    private val _state = MutableStateFlow(MyDataState())
    val state: StateFlow<MyDataState> = _state.asStateFlow()

    fun deleteAll() {
        if (_state.value.isDeleting) return
        _state.value = MyDataState(isDeleting = true)
        viewModelScope.launch {
            try {
                DataWiper.deleteEverything(getApplication())
                _state.update { MyDataState(deleted = true) }
            } catch (e: Exception) {
                _state.update { MyDataState(failed = true) }
            }
        }
    }
}

@Composable
fun MyDataScreen(onBack: () -> Unit, viewModel: MyDataViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var confirm by remember { mutableStateOf(false) }

    ClearDetailScreen(title = "Meine Daten", onBack = onBack) {
        if (state.deleted) {
            ClearCard { Text("Alle Daten wurden gelöscht.", style = MaterialTheme.typography.bodyLarge) }
        }
        if (state.failed) {
            ClearCard(containerColor = ClearNoticeSoft) {
                Text(
                    "Beim Löschen ist ein Fehler aufgetreten. Bitte versuche es noch einmal.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        OutlinedButton(
            onClick = { confirm = true },
            enabled = !state.isDeleting,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) { Text(if (state.isDeleting) "Wird gelöscht …" else "Alle meine Daten löschen") }
    }

    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Alle Daten löschen?") },
            text = {
                Text(
                    "Alles wird dauerhaft von diesem Handy gelöscht: Check-ins, Fragebögen, Ideen, " +
                        "Zitate, Briefe, Bilder, Musik, Notfallkarte und Erinnerungen. " +
                        "Das lässt sich nicht rückgängig machen."
                )
            },
            confirmButton = {
                TextButton(onClick = { confirm = false; viewModel.deleteAll() }) {
                    Text("Alles löschen")
                }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Abbrechen") } }
        )
    }
}
