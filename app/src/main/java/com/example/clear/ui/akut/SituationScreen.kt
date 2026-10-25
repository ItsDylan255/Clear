package com.example.clear.ui.akut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.clear.model.Exercise
import com.example.clear.model.ExerciseCategory
import com.example.clear.model.Situation
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearDetailScreen
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearNoticeSoft
import com.example.clear.ui.theme.ClearSurfaceSoft

@Composable
fun SituationScreen(
    situation: Situation,
    onBack: () -> Unit,
    onOpenExercise: (Exercise) -> Unit,
    onOpenAnker: () -> Unit
) {
    ClearDetailScreen(title = situation.name, onBack = onBack) {
        situation.intro?.let { intro ->
            ClearCard(containerColor = ClearSurfaceSoft) {
                Text(intro, style = MaterialTheme.typography.bodyLarge)
            }
        }
        situation.hint?.let { hint ->
            ClearCard(containerColor = ClearNoticeSoft) {
                Text(hint, style = MaterialTheme.typography.bodyLarge)
            }
        }

        // Ein einziges Feld zu den persoenlichen Inhalten (Zitate, Bilder, Musik, Briefe)
        ClearCard(onClick = onOpenAnker, containerColor = ClearSurfaceSoft) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
            ) {
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                Text(
                    "Mein Anker",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        situation.exercises.forEach { exercise ->
            ClearCard(onClick = { onOpenExercise(exercise) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
                ) {
                    Icon(
                        exercise.category.icon(),
                        contentDescription = exercise.category.label,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        exercise.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/** Symbol je Uebungskategorie (steht links neben dem Titel der Uebung). */
private fun ExerciseCategory.icon(): ImageVector = when (this) {
    ExerciseCategory.Body -> Icons.Outlined.AccessibilityNew
    ExerciseCategory.Thoughts -> Icons.Outlined.Psychology
    ExerciseCategory.Feelings -> Icons.Outlined.FavoriteBorder
    ExerciseCategory.Attention -> Icons.Outlined.Visibility
    ExerciseCategory.Action -> Icons.Outlined.Flag
    ExerciseCategory.SelfSupport -> Icons.Outlined.Spa
}
