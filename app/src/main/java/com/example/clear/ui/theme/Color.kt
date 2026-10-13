package com.example.clear.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// ---------- Helles Design (Startwerte) ----------
val LightBackground = Color(0xFFF7F6F0)      // warmes Off-White
val LightPrimary = Color(0xFF718A75)         // Salbeigruen
val LightTextDark = Color(0xFF34483D)        // dunkles Waldgruen
val LightTextSecondary = Color(0xFF667267)   // gedecktes Grau-Gruen
val LightCard = Color(0xFFFFFFFF)            // Karten
val LightSurfaceSoft = Color(0xFFE5EBDD)     // helles Salbeigruen
val LightWarning = Color(0xFFC77D5A)         // Terrakotta
val LightError = Color(0xFFB3574B)

// ---------- Dunkles Design ----------
val DarkBackground = Color(0xFF1E231F)
val DarkPrimary = Color(0xFF9DB8A1)
val DarkTextDark = Color(0xFFE6EBE3)
val DarkTextSecondary = Color(0xFFAEB9AF)
val DarkCard = Color(0xFF2A312C)
val DarkSurfaceSoft = Color(0xFF354238)
val DarkWarning = Color(0xFFE0A07F)
val DarkError = Color(0xFFE58F85)

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
