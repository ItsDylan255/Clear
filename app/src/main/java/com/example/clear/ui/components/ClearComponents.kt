package com.example.clear.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.clear.ui.theme.ClearSpacing

/** Grundlayout jeder Hauptseite: Titel, optionaler Einfuehrungstext, scrollbarer Inhalt. */
@Composable
fun ClearScreen(
    title: String,
    intro: String? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        if (intro != null) {
            Text(
                intro,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        content()
    }
}

/** Layout fuer Detailseiten mit Zurueck-Pfeil. */
@Composable
fun ClearDetailScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.small),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            content()
        }
    }
}

/** Runde Karte. Mit `onClick` wird sie antippbar. Standardfarbe: Weiss. */
@Composable
fun ClearCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = CardDefaults.cardColors(
        containerColor = containerColor ?: MaterialTheme.colorScheme.surface
    )
    val inner: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(ClearSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.small),
            content = content
        )
    }
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            content = inner
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            content = inner
        )
    }
}

/** Platzhalter, bis ein Bereich in einem spaeteren Schritt umgesetzt wird. */
@Composable
fun PlaceholderCard(text: String) {
    ClearCard {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
