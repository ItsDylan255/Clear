package com.example.clear.ui.components

import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
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
import com.example.clear.tr
import com.example.clear.ui.theme.ClearSpacing

/**
 * Grundlayout jeder Hauptseite: Titel, optional eine kleine Zeile darunter (`subtitle`),
 * optionaler Einfuehrungstext und scrollbarer Inhalt.
 * Mit `bottomBar` bleibt ein Bereich fest am unteren Rand stehen, der Inhalt scrollt darueber.
 */
@Composable
fun ClearScreen(
    title: String,
    intro: String? = null,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ClearSpacing.large, vertical = ClearSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
                    actions()
                }
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (intro != null) {
                Text(
                    intro,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            content()
        }
        if (bottomBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ClearSpacing.large)
                    .padding(top = ClearSpacing.small, bottom = ClearSpacing.medium)
            ) { bottomBar() }
        }
    }
}

/**
 * Layout fuer Detailseiten: grosse Titelleiste mit Zurueck-Pfeil. Beim Scrollen klappt der
 * grosse Titel in die Leiste ein, so sieht man immer, wo man ist.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearDetailScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val background = MaterialTheme.colorScheme.background
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            LargeTopAppBar(
                title = {
                    // Aufgeklappt darf ein langer Titel zwei Zeilen haben, eingeklappt nur eine
                    Text(
                        title,
                        maxLines = if (scrollBehavior.state.collapsedFraction < 0.5f) 2 else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = tr("Zurück", "Back"))
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = background,
                    scrolledContainerColor = background
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ClearSpacing.large)
                .padding(top = ClearSpacing.small, bottom = ClearSpacing.large),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.medium),
            content = content
        )
    }
}

/**
 * Runde Karte ohne Schatten und ohne Rand, nur als ruhige Farbflaeche. Mit `onClick` wird sie
 * antippbar. Ohne eigene Farbe hat sie die Kartenfarbe.
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
    val inner: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = ClearSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(ClearSpacing.small),
            content = content
        )
    }
    if (onClick != null) {
        val interaction = remember { MutableInteractionSource() }
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().pressScale(interaction),
            interactionSource = interaction,
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            elevation = elevation,
            content = inner
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = colors,
            elevation = elevation,
            content = inner
        )
    }
}
