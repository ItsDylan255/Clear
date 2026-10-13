package com.example.clear.ui.checkin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clear.domain.WarningState
import com.example.clear.model.CheckInCategory
import com.example.clear.ui.akut.ClearWarningSoft
import com.example.clear.ui.components.ClearCard
import com.example.clear.ui.components.ClearScreen
import com.example.clear.ui.theme.ClearCard as ClearCardColor
import com.example.clear.ui.theme.ClearSpacing
import com.example.clear.ui.theme.ClearSurfaceSoft
import com.example.clear.ui.theme.ClearTextDark

@Composable
fun CheckInStartScreen(
    onStartCheckIn: () -> Unit,
    onOpenCategory: (CheckInCategory) -> Unit,
    warningsViewModel: CheckInWarningsViewModel = viewModel()
) {
    val analysis by warningsViewModel.analysis.collectAsState()
    val checkedInToday by warningsViewModel.checkedInToday.collectAsState()
    fun stateOf(category: CheckInCategory): WarningState =
        analysis?.categories?.get(category.id)?.state ?: WarningState.Normal

    ClearScreen(title = "Check In", intro = "Nimm dir einen Moment für dich.") {
        // 1. Button immer direkt unter Titel und Einleitung
        Button(
            onClick = onStartCheckIn,
            enabled = !checkedInToday,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Check-in starten", style = MaterialTheme.typography.titleMedium)
        }
        if (checkedInToday) {
            Text(
                "Für heute hast du schon eingecheckt. Morgen ist wieder Platz dafür.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Allgemeiner Hinweis, nur wenn mehrere Bereiche auffaellig sind
        if (analysis?.showGeneralHint == true) {
            ClearCard(containerColor = ClearWarningSoft) {
                Text(GENERAL_WARNING_HINT, style = MaterialTheme.typography.bodyLarge)
            }
        }

        // 2. Allgemein: groesser und farbig abgesetzt
        CategoryCard(CheckInCategory.General, highlighted = true,
            state = stateOf(CheckInCategory.General)) {
            onOpenCategory(CheckInCategory.General)
        }

        // 3. Fuenf Spezialkategorien
        Text(
            "Deine Bereiche",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = ClearSpacing.small)
        )
        CheckInCategory.specialized.forEach { category ->
            CategoryCard(category, highlighted = false, state = stateOf(category)) {
                onOpenCategory(category)
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CheckInCategory,
    highlighted: Boolean,
    state: WarningState,
    onClick: () -> Unit
) {
    ClearCard(
        onClick = onClick,
        containerColor = when {
            state == WarningState.ContinuedLow -> ClearWarningSoft
            highlighted -> ClearSurfaceSoft
            else -> null
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().heightIn(min = if (highlighted) 88.dp else 64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(if (highlighted) 52.dp else 44.dp)
                    .clip(CircleShape)
                    .background(if (highlighted) ClearCardColor else ClearSurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = null, tint = ClearTextDark)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(category.title, style = MaterialTheme.typography.titleMedium)
                if (highlighted) {
                    Text(category.question, style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    category.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                warningLabel(state)?.let { label ->
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = ClearTextDark,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
