package com.example.clear.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// ---------- Helles Design: warmes Off-White wie zuvor, Akzente neutral ----------
val LightBackground = Color(0xFFF7F6F0)      // warmes Off-White (wie zuvor)
val LightPrimary = Color(0xFF4A4A4C)         // weiches Anthrazit
val LightTextDark = Color(0xFF2B2B2D)        // weiches Schwarz (kein reines Schwarz)
val LightTextSecondary = Color(0xFF6B6A66)   // warmes mittleres Grau
val LightCard = Color(0xFFFFFFFF)            // Karten
val LightSurfaceSoft = Color(0xFFEAE9E3)     // helles warmes Grau
val LightWarning = Color(0xFFC77D5A)         // Terrakotta (bleibt als Warnfarbe)
val LightError = Color(0xFFB3574B)

// ---------- Dunkles Design: weiches, nicht zu dunkles Anthrazit ----------
val DarkBackground = Color(0xFF2B2B2D)
val DarkPrimary = Color(0xFFD6D6D8)
val DarkTextDark = Color(0xFFEDEDEE)
val DarkTextSecondary = Color(0xFFB4B4B8)
val DarkCard = Color(0xFF38383B)
val DarkSurfaceSoft = Color(0xFF454548)
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
