package com.example.clear.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// ---------- Helles Design: kuehles Weiss, sanftes Blau, ruhiges Grau ----------
val LightBackground = Color(0xFFF5F7FA)      // kuehles Off-White
val LightPrimary = Color(0xFF3F6C9A)         // gedecktes Blau (Kontrast zu Weiss 5,5:1)
val LightTextDark = Color(0xFF1F2A37)        // weiches Blau-Schwarz
val LightTextSecondary = Color(0xFF566270)   // mittleres Blau-Grau
val LightCard = Color(0xFFFFFFFF)            // Karten
val LightSurfaceSoft = Color(0xFFE8EEF4)     // helle blaugraue Flaeche
val LightWarning = Color(0xFFBE6B5C)         // gedaempftes Terrakotta
val LightError = Color(0xFFB3574B)

// Level-Farben (hell): Niedrig = Rose, Mittel = Sand, Hoch = Salbei
val LightLevelLow = Color(0xFFF4DAD6)
val LightLevelMedium = Color(0xFFF1E6CF)
val LightLevelHigh = Color(0xFFDDEBE4)

// ---------- Dunkles Design: tiefes Blaugrau, nie reines Schwarz ----------
val DarkBackground = Color(0xFF12171C)
val DarkPrimary = Color(0xFF8FB5DD)
val DarkTextDark = Color(0xFFE6EBF0)
val DarkTextSecondary = Color(0xFFA7B2BE)
val DarkCard = Color(0xFF1B2229)
val DarkSurfaceSoft = Color(0xFF26303A)
val DarkWarning = Color(0xFFE2A092)
val DarkError = Color(0xFFE58F85)

// Level-Farben (dunkel)
val DarkLevelLow = Color(0xFF4A2E2E)
val DarkLevelMedium = Color(0xFF40392A)
val DarkLevelHigh = Color(0xFF263B33)

/** Ist das aktuelle Design dunkel? */
@Composable
fun isClearDark(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

// Diese Namen benutzt der Rest der App. Sie liefern automatisch die Farbe des aktiven Designs.
val ClearBackground: Color @Composable get() = if (isClearDark()) DarkBackground else LightBackground
val ClearPrimary: Color @Composable get() = if (isClearDark()) DarkPrimary else LightPrimary
val ClearTextDark: Color @Composable get() = if (isClearDark()) DarkTextDark else LightTextDark
val ClearTextSecondary: Color @Composable get() = if (isClearDark()) DarkTextSecondary else LightTextSecondary
val ClearCard: Color @Composable get() = if (isClearDark()) DarkCard else LightCard
val ClearSurfaceSoft: Color @Composable get() = if (isClearDark()) DarkSurfaceSoft else LightSurfaceSoft
val ClearWarning: Color @Composable get() = if (isClearDark()) DarkWarning else LightWarning
val ClearError: Color @Composable get() = if (isClearDark()) DarkError else LightError

/** Level-Farben fuer Bereiche und Hinweise. Wechseln mit Hell und Dunkel. */
val ClearLevelLow: Color @Composable get() = if (isClearDark()) DarkLevelLow else LightLevelLow
val ClearLevelMedium: Color @Composable get() = if (isClearDark()) DarkLevelMedium else LightLevelMedium
val ClearLevelHigh: Color @Composable get() = if (isClearDark()) DarkLevelHigh else LightLevelHigh

/** Sanfte Warnflaeche fuer Hinweise und die Notfallkarte (gleiche Farbe wie Level Niedrig). */
val ClearNoticeSoft: Color @Composable get() = ClearLevelLow

// Kraeftigere Punkte fuer die Level (nur kleine Flaechen wie der Punkt neben dem Level-Text)
val ClearLevelLowAccent: Color @Composable get() = if (isClearDark()) Color(0xFFE59A8C) else Color(0xFFB0594B)
val ClearLevelMediumAccent: Color @Composable get() = if (isClearDark()) Color(0xFFE0B55E) else Color(0xFF9A6C0E)
val ClearLevelHighAccent: Color @Composable get() = if (isClearDark()) Color(0xFF7DBF9B) else Color(0xFF3F7D5E)
