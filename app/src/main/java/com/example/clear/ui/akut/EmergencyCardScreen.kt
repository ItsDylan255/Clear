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
import androidx.compose.material3.OutlinedButton
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
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

/**
 * Notfallkarte. Die festen Nummern funktionieren immer, auch ohne persoenliche Inhalte.
 * "Anrufen" oeffnet nur die Telefon-App. Gewaehlt wird erst, wenn du selbst tippst.
 */
@Composable
fun EmergencyCardScreen(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: EmergencyCardViewModel = viewModel()
) {
    val card by viewModel.card.collectAsState()
    val c = card

    ClearDetailScreen(title = "Notfallkarte", onBack = onBack) {
        Text(
            "Wichtige Nummern, wenn du Unterstützung brauchst.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        PhoneCard("Notruf", "112", "Bei akuter Gefahr für dich oder andere")
        PhoneCard("Telefonseelsorge", "0800 111 0 111", "Gespräch in schwierigen Zeiten, kostenlos")

        if (c != null) {
            Text("Deine persönlichen Angaben", style = MaterialTheme.typography.titleMedium)

            val hasContact = c.contactName.isNotBlank() || c.contactPhone.isNotBlank()
            val hasPlan = c.safetyPlan.isNotBlank()

            if (!hasContact && !hasPlan) {
                ClearCard {
                    Text(
                        "Du hast noch keine Notfallperson und keinen eigenen Plan eingetragen. " +
                            "Das ist freiwillig.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (hasContact) {
                PhoneCard(
                    title = c.contactName.ifBlank { "Meine Notfallperson" },
                    number = c.contactPhone,
                    description = c.contactNote
                )
            }
            if (hasPlan) {
                ClearCard {
                    Text("Mein Plan", style = MaterialTheme.typography.titleMedium)
                    Text(c.safetyPlan, style = MaterialTheme.typography.bodyLarge)
                }
            }
            OutlinedButton(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = MaterialTheme.shapes.large
            ) { Text(if (hasContact || hasPlan) "Bearbeiten" else "Eintragen") }
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
