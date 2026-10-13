package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.clear.model.Exercise
import com.example.clear.model.Situation
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import androidx.compose.ui.unit.dp

@Composable
fun SituationScreen(
    situation: Situation,
    onBack: () -> Unit,
    onOpenExercise: (Exercise) -> Unit,
    onOpenEmergency: () -> Unit
) {
    ClearDetailScreen(title = situation.name, onBack = onBack) {
        situation.intro?.let { intro ->
            ClearCard(containerColor = ClearSurfaceSoft) {
                Text(intro, style = MaterialTheme.typography.bodyLarge)
            }
        }
        situation.hint?.let { hint ->
            ClearCard(containerColor = ClearWarningSoft) {
                Text(hint, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Text(
            "Wähle eine Übung. Du kannst jederzeit abbrechen.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        situation.exercises.forEach { exercise ->
            ClearCard(onClick = { onOpenExercise(exercise) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(exercise.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            exercise.duration,
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

        EmergencyLink(onClick = onOpenEmergency)
    }
}
