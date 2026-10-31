package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clear.model.Exercise
import com.example.clear.tr
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft

/**
 * Eine Uebung im Akut-Bereich: Du waehlst sie aus und siehst sofort die Schritte.
 * Keine Fragen und kein Startknopf.
 */
@Composable
fun ExerciseScreen(
    exercise: Exercise,
    onBack: () -> Unit
) {
    ClearDetailScreen(title = exercise.title, onBack = onBack) {
        Text(
            exercise.category.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
        Text(exercise.goal, style = MaterialTheme.typography.bodyLarge)

        ClearCard {
            exercise.steps.forEachIndexed { index, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(ClearSpacing.small)) {
                    Text(
                        "${index + 1}.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.widthIn(min = 24.dp)
                    )
                    Text(step, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        ClearCard(containerColor = ClearSurfaceSoft) {
            Text(exercise.endpoint, style = MaterialTheme.typography.bodyLarge)
            exercise.note?.let { note ->
                Text(
                    note,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text(tr("Zurück zur Auswahl", "Back to the selection")) }
    }
}
