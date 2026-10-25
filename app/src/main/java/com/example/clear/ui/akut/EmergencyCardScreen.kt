package com.example.clear.ui.akut

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.ui.anker.EmergencyCardViewModel
import com.example.clear.ui.anker.WarningSignsViewModel
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

/**
 * Notfallkarte zum Ansehen. Die festen Nummern funktionieren immer. Deine persoenlichen Angaben
 * (Notfallperson, Plan, Warnzeichen) erscheinen hier, sobald du sie im Anker-Tab eingetragen hast.
 * Hier kannst du nichts aendern. "Anrufen" oeffnet nur die Telefon-App.
 */
@Composable
fun EmergencyCardScreen(
    onBack: () -> Unit,
    viewModel: EmergencyCardViewModel = viewModel(),
    warningViewModel: WarningSignsViewModel = viewModel()
) {
    val card by viewModel.card.collectAsState()
    val signs by warningViewModel.items.collectAsState()
    val c = card
    val signList = signs.orEmpty()

    ClearDetailScreen(title = "Notfallkarte", onBack = onBack) {
        PhoneCard("Notruf", "112", "")
        PhoneCard("Telefonseelsorge", "0800 111 0 111", "")

        if (c != null) {
            if (c.contactName.isNotBlank() || c.contactPhone.isNotBlank()) {
                PhoneCard(
                    title = c.contactName.ifBlank { "Meine Notfallperson" },
                    number = c.contactPhone,
                    description = c.contactNote
                )
            }
            if (c.safetyPlan.isNotBlank()) {
                ClearCard {
                    Text("Mein Plan", style = MaterialTheme.typography.titleMedium)
                    Text(c.safetyPlan, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        if (signList.isNotEmpty()) {
            ClearCard {
                Text("Meine Warnzeichen", style = MaterialTheme.typography.titleMedium)
                signList.forEach { sign ->
                    Text("• ${sign.text}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
private fun PhoneCard(title: String, number: String, description: String) {
    val context = LocalContext.current
    ClearCard {
        Text(title, style = MaterialTheme.typography.titleMedium)
        if (number.isNotBlank()) {
            Text(number, style = MaterialTheme.typography.headlineMedium)
        }
        if (description.isNotBlank()) {
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (number.isNotBlank()) {
            Button(
                onClick = {
                    val dialable = number.filter { it.isDigit() || it == '+' }
                    try {
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$dialable")))
                    } catch (e: ActivityNotFoundException) {
                        // Keine Telefon-App auf diesem Geraet: nichts passiert
                    }
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)
                ) {
                    Icon(Icons.Filled.Phone, contentDescription = null)
                    Text("Anrufen")
                }
            }
        }
    }
}
