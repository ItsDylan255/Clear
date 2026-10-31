package com.example.clear.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.example.clear.tr
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

/**
 * Wie ClearScreen, aber die Eintraege fuellen die Hoehe bis zur unteren Leiste.
 * Jeder Eintrag bekommt `unit` mal sein Gewicht an Hoehe. Passt das nicht auf den
 * Bildschirm (kleines Handy, grosse Schrift), scrollt die Seite als Rueckfall.
 *
 * @param weightSum Summe der Gewichte aller Eintraege (ein Eintrag mit Hoehe `unit` zaehlt 1)
 * @param gaps Anzahl der Abstaende zwischen den Eintraegen
 * @param fixedHeight Hoehe von Inhalten, die nicht mitwachsen (z. B. Zwischenueberschriften)
 * @param minUnit kleinste erlaubte Hoehe pro Gewichtseinheit
 */
@Composable
fun ClearFillScreen(
    title: String,
    weightSum: Float,
    gaps: Int,
    itemSpacing: Dp,
    fixedHeight: Dp,
    minUnit: Dp,
    content: @Composable ColumnScope.(unit: Dp) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val titleHeight = with(LocalDensity.current) {
            MaterialTheme.typography.headlineMedium.lineHeight.toDp()
        }
        // Oben und unten je ein Seitenrand, dazu Titel und der Abstand unter dem Titel
        val available = maxHeight - ClearSpacing.medium * 2 - titleHeight - ClearSpacing.medium
        val unit = ((available - fixedHeight - itemSpacing * gaps) / weightSum)
            .coerceAtLeast(minUnit)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            Column(verticalArrangement = Arrangement.spacedBy(itemSpacing)) {
                content(unit)
            }
        }
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Zurück", "Back"))
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

/**
 * Runde Karte ohne Schatten. Mit `onClick` wird sie antippbar. Ohne eigene Farbe hat sie die
 * Kartenfarbe und einen feinen Rand. Mit eigener Farbe (zum Beispiel ein Level) entfaellt der Rand.
 */
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
    val elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val border = if (containerColor == null) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    } else null
    val inner: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = ClearSpacing.medium),
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
            elevation = elevation,
            border = border,
            content = inner
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            elevation = elevation,
            border = border,
            content = inner
        )
    }
}

/**
 * Seite, auf der alle Eintraege garantiert ohne Scrollen auf den Bildschirm passen. Jeder Eintrag
 * bekommt mit `Modifier.weight(...)` seinen Anteil der freien Hoehe. `unit` ist die Hoehe, die ein
 * Eintrag mit Gewicht 1 bekommt. Die Eintraege entscheiden selbst, ob sie bei wenig Platz
 * kompakter sind.
 *
 * @param weightSum Summe aller Gewichte
 * @param gaps Anzahl der Abstaende zwischen den Eintraegen
 */
@Composable
fun ClearFitScreen(
    title: String,
    weightSum: Float,
    gaps: Int,
    gap: Dp = 8.dp,
    content: @Composable ColumnScope.(unit: Dp) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(Modifier.height(ClearSpacing.medium))
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val unit = (maxHeight - gap * gaps) / weightSum
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                content(unit)
            }
        }
    }
}
