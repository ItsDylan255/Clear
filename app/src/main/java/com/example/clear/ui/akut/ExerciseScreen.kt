package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clear.model.Exercise
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing

/** Uebungsansicht: nichts startet automatisch, danach kommt keine Frage. */
@Composable
fun ExerciseScreen(
    exercise: Exercise,
    onBack: () -> Unit
) {
    ClearDetailScreen(title = exercise.title, onBack = onBack) {
        Text(
            exercise.duration,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(exercise.explanation, style = MaterialTheme.typography.bodyLarge)

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

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text("Beenden") }
    }
}
