package com.example.clear.ui.checkin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.data.IdeaLevel
import com.example.clear.domain.CategoryLevel
import com.example.clear.model.CheckInCategory
import com.example.clear.tr
import com.example.clear.ui.components.ClearDetailScreen

/** Ein Check-in-Bereich: Level der letzten 14 Tage, die Frage und der Verlauf. */
@Composable
fun CategoryDetailScreen(
    category: CheckInCategory,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val levels by warningsViewModel.levels.collectAsState()
    val info = levels?.get(category.id)

    ClearDetailScreen(title = category.title, onBack = onBack) {
        LevelNotice(info)
        Text(
            category.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedButton(
            onClick = onOpenHistory,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) { Text(tr("Verlauf ansehen", "View history")) }
    }
}

/** Level der Kategorie -> passende Stufe fuer persoenliche Ideen. Ohne Wert: normal. */
internal fun CategoryLevel?.toIdeaLevel(): IdeaLevel = when (this) {
    CategoryLevel.Low -> IdeaLevel.Severe
    CategoryLevel.Medium -> IdeaLevel.Sinking
    CategoryLevel.High, null -> IdeaLevel.Normal
}

internal fun ideaLevelPhrase(level: IdeaLevel): String = when (level) {
    IdeaLevel.Normal -> levelPhrase(CategoryLevel.High)
    IdeaLevel.Sinking -> levelPhrase(CategoryLevel.Medium)
    IdeaLevel.Severe -> levelPhrase(CategoryLevel.Low)
}
