package com.example.clear.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3E9EF),
    onPrimaryContainer = Color(0xFF1E3347),
    secondary = Color(0xFF6A6560),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9E2),
    onSecondaryContainer = LightTextDark,
    tertiary = LightWarning,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextDark,
    surface = LightCard,
    onSurface = LightTextDark,
    surfaceVariant = LightSurfaceSoft,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFF8E8A83),
    outlineVariant = Color(0xFFE4E0D9),
    error = LightError,
    onError = Color.White,
    surfaceContainerLowest = LightCard,
    surfaceContainerLow = Color(0xFFFAF9F6),
    surfaceContainer = LightCard,
    surfaceContainerHigh = Color(0xFFF1EEE9),
    surfaceContainerHighest = Color(0xFFEAE6DF)
)

private val DarkScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF141517),
    primaryContainer = Color(0xFF303A44),     // einzige ruhige Akzentfarbe (Symbolkreise, Check-in-Karte)
    onPrimaryContainer = Color(0xFFD6E2EC),
    secondary = Color(0xFFB8B3AA),
    onSecondary = Color(0xFF14202B),
    secondaryContainer = DarkSurfaceSoft,
    onSecondaryContainer = DarkTextDark,
    tertiary = DarkWarning,
    onTertiary = Color(0xFF2B1B17),
    background = DarkBackground,
    onBackground = DarkTextDark,
    surface = DarkCard,
    onSurface = DarkTextDark,
    surfaceVariant = DarkSurfaceSoft,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF77736C),
    outlineVariant = Color(0xFF2C2F33),
    error = DarkError,
    onError = Color(0xFF2B1B17),
    surfaceContainerLowest = Color(0xFF0F1012),
    surfaceContainerLow = Color(0xFF191A1D),
    surfaceContainer = DarkCard,
    surfaceContainerHigh = Color(0xFF25282B),
    surfaceContainerHighest = Color(0xFF2D3034)
)

// Weiche, grosse Rundungen. Buttons (large) sind bei 56 dp Hoehe voll gerundet.
val ClearShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Einheitliche Abstaende
object ClearSpacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
}

// Ueberschriften in einer Serifenschrift (eingebaut, kein Download), Text in der ruhigen Systemschrift.
// Etwas mehr Zeilenabstand, wenig Fettdruck.
private val Display = FontFamily.Serif

val ClearTypography = Typography(
    displaySmall = TextStyle(fontFamily = Display, fontSize = 34.sp, lineHeight = 42.sp, fontWeight = FontWeight.Normal),
    headlineLarge = TextStyle(fontFamily = Display, fontSize = 32.sp, lineHeight = 40.sp, fontWeight = FontWeight.Normal),
    headlineMedium = TextStyle(fontFamily = Display, fontSize = 30.sp, lineHeight = 38.sp, fontWeight = FontWeight.Normal),
    headlineSmall = TextStyle(fontFamily = Display, fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Normal),
    titleLarge = TextStyle(fontFamily = Display, fontSize = 22.sp, lineHeight = 30.sp, fontWeight = FontWeight.Normal),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontSize = 15.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 21.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
    // Kleine Abschnittstitel ("SCHNELL", "FÜR DICH"): gesperrt, ruhig
    labelSmall = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.4.sp)
)

@Composable
fun ClearTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        shapes = ClearShapes,
        typography = ClearTypography,
        content = content
    )
}
